package ui;

import model.Credit;
import model.Personne;
import service.ClientService;
import service.CreditService;

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
        System.out.println("=== Ajouter un crédit ===");
        Credit c = new Credit();

        System.out.print("ID du client : ");
        long personneId = scanner.nextLong();
        scanner.nextLine();

        ClientService clientService = new ClientService();
        Personne client = clientService.findClient((int) personneId);
        if (client == null) {
            System.out.println("Client introuvable !");
            return;
        }
        c.setPersonneId(personneId);

        try {
            System.out.print("Date du crédit (yyyy-mm-dd) : ");
            LocalDate date = LocalDate.parse(scanner.nextLine());
            c.setDateCredit(date);

            System.out.print("Montant demandé : ");
            double montantDemande = scanner.nextDouble();
            if (montantDemande <= 0) {
                System.out.println("Montant invalide !");
                return;
            }
            c.setMontantDemande(montantDemande);

            System.out.print("Montant octroyé : ");
            double montantOctroye = scanner.nextDouble();
            c.setMontantOctroye(montantOctroye);

            System.out.print("Taux d'intérêt : ");
            double taux = scanner.nextDouble();
            c.setTauxInteret(taux);

            System.out.print("Durée (mois) : ");
            int duree = scanner.nextInt();
            c.setDureeEnMois(duree);
            scanner.nextLine();

            System.out.print("Type de crédit (CONSO, IMMO, AUTO, PERSO) : ");
            String typeStr = scanner.nextLine().toUpperCase();
            try {
                c.setTypeCredit(model.enums.CreditType.valueOf(typeStr));
            } catch (IllegalArgumentException e) {
                System.out.println("Type de crédit invalide !");
                return;
            }
            System.out.print("Décision (ACCORD_IMMEDIAT, ETUDE_MANUELLE, REFUS_AUTOMATIQUE) : ");
            String decisionStr = scanner.nextLine().toUpperCase();
            try {
                c.setDecision(model.enums.DecisionType.valueOf(decisionStr));
            } catch (IllegalArgumentException e) {
                System.out.println("Décision invalide !");
                return;
            }
            creditService.addCredit(c);
            System.out.println("Crédit ajouté avec succès !");
        } catch (Exception e) {
            System.out.println("Erreur lors de l'ajout du crédit : " + e.getMessage());
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
