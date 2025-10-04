package ui;

import model.Credit;
import model.Echeance;
import model.Personne;
import model.enums.StatutPaiement;
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
    private Scanner scanner;

    public MenuPayment() {
        this.creditService = new CreditService();
        this.echeanceService = new EcheanceService();
        this.clientService = new ClientService();
        this.scoringService = new ScoringService();
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
            System.out.println("❌ Échéance introuvable !");
            return;
        }
        
        System.out.println("\n--- DÉTAILS DE L'ÉCHÉANCE ---");
        System.out.println("Date d'échéance: " + echeance.getDateEcheance());
        System.out.println("Montant: " + echeance.getMensualite() + " DH");
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
                System.out.println("❌ Date invalide !");
                return;
            }
        }
        
        System.out.print("Montant payé : ");
        double montantPaye = scanner.nextDouble();
        scanner.nextLine();
        
        if (montantPaye <= 0) {
            System.out.println("❌ Montant invalide !");
            return;
        }
        
        // Enregistrer le paiement
        echeance.setDatePaiement(datePaiement);
        
        // Déterminer le statut du paiement
        long joursRetard = ChronoUnit.DAYS.between(echeance.getDateEcheance(), datePaiement);
        
        if (joursRetard <= 0) {
            echeance.setStatutPaiement(StatutPaiement.PAYEATEMPS);
            System.out.println("✅ Paiement à temps enregistré");
        } else if (joursRetard <= 30) {
            echeance.setStatutPaiement(StatutPaiement.PAYEENRETARD);
            System.out.println("⚠️ Paiement en retard enregistré (" + joursRetard + " jours de retard)");
        } else {
            echeance.setStatutPaiement(StatutPaiement.IMPAYEREGLE);
            System.out.println("⚠️ Impayé réglé enregistré (" + joursRetard + " jours de retard)");
        }
        
        echeanceService.updateEcheance(echeance);
        
        // Recalculer le score du client
        Credit credit = creditService.findCreditById(echeance.getCreditId());
        if (credit != null) {
            Personne client = clientService.findClient(credit.getPersonneId().intValue());
            if (client != null) {
                double nouveauScore = scoringService.calculerScore(client);
                System.out.println("📊 Score du client recalculé: " + nouveauScore);
            }
        }
        
        // Vérifier si le montant correspond
        if (Math.abs(montantPaye - echeance.getMensualite()) > 0.01) {
            System.out.println("⚠️ Attention: Le montant payé (" + montantPaye + " DH) ne correspond pas à la mensualité (" + echeance.getMensualite() + " DH)");
        }
    }

    private void consulterEcheancesCredit() {
        System.out.println("\n=== CONSULTER LES ÉCHÉANCES D'UN CRÉDIT ===");
        
        System.out.print("ID du crédit : ");
        long creditId = scanner.nextLong();
        scanner.nextLine();
        
        Credit credit = creditService.findCreditById(creditId);
        if (credit == null) {
            System.out.println("❌ Crédit introuvable !");
            return;
        }
        
        Personne client = clientService.findClient(credit.getPersonneId().intValue());
        System.out.println("\n--- CRÉDIT ID: " + creditId + " ---");
        System.out.println("Client: " + (client != null ? client.getNom() + " " + client.getPrenom() : "Inconnu"));
        System.out.println("Montant octroyé: " + credit.getMontantOctroye() + " DH");
        System.out.println("Durée: " + credit.getDureeEnMois() + " mois");
        
        // Récupérer les échéances (simulation - normalement via EcheanceRepository)
        System.out.println("\n📅 ÉCHÉANCES:");
        System.out.println("┌─────┬──────────────┬──────────────┬──────────────┬──────────────────┐");
        System.out.println("│ ID  │ Date échéance│ Date paiement│ Mensualité   │ Statut           │");
        System.out.println("├─────┼──────────────┼──────────────┼──────────────┼──────────────────┤");
        
        // Note: Dans une vraie implémentation, on récupérerait les échéances depuis la base
        // Ici on simule avec quelques échéances
        for (int i = 1; i <= Math.min(5, credit.getDureeEnMois()); i++) {
            LocalDate dateEcheance = credit.getDateCredit().plusMonths(i);
            System.out.printf("│ %-3d │ %-12s │ %-12s │ %-12.2f │ %-16s │\n", 
                            i, dateEcheance, "En attente", credit.getMontantOctroye()/credit.getDureeEnMois(), "EN_ATTENTE");
        }
        
        if (credit.getDureeEnMois() > 5) {
            System.out.println("│ ... │ ...          │ ...          │ ...          │ ...              │");
        }
        
        System.out.println("└─────┴──────────────┴──────────────┴──────────────┴──────────────────┘");
    }

    private void consulterEcheancesClient() {
        System.out.println("\n=== CONSULTER LES ÉCHÉANCES D'UN CLIENT ===");
        
        System.out.print("ID du client : ");
        int clientId = scanner.nextInt();
        scanner.nextLine();
        
        Personne client = clientService.findClient(clientId);
        if (client == null) {
            System.out.println("❌ Client introuvable !");
            return;
        }
        
        System.out.println("\n--- CLIENT: " + client.getNom() + " " + client.getPrenom() + " ---");
        System.out.println("Score actuel: " + client.getScore());
        
        // Récupérer les crédits du client
        List<Credit> credits = creditService.findAllCredit().stream()
                .filter(c -> c.getPersonneId().equals(client.getId()))
                .collect(java.util.stream.Collectors.toList());
        
        if (credits.isEmpty()) {
            System.out.println("❌ Aucun crédit trouvé pour ce client.");
            return;
        }
        
        System.out.println("\n📊 CRÉDITS DU CLIENT:");
        for (Credit credit : credits) {
            System.out.println("\n--- Crédit ID: " + credit.getId() + " ---");
            System.out.println("Type: " + credit.getTypeCredit());
            System.out.println("Montant: " + credit.getMontantOctroye() + " DH");
            System.out.println("Décision: " + credit.getDecision());
            System.out.println("Date: " + credit.getDateCredit());
        }
    }

    private void afficherEcheancesRetard() {
        System.out.println("\n=== ÉCHÉANCES EN RETARD ===");
        
        // Simulation - dans une vraie implémentation, on interrogerait la base
        System.out.println("🔍 Recherche des échéances en retard...");
        System.out.println("❌ Fonctionnalité à implémenter avec la base de données");
        System.out.println("   → Interroger la table echeance");
        System.out.println("   → Filtrer par statut ENRETARD et date < aujourd'hui");
        System.out.println("   → Afficher les détails des échéances");
    }

    private void afficherEcheancesImpayees() {
        System.out.println("\n=== ÉCHÉANCES IMPAYÉES ===");
        
        // Simulation - dans une vraie implémentation, on interrogerait la base
        System.out.println("🔍 Recherche des échéances impayées...");
        System.out.println("❌ Fonctionnalité à implémenter avec la base de données");
        System.out.println("   → Interroger la table echeance");
        System.out.println("   → Filtrer par statut IMPAYENONREGLE");
        System.out.println("   → Afficher les détails des échéances");
        System.out.println("   → Calculer les jours de retard");
    }

    private void recalculerScore() {
        System.out.println("\n=== RECALCULER LE SCORE D'UN CLIENT ===");
        
        System.out.print("ID du client : ");
        int clientId = scanner.nextInt();
        scanner.nextLine();
        
        Personne client = clientService.findClient(clientId);
        if (client == null) {
            System.out.println("❌ Client introuvable !");
            return;
        }
        
        System.out.println("\n--- AVANT RECALCUL ---");
        System.out.println("Client: " + client.getNom() + " " + client.getPrenom());
        System.out.println("Score actuel: " + client.getScore());
        
        // Recalculer le score
        double ancienScore = client.getScore();
        double nouveauScore = scoringService.calculerScore(client);
        
        System.out.println("\n--- APRÈS RECALCUL ---");
        System.out.println("Ancien score: " + ancienScore);
        System.out.println("Nouveau score: " + nouveauScore);
        
        double difference = nouveauScore - ancienScore;
        if (difference > 0) {
            System.out.println("📈 Amélioration: +" + String.format("%.1f", difference) + " points");
        } else if (difference < 0) {
            System.out.println("📉 Détérioration: " + String.format("%.1f", difference) + " points");
        } else {
            System.out.println("➡️ Score inchangé");
        }
        
        // Afficher les composants du score
        afficherComposantsScore(client);
    }

    private void afficherComposantsScore(Personne client) {
        System.out.println("\n--- COMPOSANTS DU SCORE ---");
        
        // Note: Dans une vraie implémentation, on pourrait décomposer le calcul
        System.out.println("🔧 Composants du scoring:");
        System.out.println("   • Stabilité professionnelle (30 pts max)");
        System.out.println("   • Capacité financière (30 pts max)");
        System.out.println("   • Historique de paiement (15 pts max)");
        System.out.println("   • Relation client (10 pts max)");
        System.out.println("   • Critères complémentaires (10 pts max)");
        System.out.println("   • Total: " + client.getScore() + "/100");
        
        // Interprétation du score
        String interpretation = "";
        if (client.getScore() >= 80) {
            interpretation = "Excellent - Accord immédiat";
        } else if (client.getScore() >= 60) {
            interpretation = "Acceptable - Étude manuelle";
        } else {
            interpretation = "Insuffisant - Refus automatique";
        }
        
        System.out.println("📊 Interprétation: " + interpretation);
    }
}
