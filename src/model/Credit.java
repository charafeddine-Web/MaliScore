package model;

import model.enums.CreditType;
import model.enums.DecisionType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Credit {

    private Long id;
    private LocalDate dateCredit;
    private double montantDemande;
    private double montantOctroye;
    private double tauxInteret;
    private int dureeEnMois;
    private CreditType typeCredit;
    private DecisionType decision;
    private List<Echeance> echeances;

    public Credit(){};
    public Credit(Long id, LocalDate dateCredit, double montantDemande, double montantOctroye,
                  double tauxInteret, int dureeEnMois, CreditType typeCredit, DecisionType decision) {
        this.id = id;
        this.dateCredit = dateCredit;
        this.montantDemande = montantDemande;
        this.montantOctroye = montantOctroye;
        this.tauxInteret = tauxInteret;
        this.dureeEnMois = dureeEnMois;
        this.typeCredit = typeCredit;
        this.decision = decision;
        this.echeances = new ArrayList<>();
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getDateCredit() { return dateCredit; }
    public void setDateCredit(LocalDate dateCredit) { this.dateCredit = dateCredit; }

    public double getMontantDemande() { return montantDemande; }
    public void setMontantDemande(double montantDemande) { this.montantDemande = montantDemande; }

    public double getMontantOctroye() { return montantOctroye; }
    public void setMontantOctroye(double montantOctroye) { this.montantOctroye = montantOctroye; }

    public double getTauxInteret() { return tauxInteret; }
    public void setTauxInteret(double tauxInteret) { this.tauxInteret = tauxInteret; }

    public int getDureeEnMois() { return dureeEnMois; }
    public void setDureeEnMois(int dureeEnMois) { this.dureeEnMois = dureeEnMois; }

    public CreditType getTypeCredit() { return typeCredit; }
    public void setTypeCredit(CreditType typeCredit) { this.typeCredit = typeCredit; }

    public DecisionType getDecision() { return decision; }
    public void setDecision(DecisionType decision) { this.decision = decision; }

    public List<Echeance> getEcheances() { return echeances; }
    public void setEcheances(List<Echeance> echeances) { this.echeances = echeances; }


    @Override
    public String toString() {
        return "Credit{" +
                "id=" + id +
                ", dateCredit=" + dateCredit +
                ", montantDemande=" + montantDemande +
                ", montantOctroye=" + montantOctroye +
                ", tauxInteret=" + tauxInteret +
                ", dureeEnMois=" + dureeEnMois +
                ", typeCredit=" + typeCredit +
                ", decision=" + decision +
                ", echeances=" + echeances.size() +
                '}';
    }
}
