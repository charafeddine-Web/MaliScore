package ui;
import model.Credit;
import model.Echeance;
import model.Incident;
import model.Personne;
import model.enums.StatutPaiement;
import model.enums.TypeIncident;
import service.ClientService;
import service.CreditService;
import service.EcheanceService;
import service.IncidentService;
import service.ScoringService;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Scanner;

public class MenuPayment {
    private CreditService creditService;
    private EcheanceService echeanceService;
    private ClientService clientService;
    private ScoringService scoringService;
    private IncidentService incidentService;
    private Scanner scanner;

    public MenuPayment() {
        this.creditService = new CreditService();
        this.echeanceService = new EcheanceService();
        this.clientService = new ClientService();
        this.scoringService = new ScoringService();
        this.incidentService = new IncidentService();
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        int choix;
        do {
            System.out.println("\n=== MENU GESTION DES PAIEMENTS ===");
            System.out.println("1. Enregistrer un paiement");
            System.out.println("2. Consulter les échéances d'un crédit");
            System.out.println("3. Consulter les échéances d'un client");
            System.out.println("4. Échéances en retard");
            System.out.println("5. Échéances impayées");
            System.out.println("6. Recalculer le score d'un client");
            System.out.println("7. Retour au menu principal");
            System.out.print("Votre choix : ");
            
            try {
                choix = scanner.nextInt();
                scanner.nextLine();
                
                switch (choix) {
                    case 1:
                        enregistrerPaiement();
                        break;
                    case 2:
                        consulterEcheancesCredit();
                        break;
                
                    case 3:
                        consulterEcheancesClient();
                        break;
                    case 4:
                        afficherEcheancesRetard();
                        break;
                    case 5:
                        afficherEcheancesImpayees();
                        break;
                    case 6:
                        recalculerScore();
                        break;
                    case 7:
                        System.out.println("Retour au menu principal...");
                        break;
                    default:
                        System.out.println("Choix invalide !");
                }
            } catch (Exception e) {
                System.out.println("Erreur de saisie. Veuillez entrer un nombre valide.");
                scanner.nextLine();
                choix = 0;
            }
            
            if (choix != 7) {
                System.out.println("\nAppuyez sur Entrée pour continuer...");
                scanner.nextLine();
            }
            
        } while (choix != 7);
    }

    private void enregistrerPaiement() {
        System.out.println("\n=== ENREGISTRER UN PAIEMENT ===");
        
        System.out.print("ID de l'échéance à payer : ");
        long echeanceId = scanner.nextLong();
        scanner.nextLine();
        
        Echeance echeance = echeanceService.getEcheanceById(echeanceId);
        if (echeance == null) {
            System.out.println("Echeance introuvable !");
            return;
        }
        
        System.out.println("\n--- DETAILS DE L'ECHEANCE ---");
        System.out.println("Date d'échéance: " + echeance.getDateEcheance());
        System.out.println("Montant: " + String.format("%.2f", echeance.getMensualite()) + " DH");
        System.out.println("Statut actuel: " + echeance.getStatutPaiement());
        
        System.out.print("\nDate de paiement (yyyy-mm-dd) [Entrée pour aujourd'hui]: ");
        String dateStr = scanner.nextLine();
        
        LocalDate datePaiement;
        if (dateStr.trim().isEmpty()) {
            datePaiement = LocalDate.now();
        } else {
            try {
                datePaiement = LocalDate.parse(dateStr);
            } catch (Exception e) {
                System.out.println("Date invalide !");
                return;
            }
        }
        
        System.out.print("Montant paye : ");
        String montantStr = scanner.nextLine().trim().replace(',', '.');
        double montantPaye;
        try {
            montantPaye = Double.parseDouble(montantStr);
        } catch (NumberFormatException ex) {
            System.out.println("Erreur de saisie. Veuillez entrer un nombre valide.");
            return;
        }
        
        if (montantPaye <= 0) {
            System.out.println("Montant invalide !");
            return;
        }
        
        echeance.setDatePaiement(datePaiement);
        long joursRetard = ChronoUnit.DAYS.between(echeance.getDateEcheance(), datePaiement);
        
        TypeIncident typeIncident;
        int scoreImpact;
        
        if (joursRetard <= 0) {
            echeance.setStatutPaiement(StatutPaiement.PAYE_A_TEMPS);
            typeIncident = TypeIncident.PAYE_A_TEMPS;
            scoreImpact = 0;
            System.out.println("Paiement a temps enregistre");
        } else if (joursRetard <= 30) {
            echeance.setStatutPaiement(StatutPaiement.PAYE_EN_RETARD);
            typeIncident = TypeIncident.PAYE_EN_RETARD;
            scoreImpact = -3;
            System.out.println("Paiement en retard enregistre (" + joursRetard + " jours de retard)");
        } else {
            echeance.setStatutPaiement(StatutPaiement.IMPAYE_REGLE);
            typeIncident = TypeIncident.IMPAYE_REGLE;
            scoreImpact = -10;
            System.out.println("Impaye regle enregistre (" + joursRetard + " jours de retard)");
        }
        
        echeanceService.updateEcheance(echeance);
        
        Incident incident = new Incident();
        incident.setEcheance(echeance);
        incident.setDateIncident(datePaiement);
        incident.setTypeIncident(typeIncident);
        incident.setScoreImpact(scoreImpact);
        
        boolean incidentCree = incidentService.addIncident(incident);
        if (incidentCree) {
            System.out.println("Incident cree automatiquement (ID: " + incident.getId() + ", Impact: " + scoreImpact + ")");
        }
        
        Credit credit = creditService.findCreditById(echeance.getCreditId());
        if (credit != null) {
            Personne client = clientService.findClient(credit.getPersonneId().intValue());
            if (client != null) {
                double nouveauScore = scoringService.calculerScore(client);
                System.out.println("Score du client recalcule: " + nouveauScore);
            }
        }
        
        double diff = Math.abs(montantPaye - echeance.getMensualite());
        if (diff > 0.05) {
            System.out.println("Attention: Le montant paye (" + String.format("%.2f", montantPaye) + " DH) ne correspond pas a la mensualite (" + String.format("%.2f", echeance.getMensualite()) + " DH)");
        }
    }

    private void consulterEcheancesCredit() {
        System.out.println("\n=== CONSULTER LES ECHEANCES D'UN CREDIT ===");
        
        System.out.print("ID du crédit : ");
        long creditId = scanner.nextLong();
        scanner.nextLine();
        
        Credit credit = creditService.findCreditById(creditId);
        if (credit == null) {
            System.out.println("Credit introuvable !");
            return;
        }
        
        Personne client = clientService.findClient(credit.getPersonneId().intValue());
        System.out.println("\n--- CRÉDIT ID: " + creditId + " ---");
        System.out.println("Client: " + (client != null ? client.getNom() + " " + client.getPrenom() : "Inconnu"));
        System.out.println("Montant octroyé: " + credit.getMontantOctroye() + " DH");
        System.out.println("Durée: " + credit.getDureeEnMois() + " mois");
        
        List<Echeance> echeances = echeanceService.getByCreditId(creditId);

        if (echeances == null || echeances.isEmpty()) {
            System.out.println("\nAucune echeance enregistree pour ce credit.");
            return;
        }

        System.out.println("\nECHEANCES:");
        System.out.println("┌─────┬──────────────┬──────────────┬──────────────┬──────────────────┐");
        System.out.println("│ ID  │ Date echeance│ Date paiement│ Mensualite   │ Statut           │");
        System.out.println("├─────┼──────────────┼──────────────┼──────────────┼──────────────────┤");
        
        for (Echeance e : echeances) {
            String datePaiement = (e.getDatePaiement() == null) ? "En attente" : e.getDatePaiement().toString();
            System.out.printf("│ %-3d │ %-12s │ %-12s │ %-12.2f │ %-16s │\n", 
                    e.getId(),
                    e.getDateEcheance(),
                    datePaiement,
                    e.getMensualite(),
                    e.getStatutPaiement());
        }
        
        System.out.println("└─────┴──────────────┴──────────────┴──────────────┴──────────────────┘");
    }

    private void consulterEcheancesClient() {
        System.out.println("\n=== CONSULTER LES ECHEANCES D'UN CLIENT ===");
        
        System.out.print("ID du client : ");
        int clientId = scanner.nextInt();
        scanner.nextLine();
        
        Personne client = clientService.findClient(clientId);
        if (client == null) {
            System.out.println("Client introuvable !");
            return;
        }
        
        System.out.println("\n--- CLIENT: " + client.getNom() + " " + client.getPrenom() + " ---");
        System.out.println("Score actuel: " + client.getScore());
        
        List<Credit> credits = creditService.findAllCredit().stream()
                .filter(c -> c.getPersonneId().equals(client.getId()))
                .collect(java.util.stream.Collectors.toList());
        
        if (credits.isEmpty()) {
            System.out.println("Aucun crédit trouvé pour ce client.");
            return;
        }
        
        System.out.println("\nCREDITS DU CLIENT:");
        for (Credit credit : credits) {
            System.out.println("\n--- Credit ID: " + credit.getId() + " ---");
            System.out.println("Type: " + credit.getTypeCredit());
            System.out.println("Montant: " + String.format("%.2f", credit.getMontantOctroye()) + " DH");
            System.out.println("Decision: " + credit.getDecision());
            System.out.println("Date: " + credit.getDateCredit());

            List<Echeance> echeances = echeanceService.getByCreditId(credit.getId());
            if (echeances == null || echeances.isEmpty()) {
                System.out.println("Aucune echeance enregistree pour ce credit.");
                continue;
            }

            System.out.println("ECHEANCES:");
            System.out.println("┌─────┬──────────────┬──────────────┬──────────────┬──────────────────┐");
            System.out.println("│ ID  │ Date echeance│ Date paiement│ Mensualite   │ Statut           │");
            System.out.println("├─────┼──────────────┼──────────────┼──────────────┼──────────────────┤");
            for (Echeance e : echeances) {
                String datePaiement = (e.getDatePaiement() == null) ? "En attente" : e.getDatePaiement().toString();
                System.out.printf("│ %-3d │ %-12s │ %-12s │ %-12.2f │ %-16s │\n",
                        e.getId(),
                        e.getDateEcheance(),
                        datePaiement,
                        e.getMensualite(),
                        e.getStatutPaiement());
            }
            System.out.println("└─────┴──────────────┴──────────────┴──────────────┴──────────────────┘");
        }
    }

    private void afficherEcheancesRetard() {
        System.out.println("\n=== ECHEANCES EN RETARD ===");
        
        List<Echeance> toutesEcheances = echeanceService.getAllEcheances();
        List<Echeance> echeancesEnRetard = toutesEcheances.stream()
                .filter(e -> e.getDateEcheance().isBefore(LocalDate.now()))
                .filter(e -> e.getStatutPaiement() != StatutPaiement.PAYE_A_TEMPS)
                .filter(e -> e.getStatutPaiement() != StatutPaiement.PAYE_EN_RETARD)
                .filter(e -> e.getStatutPaiement() != StatutPaiement.IMPAYE_REGLE)
                .collect(java.util.stream.Collectors.toList());
        
        if (echeancesEnRetard.isEmpty()) {
            System.out.println("Aucune echeance en retard trouvee.");
            return;
        }
        
        System.out.println("Nombre d'echeances en retard: " + echeancesEnRetard.size());
        System.out.println("┌─────┬──────────────┬──────────────┬──────────────┬──────────────────┬──────────────────┐");
        System.out.println("│ ID  │ Date echeance│ Date paiement│ Mensualite   │ Statut           │ Jours de retard  │");
        System.out.println("├─────┼──────────────┼──────────────┼──────────────┼──────────────────┼──────────────────┤");
        
        for (Echeance e : echeancesEnRetard) {
            long joursRetard = ChronoUnit.DAYS.between(e.getDateEcheance(), LocalDate.now());
            String datePaiement = (e.getDatePaiement() == null) ? "Non paye" : e.getDatePaiement().toString();
            
            System.out.printf("│ %-3d │ %-12s │ %-12s │ %-12.2f │ %-16s │ %-16d │\n",
                    e.getId(),
                    e.getDateEcheance(),
                    datePaiement,
                    e.getMensualite(),
                    e.getStatutPaiement().name(),
                    joursRetard);
        }
        System.out.println("└─────┴──────────────┴──────────────┴──────────────┴──────────────────┴──────────────────┘");
    }

    private void afficherEcheancesImpayees() {
        System.out.println("\n=== ECHEANCES IMPAYEES ===");
        
        List<Echeance> toutesEcheances = echeanceService.getAllEcheances();
        List<Echeance> echeancesImpayees = toutesEcheances.stream()
                .filter(e -> e.getStatutPaiement() == StatutPaiement.IMPAYE_NON_REGLE)
                .collect(java.util.stream.Collectors.toList());
        
        if (echeancesImpayees.isEmpty()) {
            System.out.println("Aucune echeance impayee trouvee.");
            return;
        }
        
        System.out.println("Nombre d'echeances impayees: " + echeancesImpayees.size());
        System.out.println("┌─────┬──────────────┬──────────────┬──────────────┬──────────────────┐");
        System.out.println("│ ID  │ Date echeance│ Date paiement│ Mensualite   │ Jours de retard  │");
        System.out.println("├─────┼──────────────┼──────────────┼──────────────┼──────────────────┤");
        
        for (Echeance e : echeancesImpayees) {
            long joursRetard = ChronoUnit.DAYS.between(e.getDateEcheance(), LocalDate.now());
            String datePaiement = (e.getDatePaiement() == null) ? "Non paye" : e.getDatePaiement().toString();
            
            System.out.printf("│ %-3d │ %-12s │ %-12s │ %-12.2f │ %-16d │\n",
                    e.getId(),
                    e.getDateEcheance(),
                    datePaiement,
                    e.getMensualite(),
                    joursRetard);
        }
        System.out.println("└─────┴──────────────┴──────────────┴──────────────┴──────────────────┘");
        
        double montantTotalImpaye = echeancesImpayees.stream()
                .mapToDouble(Echeance::getMensualite)
                .sum();
        System.out.println("\nMontant total impaye: " + String.format("%.2f", montantTotalImpaye) + " DH");
    }

    private void recalculerScore() {
        System.out.println("\n=== RECALCULER LE SCORE D'UN CLIENT ===");
        
        System.out.print("ID du client : ");
        int clientId = scanner.nextInt();
        scanner.nextLine();
        
        Personne client = clientService.findClient(clientId);
        if (client == null) {
            System.out.println("Client introuvable !");
            return;
        }
        
        System.out.println("\n--- AVANT RECALCUL ---");
        System.out.println("Client: " + client.getNom() + " " + client.getPrenom());
        System.out.println("Score actuel: " + client.getScore());
        
        double ancienScore = client.getScore();
        double nouveauScore = scoringService.calculerScore(client);
        
        System.out.println("\n--- APRÈS RECALCUL ---");
        System.out.println("Ancien score: " + ancienScore);
        System.out.println("Nouveau score: " + nouveauScore);
        
        double difference = nouveauScore - ancienScore;
        if (difference > 0) {
            System.out.println("Amelioration: +" + String.format("%.1f", difference) + " points");
        } else if (difference < 0) {
            System.out.println("Deterioration: " + String.format("%.1f", difference) + " points");
        } else {
            System.out.println("Score inchange");
        }
        
        afficherComposantsScore(client);
    }

    private void afficherComposantsScore(Personne client) {
        System.out.println("\n--- COMPOSANTS DU SCORE ---");
        
        System.out.println("Composants du scoring:");
        System.out.println("   • Stabilité professionnelle (30 pts max)");
        System.out.println("   • Capacité financière (30 pts max)");
        System.out.println("   • Historique de paiement (15 pts max)");
        System.out.println("   • Relation client (10 pts max)");
        System.out.println("   • Critères complémentaires (10 pts max)");
        System.out.println("   • Total: " + client.getScore() + "/100");
        
        String interpretation = "";
        if (client.getScore() >= 80) {
            interpretation = "Excellent - Accord immédiat";
        } else if (client.getScore() >= 60) {
            interpretation = "Acceptable - Étude manuelle";
        } else {
            interpretation = "Insuffisant - Refus automatique";
        }
        
        System.out.println("Interpretation: " + interpretation);
    }
}
