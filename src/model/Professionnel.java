package model;

import model.enums.TypePersonne;

public class Professionnel extends Personne {
    private double revenu;
    private String immatriculationFiscale;
    private String secteurActivite;
    private String activite;
    private boolean autoEntrepreneur;

    public Professionnel() {}
    public Professionnel(Long id, String nom, String prenom, java.time.LocalDate dateNaissance, String ville,
                         int nombreEnfants, boolean investissement, boolean placement, String situationFamiliale,
                         java.time.LocalDateTime createdAt, double score, double revenu,
                         String immatriculationFiscale, String secteurActivite, String activite,boolean autoEntrepreneur) {

        super(id, nom, prenom, dateNaissance, ville, nombreEnfants, investissement,
                placement, situationFamiliale, createdAt, score, TypePersonne.PROFESSIONNEL);
        this.revenu = revenu;
        this.immatriculationFiscale = immatriculationFiscale;
        this.secteurActivite = secteurActivite;
        this.activite = activite;
        this.autoEntrepreneur =autoEntrepreneur;
    }

    public double getRevenu() { return revenu; }
    public void setRevenu(double revenu) { this.revenu = revenu; }

    public String getImmatriculationFiscale() { return immatriculationFiscale; }
    public void setImmatriculationFiscale(String immatriculationFiscale) { this.immatriculationFiscale = immatriculationFiscale; }

    public String getSecteurActivite() { return secteurActivite; }
    public void setSecteurActivite(String secteurActivite) { this.secteurActivite = secteurActivite; }

    public String getActivite() { return activite; }
    public void setActivite(String activite) { this.activite = activite; }


    public boolean isAutoEntrepreneur() {
        return autoEntrepreneur;
    }
    public void setAutoEntrepreneur(boolean autoEntrepreneur) {
        this.autoEntrepreneur = autoEntrepreneur;
    }

    @Override
    public String toString() {
        return "Professionnel{" +
                "id=" + getId() +
                ", nom='" + getNom() + '\'' +
                ", prenom='" + getPrenom() + '\'' +
                ", revenu=" + revenu +
                ", immatriculationFiscale='" + immatriculationFiscale + '\'' +
                ", secteurActivite='" + secteurActivite + '\'' +
                ", activite='" + activite + '\'' +
                '}';
    }

}
