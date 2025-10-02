package service;

import model.Employe;
import model.Personne;
import model.Professionnel;
import repository.ClientRepository;

public class ScoringService {
    private ClientRepository clientRepository;
    public ScoringService(){
        this.clientRepository=new ClientRepository();
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
        if (revenus >= 10000) return 30;
        else if (revenus >= 8000) return 25;
        else if (revenus >= 5000) return 20;
        else if (revenus >= 3000) return 15;
        else return 10;
    };


    private double calculHistorique(Personne p){
        double points=0;
        return 0;
    };
    private double calculRelationClient(Personne p){return 0;};
    private double calculPatrimoine(Personne p){return 0;};

}
