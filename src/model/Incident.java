package model;

import model.enums.TypeIncident;

import java.time.LocalDate;

public class Incident {
    private Long id;
    private LocalDate dateIncident;
    private Echeance echeance;
    private int scoreImpact;
    private TypeIncident typeIncident;

    public Incident() {}

    public Incident(Long id, LocalDate dateIncident, Echeance echeance, int scoreImpact, TypeIncident typeIncident) {
        this.id = id;
        this.dateIncident = dateIncident;
        this.echeance = echeance;
        this.scoreImpact = scoreImpact;
        this.typeIncident = typeIncident;
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getDateIncident() { return dateIncident; }
    public void setDateIncident(LocalDate dateIncident) { this.dateIncident = dateIncident; }

    public Echeance getEcheance() { return echeance; }
    public void setEcheance(Echeance echeance) { this.echeance = echeance; }

    public int getScoreImpact() { return scoreImpact; }
    public void setScoreImpact(int scoreImpact) { this.scoreImpact = scoreImpact; }

    public TypeIncident getTypeIncident() { return typeIncident; }
    public void setTypeIncident(TypeIncident typeIncident) { this.typeIncident = typeIncident; }

    @Override
    public String toString() {
        return "Incident{" +
                "id=" + id +
                ", dateIncident=" + dateIncident +
                ", echeanceId=" + (echeance != null ? echeance.getId() : "null") +
                ", scoreImpact=" + scoreImpact +
                ", typeIncident=" + typeIncident +
                '}';
    }
}
