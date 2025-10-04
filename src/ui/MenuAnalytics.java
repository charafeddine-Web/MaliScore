package ui;

import model.Personne;
import service.AnalyticsService;
import service.DecisionService;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class MenuAnalytics {
    private AnalyticsService analyticsService;
    private DecisionService decisionService;
    private Scanner scanner;

    public MenuAnalytics() {
        this.analyticsService = new AnalyticsService();
        this.decisionService = new DecisionService();
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        int choix;
        do {
            System.out.println("\n=== MENU ANALYTICS & DÉCISIONS ===");
            System.out.println("1. Statistiques générales");
            System.out.println("2. Clients éligibles crédit immobilier");
            System.out.println("3. Clients à risque (suivi)");
            System.out.println("4. Tri des clients par critères");
            System.out.println("5. Répartition par type d'emploi");
            System.out.println("6. Ciblage campagne crédit consommation");
            System.out.println("7. Études manuelles en attente");
            System.out.println("8. Retour au menu principal");
            System.out.print("Votre choix : ");
            
            try {
                choix = scanner.nextInt();
                scanner.nextLine();
                
                switch (choix) {
                    case 1:
                        afficherStatistiquesGenerales();
                        break;
                    case 2:
                        afficherClientsEligiblesImmobilier();
                        break;
                    case 3:
                        afficherClientsARisque();
                        break;
                    case 4:
                        afficherClientsTries();
                        break;
                    case 5:
                        afficherRepartitionEmploi();
                        break;
                    case 6:
                        afficherCibleCampagne();
                        break;
                    case 7:
                        decisionService.traiterEtudesManuelles();
                        break;
                    case 8:
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
            
            if (choix != 8) {
                System.out.println("\nAppuyez sur Entrée pour continuer...");
                scanner.nextLine();
            }
            
        } while (choix != 8);
    }

    private void afficherStatistiquesGenerales() {
        System.out.println("\n=== STATISTIQUES GÉNÉRALES DU PORTEFEUILLE ===");
        
        Map<String, Object> stats = analyticsService.getStatistiquesGenerales();
        
        System.out.println("📊 PORTEFEUILLE GLOBAL:");
        System.out.println("┌─────────────────────────────────────────┐");
        System.out.printf("│ Total clients: %-25d │\n", stats.get("totalClients"));
        System.out.printf("│ - Employés: %-27d │\n", stats.get("employes"));
        System.out.printf("│ - Professionnels: %-22d │\n", stats.get("professionnels"));
        System.out.printf("│ Score moyen: %-25.1f │\n", stats.get("scoreMoyen"));
        System.out.println("└─────────────────────────────────────────┘");
        
        System.out.println("\n💰 CRÉDITS:");
        System.out.println("┌─────────────────────────────────────────┐");
        System.out.printf("│ Total crédits: %-24d │\n", stats.get("totalCredits"));
        System.out.printf("│ Montant total octroyé: %-15.0f DH │\n", stats.get("montantTotalOctroye"));
        System.out.println("└─────────────────────────────────────────┘");
        
        System.out.println("\n📈 DÉCISIONS:");
        System.out.println("┌─────────────────────────────────────────┐");
        System.out.printf("│ ✓ Accords immédiats: %-20d │\n", stats.get("accordsImmediats"));
        System.out.printf("│ ⚠ Études manuelles: %-21d │\n", stats.get("etudesManuelles"));
        System.out.printf("│ ✗ Refus: %-29d │\n", stats.get("refus"));
        System.out.println("└─────────────────────────────────────────┘");
    }

    private void afficherClientsEligiblesImmobilier() {
        System.out.println("\n=== CLIENTS ÉLIGIBLES CRÉDIT IMMOBILIER ===");
        System.out.println("Critères: Âge 25-50 ans, Revenus >4000DH, CDI, Score >70, Marié");
        
        List<Personne> clients = analyticsService.getClientsEligiblesCreditImmobilier();
        
        if (clients.isEmpty()) {
            System.out.println("❌ Aucun client éligible trouvé.");
            return;
        }
        
        System.out.println("✅ " + clients.size() + " clients éligibles trouvés:");
        System.out.println("┌─────┬─────────────────────┬────────┬──────────┬─────────────┐");
        System.out.println("│ ID  │ Nom                 │ Score  │ Revenus  │ Type        │");
        System.out.println("├─────┼─────────────────────┼────────┼──────────┼─────────────┤");
        
        for (Personne client : clients) {
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

    private void afficherClientsARisque() {
        System.out.println("\n=== CLIENTS À RISQUE (TOP 10) ===");
        System.out.println("Critères: Score < 60, incidents récents < 6 mois");
        
        List<Personne> clients = analyticsService.getClientsARisque();
        
        if (clients.isEmpty()) {
            System.out.println("✅ Aucun client à risque identifié.");
            return;
        }
        
        System.out.println("⚠️  " + clients.size() + " clients nécessitant un suivi:");
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
        
        System.out.println("📋 Top 20 clients:");
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

    private void afficherRepartitionEmploi() {
        System.out.println("\n=== RÉPARTITION PAR TYPE D'EMPLOI ===");
        
        Map<String, Map<String, Object>> repartition = analyticsService.getRepartitionParTypeEmploi();
        
        if (repartition.isEmpty()) {
            System.out.println("❌ Aucune donnée disponible.");
            return;
        }
        
        for (Map.Entry<String, Map<String, Object>> entry : repartition.entrySet()) {
            String typeContrat = entry.getKey();
            Map<String, Object> stats = entry.getValue();
            
            System.out.println("\n📊 " + typeContrat + ":");
            System.out.println("┌─────────────────────────────────────────┐");
            System.out.printf("│ Nombre de clients: %-20d │\n", stats.get("nombreClients"));
            System.out.printf("│ Score moyen: %-26.1f │\n", stats.get("scoreMoyen"));
            System.out.printf("│ Revenus moyens: %-22.0f DH │\n", stats.get("revenusMoyens"));
            System.out.printf("│ Taux approbation: %-22s │\n", stats.get("tauxApprobation"));
            System.out.println("└─────────────────────────────────────────┘");
        }
    }

    private void afficherCibleCampagne() {
        System.out.println("\n=== CIBLAGE CAMPAGNE CRÉDIT CONSOMMATION ===");
        System.out.println("Critères: Score 65-85, Revenus 4000-8000DH, Âge 28-45 ans, Pas de crédit en cours");
        
        List<Personne> clients = analyticsService.getCibleCampagneCreditConsommation();
        
        if (clients.isEmpty()) {
            System.out.println("❌ Aucun client ciblé trouvé.");
            return;
        }
        
        System.out.println("🎯 " + clients.size() + " clients ciblés pour la campagne:");
        System.out.println("┌─────┬─────────────────────┬────────┬──────────┬─────────────┐");
        System.out.println("│ ID  │ Nom                 │ Score  │ Revenus  │ Âge         │");
        System.out.println("├─────┼─────────────────────┼────────┼──────────┼─────────────┤");
        
        for (Personne client : clients) {
            String nom = (client.getNom() + " " + client.getPrenom());
            if (nom.length() > 20) nom = nom.substring(0, 17) + "...";
            
            double revenus = 0;
            if (client instanceof model.Employe) {
                revenus = ((model.Employe) client).getSalaire();
            } else if (client instanceof model.Professionnel) {
                revenus = ((model.Professionnel) client).getRevenu();
            }
            
            int age = java.time.Period.between(client.getDateNaissance(), java.time.LocalDate.now()).getYears();
            
            System.out.printf("│ %-3d │ %-19s │ %-6.1f │ %-8.0f │ %-11d │\n", 
                            client.getId(), nom, client.getScore(), revenus, age);
        }
        System.out.println("└─────┴─────────────────────┴────────┴──────────┴─────────────┘");
        
        System.out.println("\n📧 Action recommandée:");
        System.out.println("   → Campagne SMS/Email pour " + clients.size() + " clients");
        System.out.println("   → Taux de conversion estimé: 15-25%");
        System.out.println("   → Potentiel de nouveaux crédits: " + (clients.size() * 0.2) + " clients");
    }
}
