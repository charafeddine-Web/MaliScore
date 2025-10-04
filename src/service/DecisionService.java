package service;

import model.*;
import model.enums.DecisionType;
import repository.CreditRepository;
import repository.ClientRepository;
import java.util.List;
import java.util.Scanner;

public class DecisionService {
    private CreditRepository creditRepository;
    private ClientRepository clientRepository;
    private ScoringService scoringService;
    private Scanner scanner;

    public DecisionService() {
        this.creditRepository = new CreditRepository();
        this.clientRepository = new ClientRepository();
        this.scoringService = new ScoringService();
        this.scanner = new Scanner(System.in);
    }

//
//    /**
//     * Traite un crédit spécifique en étude manuelle
//     */
//    private void traiterCreditManuel(Credit credit) {
//        Personne client = clientRepository.findById(credit.getPersonneId().intValue());
//        if (client == null) {
//            System.out.println("Client introuvable pour le crédit ID: " + credit.getId());
//            return;
//        }
//
//        System.out.println("\n--- CRÉDIT ID: " + credit.getId() + " ---");
//        System.out.println("Client: " + client.getNom() + " " + client.getPrenom());
//        System.out.println("Score: " + client.getScore());
//        System.out.println("Montant demandé: " + credit.getMontantDemande() + " DH");
//        System.out.println("Type: " + credit.getTypeCredit());
//
//        afficherDetailsClient(client);
//
//        System.out.println("\nOptions:");
//        System.out.println("1. Approuver le crédit");
//        System.out.println("2. Refuser le crédit");
//        System.out.println("3. Demander des documents supplémentaires");
//        System.out.println("4. Passer au suivant");
//        System.out.print("Votre choix: ");
//
//        int choix = scanner.nextInt();
//        scanner.nextLine();
//
//        switch (choix) {
//            case 1:
//                approuverCredit(credit, client);
//                break;
//            case 2:
//                refuserCredit(credit, client);
//                break;
//            case 3:
//                demanderDocuments(credit, client);
//                break;
//            case 4:
//                System.out.println("Crédit laissé en attente.");
//                break;
//            default:
//                System.out.println("Choix invalide.");
//        }
//    }

    /**
     * Affiche les détails complets du client pour l'évaluation
     */
    private void afficherDetailsClient(Personne client) {
        System.out.println("\n--- DÉTAILS CLIENT ---");
        System.out.println("Âge: " + java.time.Period.between(client.getDateNaissance(), java.time.LocalDate.now()).getYears() + " ans");
        System.out.println("Ville: " + client.getVille());
        System.out.println("Enfants: " + client.getNombreEnfants());
        System.out.println("Situation familiale: " + client.getSituationFamiliale());
        System.out.println("Investissements: " + (client.getInvestissement() ? "Oui" : "Non"));
        System.out.println("Placements: " + (client.getPlacement() ? "Oui" : "Non"));

        if (client instanceof Employe) {
            Employe emp = (Employe) client;
            System.out.println("Salaire: " + emp.getSalaire() + " DH");
            System.out.println("Ancienneté: " + emp.getAnciennete() + " ans");
            System.out.println("Poste: " + emp.getPoste());
            System.out.println("Type contrat: " + emp.getTypeContrat());
            System.out.println("Secteur: " + emp.getSecteur());
        } else if (client instanceof Professionnel) {
            Professionnel prof = (Professionnel) client;
            System.out.println("Revenu: " + prof.getRevenu() + " DH");
            System.out.println("Secteur activité: " + prof.getSecteurActivite());
            System.out.println("Activité: " + prof.getActivite());
            System.out.println("Auto-entrepreneur: " + (prof.isAutoEntrepreneur() ? "Oui" : "Non"));
        }

        List<Credit> historiqueCredits = creditRepository.findByPersonneId(client.getId());
        if (!historiqueCredits.isEmpty()) {
            System.out.println("\nHistorique des crédits:");
            for (Credit c : historiqueCredits) {
                System.out.println("- " + c.getTypeCredit() + " (" + c.getDateCredit() + "): " + 
                                 c.getMontantOctroye() + " DH - " + c.getDecision());
            }
        } else {
            System.out.println("\nAucun historique de crédit.");
        }
    }

    /**
     * Approuve un crédit après étude manuelle
     */
    private void approuverCredit(Credit credit, Personne client) {
        System.out.print("Montant à octroyer (max " + credit.getMontantDemande() + " DH): ");
        double montantOctroye = scanner.nextDouble();
        scanner.nextLine();

        if (montantOctroye <= 0 || montantOctroye > credit.getMontantDemande()) {
            System.out.println("Montant invalide!");
            return;
        }

        System.out.print("Taux d'intérêt (%): ");
        double tauxInteret = scanner.nextDouble() / 100;
        scanner.nextLine();

        credit.setMontantOctroye(montantOctroye);
        credit.setTauxInteret(tauxInteret);
        credit.setDecision(DecisionType.ACCORD_IMMEDIAT);
        creditRepository.update(credit);

        System.out.println("Crédit approuvé pour " + montantOctroye + " DH");
    }

    /**
     * Refuse un crédit après étude manuelle
     */

    private void refuserCredit(Credit credit, Personne client) {
        System.out.println("Motifs du refus:");
        System.out.println("1. Score insuffisant");
        System.out.println("2. Capacité de remboursement insuffisante");
        System.out.println("3. Historique de paiement défavorable");
        System.out.println("4. Documents insuffisants");
        System.out.println("5. Autre");
        System.out.print("Votre choix: ");

        int motif = scanner.nextInt();
        scanner.nextLine();

        String motifText = "";
        switch (motif) {
            case 1: motifText = "Score insuffisant"; break;
            case 2: motifText = "Capacité de remboursement insuffisante"; break;
            case 3: motifText = "Historique de paiement défavorable"; break;
            case 4: motifText = "Documents insuffisants"; break;
            case 5: 
                System.out.print("Précisez le motif: ");
                motifText = scanner.nextLine();
                break;
            default: motifText = "Motif non spécifié";
        }

        credit.setMontantOctroye(0);
        credit.setDecision(DecisionType.REFUS_AUTOMATIQUE);
        creditRepository.update(credit);

        System.out.println(" Crédit refusé - Motif: " + motifText);
    }

    /**
     * Demande des documents supplémentaires
     */

    private void demanderDocuments(Credit credit, Personne client) {
        System.out.println("Documents supplémentaires requis:");
        System.out.println("1. Justificatifs de revenus");
        System.out.println("2. Attestation de travail");
        System.out.println("3. Relevés bancaires");
        System.out.println("4. Garanties");
        System.out.println("5. Autre");
        System.out.print("Votre choix: ");

        int typeDoc = scanner.nextInt();
        scanner.nextLine();

        String document = "";
        switch (typeDoc) {
            case 1: document = "Justificatifs de revenus"; break;
            case 2: document = "Attestation de travail"; break;
            case 3: document = "Relevés bancaires"; break;
            case 4: document = "Garanties"; break;
            case 5: 
                System.out.print("Précisez le document: ");
                document = scanner.nextLine();
                break;
        }

        System.out.println("Document demandé: " + document);
        System.out.println("Le client sera contacté pour fournir: " + document);
    }

    /**
     * Calcule et affiche les recommandations de décision
     */
    public void afficherRecommandations(Credit credit) {
        Personne client = clientRepository.findById(credit.getPersonneId().intValue());
        if (client == null) return;

        System.out.println("\n=== RECOMMANDATIONS DE DÉCISION ===");
        System.out.println("Client: " + client.getNom() + " " + client.getPrenom());
        System.out.println("Score actuel: " + client.getScore());

        double scoreRecalcule = scoringService.calculerScore(client);
        System.out.println("Score recalculé: " + scoreRecalcule);

        if (scoreRecalcule >= 80) {
            System.out.println(" RECOMMANDATION: ACCORD IMMÉDIAT");
            System.out.println("   - Score excellent");
            System.out.println("   - Risque faible");
            System.out.println("   - Montant recommande: " + calculerMontantRecommand(client));
        } else if (scoreRecalcule >= 60) {
            System.out.println(" RECOMMANDATION: ÉTUDE MANUELLE");
            System.out.println("   - Score acceptable mais nécessite validation");
            System.out.println("   - Vérifier la stabilité des revenus");
            System.out.println("   - Montant recommande: " + calculerMontantRecommand(client) * 0.8);
        } else {
            System.out.println(" RECOMMANDATION: REFUS");
            System.out.println("   - Score insuffisant");
            System.out.println("   - Risque élevé");
            System.out.println("   - Suggérer amélioration du profil");
        }

        afficherFacteursRisque(client);
    }


    private double calculerMontantRecommand(Personne client) {
        double revenus = 0;
        if (client instanceof Employe) {
            revenus = ((Employe) client).getSalaire();
        } else if (client instanceof Professionnel) {
            revenus = ((Professionnel) client).getRevenu();
        }
        boolean estNouveauClient = creditRepository.findByPersonneId(client.getId()).isEmpty();

        if (estNouveauClient) {
            return revenus * 4;
        } else {
            if (client.getScore() >= 80) {
                return revenus * 10;
            } else {
                return revenus * 7;
            }
        }
    }

    /**
     * Affiche les facteurs de risque identifiés
     */
    private void afficherFacteursRisque(Personne client) {
        System.out.println("\n--- FACTEURS DE RISQUE ---");
        
        if (client.getScore() < 60) {
            System.out.println("Score faible (" + client.getScore() + ")");
        }

        if (client instanceof Employe) {
            Employe emp = (Employe) client;
            if (emp.getAnciennete() < 1) {
                System.out.println("Ancienneté faible (" + emp.getAnciennete() + " ans)");
            }
            if (emp.getTypeContrat() == model.enums.ContratType.CDD_INTERIM) {
                System.out.println("Contrat précaire (CDD/Intérim)");
            }
        }

        if (client.getNombreEnfants() > 2) {
            System.out.println("Nombre d'enfants élevé (" + client.getNombreEnfants() + ")");
        }

        if (!client.getInvestissement() && !client.getPlacement()) {
            System.out.println("Aucun patrimoine");
        }

        List<Credit> historique = creditRepository.findByPersonneId(client.getId());
        long creditsRefuses = historique.stream()
                .filter(c -> c.getDecision() == DecisionType.REFUS_AUTOMATIQUE)
                .count();
        
        if (creditsRefuses > 0) {
            System.out.println("Historique de refus (" + creditsRefuses + " refus)");
        }
    }

    /**
     * Valide les critères d'éligibilité par profil
     */
    public boolean validerEligibilite(Personne client, double montantDemande) {
        List<Credit> credits = creditRepository.findByPersonneId(client.getId());
        boolean estNouveauClient = credits.isEmpty();

        if (estNouveauClient) {
            if (client.getScore() < 70) {
                System.out.println("Score insuffisant pour nouveau client (minimum 70)");
                return false;
            }

            if (client instanceof Employe) {
                Employe emp = (Employe) client;
                if (emp.getAnciennete() < 2) {
                    System.out.println("Ancienneté insuffisante pour nouveau client (minimum 2 ans)");
                    return false;
                }
                if (emp.getTypeContrat() == model.enums.ContratType.CDD_INTERIM) {
                    System.out.println("Contrat précaire non éligible pour nouveau client");
                    return false;
                }
            }

            double revenus = 0;
            if (client instanceof Employe) revenus = ((Employe) client).getSalaire();
            if (client instanceof Professionnel) revenus = ((Professionnel) client).getRevenu();
            
            if (montantDemande > revenus * 4) {
                System.out.println("Montant trop élevé pour nouveau client (max 4x le salaire)");
                return false;
            }

        } else {
            if (client.getScore() < 60) {
                System.out.println("Score insuffisant pour client existant (minimum 60)");
                return false;
            }

            long refusRecents = credits.stream()
                    .filter(c -> c.getDecision() == DecisionType.REFUS_AUTOMATIQUE)
                    .count();
            
            if (refusRecents > 2) {
                System.out.println("Trop de refus récents (" + refusRecents + " refus)");
                return false;
            }

            double revenus = 0;
            if (client instanceof Employe) revenus = ((Employe) client).getSalaire();
            if (client instanceof Professionnel) revenus = ((Professionnel) client).getRevenu();
            
            double montantMax = client.getScore() > 80 ? revenus * 10 : revenus * 7;
            if (montantDemande > montantMax) {
                System.out.println("Montant trop élevé (max " + montantMax + " DH)");
                return false;
            }
        }

        return true;
    }
}
