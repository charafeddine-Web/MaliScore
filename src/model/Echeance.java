package model;

import model.enums.StatutPaiement;

import java.time.LocalDate;

public class Echeance {
    private Long id;
    private LocalDate dateEcheance;
    private double mensualite;
    private LocalDate datePaiement;
    private StatutPaiement statutPaiement;
    private Long creditId;

    public Echeance() {}

    public Echeance(Long id, LocalDate dateEcheance, double mensualite,
                    LocalDate datePaiement, StatutPaiement statutPaiement,Long creditId) {
        this.id = id;
        this.dateEcheance = dateEcheance;
        this.mensualite = mensualite;
        this.datePaiement = datePaiement;
        this.statutPaiement = statutPaiement;
        this.creditId = creditId;
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getDateEcheance() { return dateEcheance; }

    public Long getCreditId() {
        return creditId;
    }

    public void setCreditId(Long creditId) {
        this.creditId = creditId;
    }

    public void setDateEcheance(LocalDate dateEcheance) { this.dateEcheance = dateEcheance; }

    public double getMensualite() { return mensualite; }
    public void setMensualite(double mensualite) { this.mensualite = mensualite; }

    public LocalDate getDatePaiement() { return datePaiement; }
    public void setDatePaiement(LocalDate datePaiement) { this.datePaiement = datePaiement; }

    public StatutPaiement getStatutPaiement() { return statutPaiement; }
    public void setStatutPaiement(StatutPaiement statutPaiement) { this.statutPaiement = statutPaiement; }


    @Override
    public String toString() {
        return "Echeance{" +
                "id=" + id +
                ", dateEcheance=" + dateEcheance +
                ", mensualite=" + mensualite +
                ", datePaiement=" + datePaiement +
                ", statutPaiement=" + statutPaiement +
                '}';
    }
}
