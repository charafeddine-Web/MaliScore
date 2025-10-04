package ui;



import model.Credit;
import model.Personne;
import service.ClientService;
import service.CreditService;
import service.EcheanceService;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class MenuCredit {
    private CreditService creditService;
    private Scanner scanner;

    public MenuCredit() {
        this.creditService = new CreditService();
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        int choix;
        do {
            System.out.println("\n=== MENU CREDIT ===");
            System.out.println("1. Ajouter un crédit");
            System.out.println("2. Consulter un crédit");
            System.out.println("3. Modifier un crédit");
            System.out.println("4. Supprimer un crédit");
            System.out.println("5. Afficher tous les crédits");
            System.out.println("6. Quitter");
            System.out.print("Votre choix : ");
            choix = scanner.nextInt();
            scanner.nextLine();

            switch (choix) {
                case 1:
                    ajouterCredit();
                    break;
                case 2:
                    consulterCredit();
                    break;
                case 3:
                    modifierCredit();
                    break;
                case 4:
                    supprimerCredit();
                    break;
                case 5:
                    afficherTousCredits();
                    break;
                case 6:
                    System.out.println("Retour au menu principal...");
                    break;
                default:
                    System.out.println("Choix invalide !");
            }
        } while (choix != 6);
    }

    private void ajouterCredit() {
        System.out.println("\n=== DEMANDE DE CREDIT ===");
        
        System.out.print("ID du client : ");
        long personneId = scanner.nextLong();
        scanner.nextLine();

        ClientService clientService = new ClientService();
        Personne client = clientService.findClient((int) personneId);
        if (client == null) {
            System.out.println("❌ Client introuvable !");
            return;
        }

        System.out.println("\n--- INFORMATIONS CLIENT ---");
        System.out.println("Nom: " + client.getNom() + " " + client.getPrenom());
        System.out.println("Score actuel: " + client.getScore());

        service.ScoringService scoringService = new service.ScoringService();
        double scoreRecalcule = scoringService.calculerScore(client);
        System.out.println("Score recalculé: " + scoreRecalcule);

        try {
            System.out.print("\nDate du crédit (yyyy-mm-dd) [Entrée pour aujourd'hui]: ");
            String dateStr = scanner.nextLine();
            LocalDate date;
            if (dateStr.trim().isEmpty()) {
                date = LocalDate.now();
            } else {
                date = LocalDate.parse(dateStr);
            }

            System.out.print("Montant demandé (DH) : ");
            double montantDemande = scanner.nextDouble();
            scanner.nextLine();
            if (montantDemande <= 0) {
                System.out.println(" Montant invalide !");
                return;
            }

            System.out.print("Type de crédit (CONSO, IMMO, AUTO, PERSO) : ");
            String typeStr = scanner.nextLine().toUpperCase();
            model.enums.CreditType typeCredit;
            try {
                typeCredit = model.enums.CreditType.valueOf(typeStr);
            } catch (IllegalArgumentException e) {
                System.out.println("Type de crédit invalide !");
                return;
            }

            System.out.print("Durée souhaitée (mois) : ");
            int duree = scanner.nextInt();
            scanner.nextLine();

            Credit c = new Credit();
            c.setPersonneId(personneId);
            c.setDateCredit(date);
            c.setMontantDemande(montantDemande);
            c.setTypeCredit(typeCredit);
            c.setDureeEnMois(duree);

            service.DecisionService decisionService = new service.DecisionService();
            boolean estEligible = decisionService.validerEligibilite(client, montantDemande);
            
            if (!estEligible) {
                System.out.println("Demande non éligible selon les critères.");
                return;
            }

            decisionService.afficherRecommandations(c);

            System.out.println("\n--- CONFIRMATION DE LA DEMANDE ---");
            System.out.println("Voulez-vous soumettre cette demande de crédit ?");
            System.out.println("1. Oui, soumettre");
            System.out.println("2. Non, annuler");
            System.out.print("Votre choix : ");
            
            int confirmation = scanner.nextInt();
            scanner.nextLine();

            if (confirmation == 1) {
                creditService.addCredit(c);
                System.out.println("Demande de credit soumise avec succes.");

                if (c.getDecision() != null) {
                    System.out.println("Decision automatique: " + c.getDecision());
                    if (c.getDecision() == model.enums.DecisionType.ACCORD_IMMEDIAT) {
                        System.out.println("Montant octroye: " + c.getMontantOctroye() + " DH");
                        try {
                            EcheanceService echeanceService = new EcheanceService();
                            java.util.List<model.Echeance> echeancesCreees = echeanceService.getByCreditId(c.getId());
                            System.out.println("Echeances generees automatiquement: " + (echeancesCreees != null ? echeancesCreees.size() : 0));
                        } catch (Exception ex) {
                            System.out.println("Impossible de verifier les echeances: " + ex.getMessage());
                        }
                    } else if (c.getDecision() == model.enums.DecisionType.ETUDE_MANUELLE) {
                        System.out.println("Demande en attente d'etude manuelle");
                        System.out.println("Le client sera contacte sous 48h");
                    } else {
                        System.out.println("Demande refusee automatiquement");
                    }
                }
            } else {
                System.out.println("Demande annulee.");
            }

        } catch (Exception e) {
            System.out.println("Erreur lors de la demande de credit : " + e.getMessage());
        }
    }


    private void consulterCredit() {
        System.out.print("ID du crédit à consulter : ");
        long id = scanner.nextLong();
        scanner.nextLine();

        Credit c = creditService.findCreditById(id);
        if (c != null) {
            System.out.println(c);
        } else {
            System.out.println("Aucun crédit trouvé avec cet ID.");
        }
    }

    private void modifierCredit() {
        System.out.print("ID du crédit à modifier : ");
        long id = scanner.nextLong();
        scanner.nextLine();

        Credit c = creditService.findCreditById(id);
        if (c == null) {
            System.out.println("Crédit introuvable !");
            return;
        }

        System.out.println("=== Modification du crédit ID " + id + " ===");
        System.out.print("Nouveau montant demandé (" + c.getMontantDemande() + ") : ");
        String input = scanner.nextLine();
        if (!input.isEmpty()) c.setMontantDemande(Double.parseDouble(input));

        System.out.print("Nouveau montant octroyé (" + c.getMontantOctroye() + ") : ");
        input = scanner.nextLine();
        if (!input.isEmpty()) c.setMontantOctroye(Double.parseDouble(input));

        System.out.print("Nouveau taux d'intérêt (" + c.getTauxInteret() + ") : ");
        input = scanner.nextLine();
        if (!input.isEmpty()) c.setTauxInteret(Double.parseDouble(input));

        System.out.print("Nouvelle durée (" + c.getDureeEnMois() + " mois) : ");
        input = scanner.nextLine();
        if (!input.isEmpty()) c.setDureeEnMois(Integer.parseInt(input));

        creditService.updateCredit(c);
        System.out.println("Crédit mis à jour !");
    }

    private void supprimerCredit() {
        System.out.print("ID du crédit à supprimer : ");
        long id = scanner.nextLong();
        scanner.nextLine();

        Credit credit = creditService.findCreditById(id);
        if (credit == null) {
            System.out.println("Aucun crédit trouvé avec l'ID " + id);
            return;
        }
        creditService.deleteCredit(id);
        System.out.println("Crédit supprimé !");
    }

    private void afficherTousCredits() {
        List<Credit> credits = creditService.findAllCredit();
        if (credits.isEmpty()) {
            System.out.println("Aucun crédit enregistré.");
        } else {
            credits.forEach(System.out::println);
        }
    }
}
