package model;
import model.enums.*;

public class Employe extends Personne {
    private double salaire;
    private int anciennete;
    private String poste;
    private ContratType typeContrat;
    private SecteurType secteur;

    public Employe(Long id, String nom, String prenom, java.time.LocalDate dateNaissance, String ville,
                   int nombreEnfants, double investissement, double placement, String situationFamiliale,
                   java.time.LocalDate createdAt, double score, double salaire, int anciennete,
                   String poste, ContratType typeContrat, SecteurType secteur) {

        super(id, nom, prenom, dateNaissance, ville, nombreEnfants, investissement,
                placement, situationFamiliale, createdAt, score, TypePersonne.EMPLOYE);
        this.salaire = salaire;
        this.anciennete = anciennete;
        this.poste = poste;
        this.typeContrat = typeContrat;
        this.secteur = secteur;
    }

    // Getters et Setters
    public double getSalaire() { return salaire; }
    public void setSalaire(double salaire) { this.salaire = salaire; }

    public int getAnciennete() { return anciennete; }
    public void setAnciennete(int anciennete) { this.anciennete = anciennete; }

    public String getPoste() { return poste; }
    public void setPoste(String poste) { this.poste = poste; }

    public ContratType getTypeContrat() { return typeContrat; }
    public void setTypeContrat(ContratType typeContrat) { this.typeContrat = typeContrat; }

    public SecteurType getSecteur() { return secteur; }
    public void setSecteur(SecteurType secteur) { this.secteur = secteur; }

    @Override
    public String toString() {
        return "Employe{" +
                "id=" + getId() +
                ", nom='" + getNom() + '\'' +
                ", prenom='" + getPrenom() + '\'' +
                ", salaire=" + salaire +
                ", anciennete=" + anciennete +
                ", poste='" + poste + '\'' +
                ", typeContrat=" + typeContrat +
                ", secteur=" + secteur +
                '}';
    }

}
