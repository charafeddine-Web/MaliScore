package service;

import model.*;
import repository.ClientRepository;
import repository.CreditRepository;
import repository.EcheanceRepository;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

public class ScoringService {
    private ClientRepository clientRepository;
    private CreditRepository creditRepository;
    private EcheanceRepository echeanceRepository;
    public ScoringService(){
        this.clientRepository=new ClientRepository();
        this.creditRepository = new CreditRepository();
        this.echeanceRepository = new EcheanceRepository();
    }


    public double calculerScore(Personne p) {
        double score = 0;

        score += calculStabiliteProfessionnelle(p);
        score += calculCapaciteFinanciere(p);
        score += calculHistorique(p);
        score += calculRelationClient(p);
        score += calculPatrimoine(p);
        p.setScore(score);
        clientRepository.update(p);
        return score;
    }

    private double calculStabiliteProfessionnelle(Personne p){
        double points = 0;

        if (p instanceof Employe) {
            Employe e = (Employe) p;

            switch (e.getTypeContrat()) {
                case CDI_PUBLIC:
                    points += 25;
                    break;
                case CDI_PRIVEE_GRANDE:
                    points += 15;
                    break;
                case CDI_PRIVEE_PME:
                    points += 12;
                    break;
                case CDD_INTERIM:
                    points += 10;
                    break;
                default:
                    break;
            }

            if (e.getAnciennete() >= 5) points += 5;
            else if (e.getAnciennete() >= 2) points += 3;
            else if (e.getAnciennete() >= 1) points += 1;

        } else if (p instanceof Professionnel) {
            Professionnel pr = (Professionnel) p;

            if (pr.isAutoEntrepreneur()) {
                points += 12;
            } else {
                points += 18;
            }

        }

        return points;

    };

    private double calculCapaciteFinanciere(Personne p){
        double revenus=0;

        if (p instanceof  Employe) revenus = ((Employe) p).getSalaire();
        if(p instanceof  Professionnel) revenus = ((Professionnel) p).getRevenu();
        double pts;

        if (revenus >= 10000) pts= 30;
        else if (revenus >= 8000) pts= 25;
        else if (revenus >= 5000) pts= 20;
        else if (revenus >= 3000) pts= 15;
        else pts= 10;

        if (pts < 0) pts = 0;
        if (pts > 30) pts = 30;
        return pts;

    };

    private double calculHistorique(Personne p) {
        List<Credit> credits = creditRepository.findByPersonneId(p.getId());
        if (credits.isEmpty()) return 0;

        Credit dernier = credits.stream()
                .max(Comparator.comparing(Credit::getDateCredit))
                .orElse(null);
        if (dernier == null) return 0;

        List<Echeance> echeances = echeanceRepository.findByCreditId(dernier.getId());
        if (echeances.isEmpty()) return 0;

        LocalDate today = LocalDate.now();

        long nbImpayesNonRegles = echeances.stream()
                .filter(e -> e.getDatePaiement() == null &&
                        ChronoUnit.DAYS.between(e.getDateEcheance(), today) >= 31)
                .count();

        long nbRetards = echeances.stream()
                .filter(e -> e.getDatePaiement() == null)
                .filter(e -> {
                    long daysLate = ChronoUnit.DAYS.between(e.getDateEcheance(), today);
                    return daysLate >= 5 && daysLate <= 30;
                })
                .count();

        long nbImpayesRegles = echeances.stream()
                .filter(e -> e.getDatePaiement() != null)
                .filter(e -> ChronoUnit.DAYS.between(e.getDateEcheance(), e.getDatePaiement()) >= 31)
                .count();

        long nbRetardsPayes = echeances.stream()
                .filter(e -> e.getDatePaiement() != null)
                .filter(e -> {
                    long daysDiff = ChronoUnit.DAYS.between(e.getDateEcheance(), e.getDatePaiement());
                    return daysDiff >= 5 && daysDiff <= 30;
                })
                .count();

        long nbPayesATemps = echeances.stream()
                .filter(e -> e.getDatePaiement() != null)
                .filter(e -> ChronoUnit.DAYS.between(e.getDateEcheance(), e.getDatePaiement()) <= 0)
                .count();

        double points = 0;
        points += -10 * nbImpayesNonRegles;
        points += 5 * nbImpayesRegles;
        points += (nbRetards >= 4 ? -5 : (nbRetards >= 1 ? -3 : 0));
        points += 3 * nbRetardsPayes;

        if (nbImpayesNonRegles + nbImpayesRegles + nbRetards == 0 &&
                nbPayesATemps == echeances.size()) {
            points += 10;
        }

        return Math.max(-15, Math.min(15, points));
    }

    private double calculRelationClient(Personne p) {
        double points = 0;

        boolean nouveau = true;
        try {
            List<Credit> credits = creditRepository.findByPersonneId(p.getId());
            if (credits != null && !credits.isEmpty()) nouveau = false;
        } catch (Exception ex) {
            nouveau = true;
        }

        if (nouveau) {
            if (p.getDateNaissance() != null) {
                int age = Period.between(p.getDateNaissance(), LocalDate.now()).getYears();
                if (age >= 36 && age <= 55) points += 10;
                else if (age >= 26 && age <= 35) points += 8;
                else if (age >= 18 && age <= 25) points += 4;
                else if (age > 55) points += 6;
            }

            if (p.getSituationFamiliale() != null) {
                if ("marie".equalsIgnoreCase(p.getSituationFamiliale()) || "marié".equalsIgnoreCase(p.getSituationFamiliale())) {
                    points += 3;
                } else {
                    points += 2;
                }
            }

            int enfants = p.getNombreEnfants();
            if (enfants == 0) points += 2;
            else if (enfants <= 2) points += 1;
            else points += 0;

        } else {
            if (p.getCreatedAt() != null) {
                int annees = Period.between(p.getCreatedAt().toLocalDate(), LocalDate.now()).getYears();
                if (annees > 3) points += 10;
                else if (annees >= 1) points += 8;
                else points += 5;
            } else {
                points += 5;
            }
        }

        if (points < 0) points = 0;
        if (points > 10) points = 10;
        return points;
    }

    private double calculPatrimoine(Personne p) {
        double points = 0;
        if (p.getInvestissement() || p.getPlacement()) {
            points = 10;
        }
        return points;
    }


}
