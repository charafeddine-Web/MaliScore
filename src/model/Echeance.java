package model;

import model.enums.StatutPaiement;

import java.time.LocalDate;

public class Echeance {
    private Long id;
    private LocalDate dateEcheance;
    private double mensualite;
    private LocalDate datePaiement;
    private StatutPaiement statutPaiement;

    public Echeance() {}

    public Echeance(Long id, LocalDate dateEcheance, double mensualite,
                    LocalDate datePaiement, StatutPaiement statutPaiement) {
        this.id = id;
        this.dateEcheance = dateEcheance;
        this.mensualite = mensualite;
        this.datePaiement = datePaiement;
        this.statutPaiement = statutPaiement;
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getDateEcheance() { return dateEcheance; }
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
