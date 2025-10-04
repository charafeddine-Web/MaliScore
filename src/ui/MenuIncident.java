package ui;



import model.Echeance;
import model.Incident;
import model.enums.TypeIncident;
import service.EcheanceService;
import service.IncidentService;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class MenuIncident {
    private final IncidentService incidentService;
    private final EcheanceService echeanceService;
    private final Scanner scanner;

    public MenuIncident() {
        this.incidentService = new IncidentService();
        this.echeanceService = new EcheanceService();
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        int choix;
        do {
            System.out.println("\n=== MENU GESTION DES INCIDENTS ===");
            System.out.println("1. Ajouter un incident");
            System.out.println("2. Lister incidents d'une échéance");
            System.out.println("3. Lister tous les incidents");
            System.out.println("4. Supprimer un incident");
            System.out.println("5. Retour au menu principal");
            System.out.print("Votre choix : ");

            try {
                choix = scanner.nextInt();
                scanner.nextLine();

                switch (choix) {
                    case 1:
                        ajouterIncident();
                        break;
                    case 2:
                        listerIncidentsParEcheance();
                        break;
                    case 3:
                        listerTousIncidents();
                        break;
                    case 4:
                        supprimerIncident();
                        break;
                    case 5:
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

            if (choix != 5) {
                System.out.println("\nAppuyez sur Entrée pour continuer...");
                scanner.nextLine();
            }

        } while (choix != 5);
    }

    private void ajouterIncident() {
        System.out.println("\n=== AJOUTER UN INCIDENT ===");

        System.out.print("ID de l'échéance : ");
        long echeanceId = scanner.nextLong();
        scanner.nextLine();

        Echeance echeance = echeanceService.getEcheanceById(echeanceId);
        if (echeance == null) {
            System.out.println("Echeance introuvable !");
            return;
        }

        System.out.print("Date de l'incident (yyyy-mm-dd) [Entrée pour aujourd'hui] : ");
        String dateStr = scanner.nextLine();
        LocalDate dateIncident;
        if (dateStr.trim().isEmpty()) {
            dateIncident = LocalDate.now();
        } else {
            try {
                dateIncident = LocalDate.parse(dateStr);
            } catch (Exception e) {
                System.out.println("Date invalide !");
                return;
            }
        }

        System.out.print("Type (PAYE_A_TEMPS, EN_RETARD, PAYE_EN_RETARD, IMPAYE_NON_REGLE, IMPAYE_REGLE) : ");
        String typeStr = scanner.nextLine().trim().toUpperCase();
        TypeIncident typeIncident;
        try {
            typeIncident = TypeIncident.valueOf(typeStr);
        } catch (IllegalArgumentException ex) {
            System.out.println("Type d'incident invalide !");
            return;
        }

        System.out.print("Impact score (entier, ex: -10, 3) : ");
        int impact;
        try {
            impact = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException ex) {
            System.out.println("Valeur d'impact invalide !");
            return;
        }

        Incident incident = new Incident();
        incident.setEcheance(echeance);
        incident.setDateIncident(dateIncident);
        incident.setTypeIncident(typeIncident);
        incident.setScoreImpact(impact);

        boolean ok = incidentService.addIncident(incident);
        if (ok) {
            System.out.println("Incident ajouté avec succes. ID: " + incident.getId());
        } else {
            System.out.println("Echec de l'ajout de l'incident.");
        }
    }

    private void listerIncidentsParEcheance() {
        System.out.println("\n=== INCIDENTS PAR ECHEANCE ===");
        System.out.print("ID de l'échéance : ");
        long echeanceId = scanner.nextLong();
        scanner.nextLine();

        List<Incident> list = new repository.IncidentRepository().findByEcheanceId(echeanceId);
        if (list.isEmpty()) {
            System.out.println("Aucun incident pour cette échéance.");
            return;
        }

        for (Incident inc : list) {
            System.out.println(inc);
        }
    }

    private void listerTousIncidents() {
        System.out.println("\n=== TOUS LES INCIDENTS ===");
        List<Incident> list = incidentService.getAllIncidents();
        if (list.isEmpty()) {
            System.out.println("Aucun incident enregistré.");
            return;
        }
        for (Incident inc : list) {
            System.out.println(inc);
        }
    }

    private void supprimerIncident() {
        System.out.println("\n=== SUPPRIMER UN INCIDENT ===");
        System.out.print("ID de l'incident : ");
        long id = scanner.nextLong();
        scanner.nextLine();
        boolean ok = incidentService.deleteIncident(id);
        if (ok) {
            System.out.println("Incident supprime avec succes.");
        } else {
            System.out.println("Echec de la suppression.");
        }
    }
}


