package ui;

import model.Employe;
import model.Personne;
import model.Professionnel;

import service.ClientService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class MenuClient {

    private ClientService clientService = new ClientService();
    private Scanner scanner = new Scanner(System.in);

    public void start() {
        int choice = -1;
        while (choice != 6) {
            System.out.println("\n=== MENU CLIENT ===");
            System.out.println("1. Ajouter un client");
            System.out.println("2. Consulter un client");
            System.out.println("3. Modifier un client");
            System.out.println("4. Supprimer un client");
            System.out.println("5. Afficher tous les clients");
            System.out.println("6. Quitter");
            System.out.print("Votre choix : ");

            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
            } else {
                System.out.println("Veuillez entrer un chiffre entre 1 et 6 !");
                scanner.next();
                continue;
            }
            scanner.nextLine();

            switch (choice) {
                case 1 : ajouterClient();break;
                case 2 : consulterClient();break;
                case 3 : modifierClient();break;
                case 4 : supprimerClient();break;
                case 5 : afficherTousClients();break;
                case 6 : System.out.println("Au revoir !");break;
                default: System.out.println("Choix invalide !");break;
            }
        }
    }

    private void ajouterClient() {
        System.out.println("Ajouter un client :");
        System.out.print("Type (1= Employe, 2= Professionnel) : ");
        int type = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Nom : ");
        String nom = scanner.nextLine();
        System.out.print("Prenom : ");
        String prenom = scanner.nextLine();
        System.out.print("Date de naissance (yyyy-mm-dd) : ");
        LocalDate dateNaissance = LocalDate.parse(scanner.nextLine());
        System.out.print("Ville : ");
        String ville = scanner.nextLine();
        System.out.print("Nombre d’enfants : ");
        int enfants = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Investissement (true/false) : ");
        boolean investissement = Boolean.parseBoolean(scanner.nextLine());
        System.out.print("Placement : ");
        boolean placement = scanner.nextBoolean();
        scanner.nextLine();
        System.out.print("Situation familiale : ");
        String situation = scanner.nextLine();

        LocalDateTime createdAt = LocalDateTime.now();
        double score = 0.0;

        if (type == 1) {

            Employe e = new Employe();
            e.setNom(nom);
            e.setPrenom(prenom);
            e.setDateNaissance(dateNaissance);
            e.setVille(ville);
            e.setNombreEnfants(enfants);
            e.setInvestissement(investissement);
            e.setPlacement(placement);
            e.setSituationFamiliale(situation);
            e.setCreatedAt(createdAt);
            e.setScore(score);

            System.out.print("Salaire : ");
            e.setSalaire(scanner.nextDouble());
            scanner.nextLine();
            System.out.print("Anciennete : ");
            e.setAnciennete(scanner.nextInt());
            scanner.nextLine();
            System.out.print("Poste : ");
            e.setPoste(scanner.nextLine());
            System.out.print("Type contrat (CDI, CDD, STAGE) : ");
            e.setTypeContrat(model.enums.ContratType.valueOf(scanner.nextLine().toUpperCase()));
            System.out.print("Secteur (PUBLIC, GRANDE_ENTREPRISE, PME) : ");
            e.setSecteur(model.enums.SecteurType.valueOf(scanner.nextLine().toUpperCase()));

            clientService.addClient(e);

        } else if (type == 2) {
            Professionnel p = new Professionnel();
            p.setNom(nom);
            p.setPrenom(prenom);
            p.setDateNaissance(dateNaissance);
            p.setVille(ville);
            p.setNombreEnfants(enfants);
            p.setInvestissement(investissement);
            p.setPlacement(placement);
            p.setSituationFamiliale(situation);
            p.setCreatedAt(createdAt);
            p.setScore(score);

            System.out.print("Revenu : ");
            p.setRevenu(scanner.nextDouble());
            scanner.nextLine();
            System.out.print("Immatriculation fiscale : ");
            p.setImmatriculationFiscale(scanner.nextLine());
            System.out.print("Secteur activite (service,commerce,..): ");
            p.setSecteurActivite(scanner.nextLine());
            System.out.print("Activite (Avocat, Mecanicien,..) : ");
            p.setActivite(scanner.nextLine());

            clientService.addClient(p);
        }else {
            System.out.println("Choix Invalid !");
        }

        System.out.println("Client ajouté avec succès !");
    }

    private void consulterClient() {
        System.out.print("ID du client à consulter : ");
        int id = scanner.nextInt();
        scanner.nextLine();

        Personne p = clientService.findClient(id);
        if (p != null) {
            System.out.println(p);
        } else {
            System.out.println("Client introuvable !");
        }
    }

    private void modifierClient() {
        System.out.print("ID du client à modifier : ");
        int id = 0;
        try {
            id = scanner.nextInt();
            scanner.nextLine();
        } catch (NumberFormatException e) {
            System.out.println("ID invalide !");
            return;
        }

        Personne p = clientService.findClient(id);

        if (p != null) {
            System.out.println("\n=== Modification du client : " + p.getNom() + " " + p.getPrenom() + " ===");

            System.out.print("Nouveau nom (" + p.getNom() + ") : ");
            String nom = scanner.nextLine();
            if (!nom.trim().isEmpty()) p.setNom(nom);

            System.out.print("Nouveau prénom (" + p.getPrenom() + ") : ");
            String prenom = scanner.nextLine();
            if (!prenom.trim().isEmpty()) p.setPrenom(prenom);

            System.out.println("Nouvelle date de naissance (" + p.getDateNaissance() + ") [yyyy-MM-dd] : ");
            String date = scanner.nextLine();
            if (!date.trim().isEmpty()) {
                try {
                    p.setDateNaissance(LocalDate.parse(date));
                } catch (Exception ex) {
                    System.out.println("Date invalide, ancienne valeur conservée.");
                }
            }

            System.out.print("Nouvelle ville (" + p.getVille() + ") : ");
            String ville = scanner.nextLine();
            if (!ville.trim().isEmpty()) p.setVille(ville);

            System.out.print("Nouveau nombre d’enfants (" + p.getNombreEnfants() + ") : ");
            String enfantsStr = scanner.nextLine();
            if (!enfantsStr.trim().isEmpty()) {
                try {
                    p.setNombreEnfants(Integer.parseInt(enfantsStr));
                } catch (NumberFormatException e) {
                    System.out.println("Valeur invalide, ancienne valeur conservée.");
                }
            }

            System.out.print("Investissement (true/false) (" + p.getInvestissement() + ") : ");
            String investStr = scanner.nextLine();
            if (!investStr.trim().isEmpty()) {
                if (investStr.equalsIgnoreCase("true") || investStr.equalsIgnoreCase("false")) {
                    p.setInvestissement(Boolean.parseBoolean(investStr));
                } else {
                    System.out.println("Valeur invalide, ancienne valeur conservée.");
                }
            }


            System.out.print("Nouveau placement (True/False) (" + p.getPlacement() + ") : ");
            boolean placementStr = scanner.nextBoolean();
            if (!placementStr) {
                try {
                    p.setPlacement(placementStr);
                } catch (NumberFormatException e) {
                    System.out.println("Valeur invalide, ancienne valeur conservée.");
                }
            }

            System.out.print("Nouvelle situation familiale (" + p.getSituationFamiliale() + ") : ");
            String sit = scanner.nextLine();
            if (!sit.trim().isEmpty()) p.setSituationFamiliale(sit);

            if (p instanceof Employe ) {
                Employe e = (Employe) p;

                System.out.println("---- Mise à jour Employé ----");

                System.out.print("Nouveau salaire (" + e.getSalaire() + ") : ");
                String salaireStr = scanner.nextLine();
                if (!salaireStr.trim().isEmpty()) {
                    try {
                        e.setSalaire(Double.parseDouble(salaireStr));
                    } catch (NumberFormatException ex) {
                        System.out.println("Valeur invalide, ancienne conservée.");
                    }
                }

                System.out.print("Nouvelle ancienneté (" + e.getAnciennete() + ") : ");
                String ancStr = scanner.nextLine();
                if (!ancStr.trim().isEmpty()) {
                    try {
                        e.setAnciennete(Integer.parseInt(ancStr));
                    } catch (NumberFormatException ex) {
                        System.out.println("Valeur invalide, ancienne conservée.");
                    }
                }

                System.out.print("Nouveau poste (" + e.getPoste() + ") : ");
                String poste = scanner.nextLine();
                if (!poste.trim().isEmpty()) e.setPoste(poste);

                System.out.print("Nouveau type contrat (CDI, CDD, STAGE) (" + e.getTypeContrat() + ") : ");
                String contratStr = scanner.nextLine();
                if (!contratStr.trim().isEmpty()) {
                    try {
                        e.setTypeContrat(model.enums.ContratType.valueOf(contratStr.toUpperCase()));
                    } catch (IllegalArgumentException ex) {
                        System.out.println("Type contrat invalide !");
                    }
                }

                System.out.print("Nouveau secteur (PUBLIC, GRANDE_ENTREPRISE, PME) (" + e.getSecteur() + ") : ");
                String secStr = scanner.nextLine();
                if (!secStr.trim().isEmpty()) {
                    try {
                        e.setSecteur(model.enums.SecteurType.valueOf(secStr.toUpperCase()));
                    } catch (IllegalArgumentException ex) {
                        System.out.println("Secteur invalide !");
                    }
                }

            }else if (p instanceof Professionnel ) {
                Professionnel pro = (Professionnel) p;

                System.out.println("---- Mise à jour Professionnel ----");

                System.out.print("Nouveau revenu (" + pro.getRevenu() + ") : ");
                String revStr = scanner.nextLine();
                if (!revStr.trim().isEmpty()) {
                    try {
                        pro.setRevenu(Double.parseDouble(revStr));
                    } catch (NumberFormatException ex) {
                        System.out.println("Valeur invalide, ancienne conservée.");
                    }
                }

                System.out.print("Nouvelle immatriculation fiscale (" + pro.getImmatriculationFiscale() + ") : ");
                String imm = scanner.nextLine();
                if (!imm.trim().isEmpty()) pro.setImmatriculationFiscale(imm);

                System.out.print("Nouveau secteur activité (" + pro.getSecteurActivite() + ") : ");
                String sec = scanner.nextLine();
                if (!sec.trim().isEmpty()) pro.setSecteurActivite(sec);

                System.out.print("Nouvelle activité (" + pro.getActivite() + ") : ");
                String act = scanner.nextLine();
                if (!act.trim().isEmpty()) pro.setActivite(act);
            }

            clientService.update(p);
            System.out.println("Client mis à jour avec succès !");
        } else {
            System.out.println("Client introuvable !");
        }
    }


    private void supprimerClient() {
        System.out.print("ID du client à supprimer : ");
        int id = scanner.nextInt();
        scanner.nextLine();
        clientService.deleteClient(id);
    }
    private void afficherTousClients() {
        List<Personne> clients = clientService.findAll();
        for (Personne p : clients) {
            System.out.println(p);
            System.out.println("-----------------------------");
        }
    }

}
