package ui;

import java.util.Scanner;

public class MenuPranc {
    private Scanner scanner;
    private MenuClient menuClient;
    private MenuCredit menuCredit;

    public MenuPranc() {
        this.scanner = new Scanner(System.in);
        this.menuClient = new MenuClient();
        this.menuCredit = new MenuCredit();
    }

    public void start() {
        int choix;
        do {
            System.out.println("\n=== MENU PRINCIPAL ===");
            System.out.println("1. Gestion des clients");
            System.out.println("2. Gestion des crédits");
            System.out.println("3. Quitter");
            System.out.print("Votre choix : ");
            choix = scanner.nextInt();
            scanner.nextLine();

            switch (choix) {
                case 1:
                    menuClient.start();
                    break;
                case 2:
                    menuCredit.start();
                    break;
                case 3:
                    System.out.println("Au revoir !");
                    break;
                default:
                    System.out.println("Choix invalide !");
            }
        } while (choix != 3);
    }
}
