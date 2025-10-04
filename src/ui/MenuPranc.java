package ui;

import java.util.Scanner;

public class MenuPranc {
    private Scanner scanner;
    private MenuClient menuClient;
    private MenuCredit menuCredit;
    private MenuAnalytics menuAnalytics;
    private MenuPayment menuPayment;

    public MenuPranc() {
        this.scanner = new Scanner(System.in);
        this.menuClient = new MenuClient();
        this.menuCredit = new MenuCredit();
        this.menuAnalytics = new MenuAnalytics();
        this.menuPayment = new MenuPayment();
    }

    public void start() {
        int choix;
        do {
            System.out.println("\n==================================================");
            System.out.println("MALISCORE - SYSTEME DE SCORING CREDIT");
            System.out.println("==================================================");
            System.out.println("1. 👥 Gestion des clients");
            System.out.println("2. 💰 Gestion des crédits");
            System.out.println("3. 📊 Analytics & Décisions");
            System.out.println("4. 💳 Gestion des paiements");
            System.out.println("5. [X] Quitter");
            System.out.println("==================================================");
            System.out.print("Votre choix : ");
            
            try {
                choix = scanner.nextInt();
                scanner.nextLine();

                switch (choix) {
                    case 1:
                        menuClient.start();
                        // Clear any leftover input after returning from client menu
                        if (scanner.hasNextLine()) {
                            scanner.nextLine();
                        }
                        break;
                    case 2:
                        menuCredit.start();
                        if (scanner.hasNextLine()) {
                            scanner.nextLine();
                        }
                        break;
                    case 3:
                        menuAnalytics.start();
                        if (scanner.hasNextLine()) {
                            scanner.nextLine();
                        }
                        break;
                    case 4:
                        menuPayment.start();
                        if (scanner.hasNextLine()) {
                            scanner.nextLine();
                        }
                        break;
                    case 5:
                        System.out.println("\n👋 Merci d'avoir utilisé MaliScore !");
                        System.out.println("Au revoir !");
                        break;
                    default:
                        System.out.println("[ERROR] Choix invalide ! Veuillez entrer un nombre entre 1 et 5.");
                }
            } catch (Exception e) {
                System.out.println("[ERROR] Erreur de saisie. Veuillez entrer un nombre valide.");
                scanner.nextLine();
                choix = 0;
            }
        } while (choix != 5);
    }
}
