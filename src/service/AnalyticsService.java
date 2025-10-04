package service;

import model.*;
import repository.ClientRepository;
import repository.CreditRepository;
import repository.EcheanceRepository;
import repository.IncidentRepository;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

public class AnalyticsService {
    private ClientRepository clientRepository;
    private CreditRepository creditRepository;
    private EcheanceRepository echeanceRepository;

    public AnalyticsService() {
        this.clientRepository = new ClientRepository();
        this.creditRepository = new CreditRepository();
        this.echeanceRepository = new EcheanceRepository();
    }

    /**
     * Recherche clients éligibles pour crédit immobilier
     * Critères: Age 25-50 ans, Revenus >4000DH/mois, CDI uniquement, Score >70, Marié
     */
    public List<Personne> getClientsEligiblesCreditImmobilier() {
        List<Personne> allClients = clientRepository.findAll();
        LocalDate now = LocalDate.now();
        
        return allClients.stream()
                .filter(client -> {
                    // Age entre 25 et 50 ans
                    int age = Period.between(client.getDateNaissance(), now).getYears();
                    if (age < 25 || age > 50) return false;
                    
                    // Score > 70
                    if (client.getScore() < 70) return false;
                    
                    // Situation familiale: Marié
                    if (!"marie".equalsIgnoreCase(client.getSituationFamiliale()) && 
                        !"marié".equalsIgnoreCase(client.getSituationFamiliale())) return false;
                    
                    // Employé avec CDI et revenus > 4000DH
                    if (client instanceof Employe) {
                        Employe emp = (Employe) client;
                        return emp.getSalaire() > 4000 && 
                               (emp.getTypeContrat() == model.enums.ContratType.CDI_PUBLIC ||
                                emp.getTypeContrat() == model.enums.ContratType.CDI_PRIVEE_GRANDE ||
                                emp.getTypeContrat() == model.enums.ContratType.CDI_PRIVEE_PME);
                    }
                    
                    // Professionnel avec revenus > 4000DH
                    if (client instanceof Professionnel) {
                        Professionnel prof = (Professionnel) client;
                        return prof.getRevenu() > 4000 && !prof.isAutoEntrepreneur();
                    }
                    
                    return false;
                })
                .collect(Collectors.toList());
    }

    /**
     * Clients à risque nécessitant suivi (top 10)
     * Critères: Score < 60, incidents récents < 6 mois, triés par score décroissant
     */
    public List<Personne> getClientsARisque() {
        List<Personne> allClients = clientRepository.findAll();
        LocalDate sixMonthsAgo = LocalDate.now().minusMonths(6);
        
        return allClients.stream()
                .filter(client -> {
                    // Score < 60
                    if (client.getScore() >= 60) return false;
                    
                    // Vérifier incidents récents
                    List<Credit> credits = creditRepository.findByPersonneId(client.getId());
                    for (Credit credit : credits) {
                        List<Echeance> echeances = echeanceRepository.findByCreditId(credit.getId());
                        boolean hasRecentIncidents = echeances.stream()
                                .anyMatch(e -> e.getDateEcheance().isAfter(sixMonthsAgo) && 
                                             (e.getStatutPaiement() == model.enums.StatutPaiement.EN_RETARD ||
                                              e.getStatutPaiement() == model.enums.StatutPaiement.IMPAYE_NON_REGLE));
                        if (hasRecentIncidents) return true;
                    }
                    return false;
                })
                .sorted(Comparator.comparing(Personne::getScore))
                .limit(10)
                .collect(Collectors.toList());
    }

    /**
     * Tri des clients par critères multiples
     */
    public List<Personne> getClientsTriesParCriteres() {
        return clientRepository.findAll().stream()
                .sorted(Comparator.comparing(Personne::getScore).reversed()
                        .thenComparing(p -> {
                            if (p instanceof Employe) return ((Employe) p).getSalaire();
                            if (p instanceof Professionnel) return ((Professionnel) p).getRevenu();
                            return 0.0;
                        }, Comparator.reverseOrder())
                        .thenComparing(p -> {
                            if (p.getCreatedAt() != null) {
                                return Period.between(p.getCreatedAt().toLocalDate(), LocalDate.now()).getYears();
                            }
                            return 0;
                        }, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    /**
     * Répartition par type d'emploi avec statistiques
     */
    public Map<String, Map<String, Object>> getRepartitionParTypeEmploi() {
        List<Personne> allClients = clientRepository.findAll();
        Map<String, Map<String, Object>> repartition = new HashMap<>();
        
        // Grouper par type de contrat
        Map<model.enums.ContratType, List<Employe>> employesParContrat = allClients.stream()
                .filter(client -> client instanceof Employe)
                .map(client -> (Employe) client)
                .collect(Collectors.groupingBy(Employe::getTypeContrat));
        
        for (Map.Entry<model.enums.ContratType, List<Employe>> entry : employesParContrat.entrySet()) {
            List<Employe> employes = entry.getValue();
            String contratType = entry.getKey().name();
            
            double scoreMoyen = employes.stream().mapToDouble(Personne::getScore).average().orElse(0.0);
            double revenusMoyens = employes.stream().mapToDouble(Employe::getSalaire).average().orElse(0.0);
            
            // Calculer taux d'approbation
            long totalCredits = employes.stream()
                    .mapToLong(emp -> creditRepository.findByPersonneId(emp.getId()).size())
                    .sum();
            long creditsApprouves = employes.stream()
                    .mapToLong(emp -> creditRepository.findByPersonneId(emp.getId()).stream()
                            .filter(c -> c.getDecision() == model.enums.DecisionType.ACCORD_IMMEDIAT)
                            .count())
                    .sum();
            
            double tauxApprobation = totalCredits > 0 ? (double) creditsApprouves / totalCredits * 100 : 0;
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("nombreClients", employes.size());
            stats.put("scoreMoyen", Math.round(scoreMoyen * 10.0) / 10.0);
            stats.put("revenusMoyens", Math.round(revenusMoyens));
            stats.put("tauxApprobation", Math.round(tauxApprobation) + "%");
            
            repartition.put(contratType, stats);
        }
        
        return repartition;
    }

    /**
     * Ciblage pour campagne publicitaire crédit consommation
     * Critères: Score 65-85, Revenus 4000-8000DH, Âge 28-45 ans, Pas de crédit en cours
     */
    public List<Personne> getCibleCampagneCreditConsommation() {
        List<Personne> allClients = clientRepository.findAll();
        LocalDate now = LocalDate.now();
        
        return allClients.stream()
                .filter(client -> {
                    // Score entre 65 et 85
                    if (client.getScore() < 65 || client.getScore() > 85) return false;
                    
                    // Âge entre 28 et 45 ans
                    int age = Period.between(client.getDateNaissance(), now).getYears();
                    if (age < 28 || age > 45) return false;
                    
                    // Revenus entre 4000 et 8000DH
                    double revenus = 0;
                    if (client instanceof Employe) {
                        revenus = ((Employe) client).getSalaire();
                    } else if (client instanceof Professionnel) {
                        revenus = ((Professionnel) client).getRevenu();
                    }
                    if (revenus < 4000 || revenus > 8000) return false;
                    
                    // Pas de crédit en cours
                    List<Credit> credits = creditRepository.findByPersonneId(client.getId());
                    boolean creditEnCours = credits.stream()
                            .anyMatch(c -> {
                                List<Echeance> echeances = echeanceRepository.findByCreditId(c.getId());
                                return echeances.stream().anyMatch(e -> e.getDatePaiement() == null);
                            });
                    
                    return !creditEnCours;
                })
                .collect(Collectors.toList());
    }

    /**
     * Statistiques générales du portefeuille
     */
    public Map<String, Object> getStatistiquesGenerales() {
        List<Personne> allClients = clientRepository.findAll();
        List<Credit> allCredits = creditRepository.findAll();
        
        Map<String, Object> stats = new HashMap<>();
        
        // Nombre total de clients
        stats.put("totalClients", allClients.size());
        
        // Répartition par type
        long employes = allClients.stream().filter(c -> c instanceof Employe).count();
        long professionnels = allClients.stream().filter(c -> c instanceof Professionnel).count();
        stats.put("employes", employes);
        stats.put("professionnels", professionnels);
        
        // Score moyen
        double scoreMoyen = allClients.stream().mapToDouble(Personne::getScore).average().orElse(0.0);
        stats.put("scoreMoyen", Math.round(scoreMoyen * 10.0) / 10.0);
        
        // Total crédits
        stats.put("totalCredits", allCredits.size());
        
        // Répartition des décisions
        long accordsImmediats = allCredits.stream().filter(c -> c.getDecision() == model.enums.DecisionType.ACCORD_IMMEDIAT).count();
        long etudesManuelles = allCredits.stream().filter(c -> c.getDecision() == model.enums.DecisionType.ETUDE_MANUELLE).count();
        long refus = allCredits.stream().filter(c -> c.getDecision() == model.enums.DecisionType.REFUS_AUTOMATIQUE).count();
        
        stats.put("accordsImmediats", accordsImmediats);
        stats.put("etudesManuelles", etudesManuelles);
        stats.put("refus", refus);
        
        // Montant total octroyé
        double montantTotal = allCredits.stream().mapToDouble(Credit::getMontantOctroye).sum();
        stats.put("montantTotalOctroye", Math.round(montantTotal));
        
        return stats;
    }
}
