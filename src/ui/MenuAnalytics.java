package ui;



import model.Personne;
import service.AnalyticsService;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class MenuAnalytics {
    private AnalyticsService analyticsService;
    private Scanner scanner;

    public MenuAnalytics() {
        this.analyticsService = new AnalyticsService();
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        int choix;
        do {
            System.out.println("\n=== MENU ANALYTICS ===");
            System.out.println("1. Statistiques generales");
            System.out.println("2. Clients a risque (suivi)");
            System.out.println("3. Tri des clients par criteres");
            System.out.println("4. Retour au menu principal");
            System.out.print("Votre choix : ");
            
            try {
                choix = scanner.nextInt();
                scanner.nextLine();
                
                switch (choix) {
                    case 1:
                        afficherStatistiquesGenerales();
                        break;
                    case 2:
                        afficherClientsARisque();
                        break;
                    case 3:
                        afficherClientsTries();
                        break;
                    case 4:
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
            
            if (choix != 4) {
                System.out.println("\nAppuyez sur Entrée pour continuer...");
                scanner.nextLine();
            }
            
        } while (choix != 4);
    }

    private void afficherStatistiquesGenerales() {
        System.out.println("\n=== STATISTIQUES GENERALES DU PORTEFEUILLE ===");
        
        Map<String, Object> stats = analyticsService.getStatistiquesGenerales();
        
        System.out.println("PORTEFEUILLE GLOBAL:");
        System.out.println("┌─────────────────────────────────────────┐");
        System.out.printf("│ Total clients: %-25d │\n", stats.get("totalClients"));
        System.out.printf("│ - Employés: %-27d │\n", stats.get("employes"));
        System.out.printf("│ - Professionnels: %-22d │\n", stats.get("professionnels"));
        System.out.printf("│ Score moyen: %-25.1f │\n", stats.get("scoreMoyen"));
        System.out.println("└─────────────────────────────────────────┘");
        
        System.out.println("\nCREDITS:");
        System.out.println("┌─────────────────────────────────────────┐");
        System.out.printf("│ Total crédits: %-24d │\n", stats.get("totalCredits"));
        
        Object montantObj = stats.get("montantTotalOctroye");
        double montantTotal = 0.0;
        if (montantObj instanceof Number) {
            montantTotal = ((Number) montantObj).doubleValue();
        }
        System.out.printf("│ Montant total octroyé: %-15.0f DH │\n", montantTotal);
        System.out.println("└─────────────────────────────────────────┘");
        
        System.out.println("\nDECISIONS:");
        System.out.println("┌─────────────────────────────────────────┐");
        System.out.printf("│ Accords immediats: %-20d │\n", stats.get("accordsImmediats"));
        System.out.printf("│ Etudes manuelles: %-21d │\n", stats.get("etudesManuelles"));
        System.out.printf("│ Refus: %-29d │\n", stats.get("refus"));
        System.out.println("└─────────────────────────────────────────┘");
    }

    private void afficherClientsARisque() {
        System.out.println("\n=== CLIENTS A RISQUE (TOP 10) ===");
        System.out.println("Critères: Score < 60, incidents récents < 6 mois");
        
        List<Personne> clients = analyticsService.getClientsARisque();
        
        if (clients.isEmpty()) {
            System.out.println("Aucun client a risque identifie.");
            return;
        }
        System.out.println(clients.size() + " clients necessitant un suivi:");
        System.out.println("┌─────┬─────────────────────┬────────┬──────────────────────┐");
        System.out.println("│ ID  │ Nom                 │ Score  │ Recommandation       │");
        System.out.println("├─────┼─────────────────────┼────────┼──────────────────────┤");
        
        for (Personne client : clients) {
            String nom = (client.getNom() + " " + client.getPrenom());
            if (nom.length() > 20) nom = nom.substring(0, 17) + "...";
            
            String recommandation = "";
            if (client.getScore() < 40) {
                recommandation = "Suivi urgent";
            } else if (client.getScore() < 50) {
                recommandation = "Accompagnement";
            } else {
                recommandation = "Surveillance";
            }
            
            System.out.printf("│ %-3d │ %-19s │ %-6.1f │ %-20s │\n", 
                            client.getId(), nom, client.getScore(), recommandation);
        }
        System.out.println("└─────┴─────────────────────┴────────┴──────────────────────┘");
    }

    private void afficherClientsTries() {
        System.out.println("\n=== TRI DES CLIENTS PAR CRITÈRES ===");
        System.out.println("Tri: Score (décroissant) → Revenus (décroissant) → Ancienneté");
        
        List<Personne> clients = analyticsService.getClientsTriesParCriteres();
        
        System.out.println("Top 20 clients:");
        System.out.println("┌─────┬─────────────────────┬────────┬──────────┬─────────────┐");
        System.out.println("│ ID  │ Nom                 │ Score  │ Revenus  │ Type        │");
        System.out.println("├─────┼─────────────────────┼────────┼──────────┼─────────────┤");
        
        for (int i = 0; i < Math.min(20, clients.size()); i++) {
            Personne client = clients.get(i);
            String nom = (client.getNom() + " " + client.getPrenom());
            if (nom.length() > 20) nom = nom.substring(0, 17) + "...";
            
            double revenus = 0;
            String type = "";
            if (client instanceof model.Employe) {
                revenus = ((model.Employe) client).getSalaire();
                type = "Employé";
            } else if (client instanceof model.Professionnel) {
                revenus = ((model.Professionnel) client).getRevenu();
                type = "Professionnel";
            }
            
            System.out.printf("│ %-3d │ %-19s │ %-6.1f │ %-8.0f │ %-11s │\n", 
                            client.getId(), nom, client.getScore(), revenus, type);
        }
        System.out.println("└─────┴─────────────────────┴────────┴──────────┴─────────────┘");
    }

}
