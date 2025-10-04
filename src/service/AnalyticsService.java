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
     * Clients à risque nécessitant suivi (top 10)
     * Critères: Score < 60, incidents récents < 6 mois, triés par score décroissant
     */
    public List<Personne> getClientsARisque() {
        List<Personne> allClients = clientRepository.findAll();
        LocalDate sixMonthsAgo = LocalDate.now().minusMonths(6);
        
        return allClients.stream()
                .filter(client -> {
                    if (client.getScore() >= 60) return false;
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
     * Statistiques générales du portefeuille
     */
    public Map<String, Object> getStatistiquesGenerales() {
        List<Personne> allClients = clientRepository.findAll();
        List<Credit> allCredits = creditRepository.findAll();
        
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalClients", allClients.size());
        long employes = allClients.stream().filter(c -> c instanceof Employe).count();
        long professionnels = allClients.stream().filter(c -> c instanceof Professionnel).count();
        stats.put("employes", employes);
        stats.put("professionnels", professionnels);
        double scoreMoyen = allClients.stream().mapToDouble(Personne::getScore).average().orElse(0.0);
        stats.put("scoreMoyen", Math.round(scoreMoyen * 10.0) / 10.0);
        
        stats.put("totalCredits", allCredits.size());
        long accordsImmediats = allCredits.stream().filter(c -> c.getDecision() == model.enums.DecisionType.ACCORD_IMMEDIAT).count();
        long etudesManuelles = allCredits.stream().filter(c -> c.getDecision() == model.enums.DecisionType.ETUDE_MANUELLE).count();
        long refus = allCredits.stream().filter(c -> c.getDecision() == model.enums.DecisionType.REFUS_AUTOMATIQUE).count();
        
        stats.put("accordsImmediats", accordsImmediats);
        stats.put("etudesManuelles", etudesManuelles);
        stats.put("refus", refus);
        double montantTotal = allCredits.stream().mapToDouble(Credit::getMontantOctroye).sum();
        stats.put("montantTotalOctroye", Math.round(montantTotal));
        
        return stats;
    }
}
