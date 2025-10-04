package model;

import java.time.LocalDateTime;

public class ScoreHistory {
    private Long id;
    private Long personneId;
    private double ancienScore;
    private double nouveauScore;
    private String raison;
    private LocalDateTime dateChangement;

    public ScoreHistory() {}

    public ScoreHistory(Long personneId, double ancienScore, double nouveauScore, String raison) {
        this.personneId = personneId;
        this.ancienScore = ancienScore;
        this.nouveauScore = nouveauScore;
        this.raison = raison;
        this.dateChangement = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPersonneId() { return personneId; }
    public void setPersonneId(Long personneId) { this.personneId = personneId; }

    public double getAncienScore() { return ancienScore; }
    public void setAncienScore(double ancienScore) { this.ancienScore = ancienScore; }

    public double getNouveauScore() { return nouveauScore; }
    public void setNouveauScore(double nouveauScore) { this.nouveauScore = nouveauScore; }

    public String getRaison() { return raison; }
    public void setRaison(String raison) { this.raison = raison; }

    public LocalDateTime getDateChangement() { return dateChangement; }
    public void setDateChangement(LocalDateTime dateChangement) { this.dateChangement = dateChangement; }

    public double getDifference() {
        return nouveauScore - ancienScore;
    }

    @Override
    public String toString() {
        return "ScoreHistory{" +
                "id=" + id +
                ", personneId=" + personneId +
                ", ancienScore=" + ancienScore +
                ", nouveauScore=" + nouveauScore +
                ", difference=" + getDifference() +
                ", raison='" + raison + '\'' +
                ", dateChangement=" + dateChangement +
                '}';
    }
}
