package model;
import java.time.LocalDate;
import java.time.LocalDateTime;

import model.enums.TypePersonne;

public abstract class Personne {
    private Long id;
    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private String ville;
    private int nombreEnfants;
    private boolean investissement;
    private boolean placement;
    private String situationFamiliale;
    private LocalDateTime createdAt;
    private double score;
    private TypePersonne typePersonne;

    public Personne() {}

    public Personne(Long id, String nom, String prenom, LocalDate dateNaissance, String ville,
                    int nombreEnfants, boolean investissement, boolean placement,
                    String situationFamiliale, LocalDateTime createdAt, double score, TypePersonne typePersonne) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.ville = ville;
        this.nombreEnfants = nombreEnfants;
        this.investissement = investissement;
        this.placement = placement;
        this.situationFamiliale = situationFamiliale;
        this.createdAt = createdAt;
        this.score = score;
        this.typePersonne = typePersonne;
    }



    // Getters et Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public LocalDate getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }

    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }

    public int getNombreEnfants() { return nombreEnfants; }
    public void setNombreEnfants(int nombreEnfants) { this.nombreEnfants = nombreEnfants; }

    public boolean getInvestissement() { return investissement; }
    public void setInvestissement(boolean investissement) { this.investissement = investissement; }

    public boolean getPlacement() { return placement; }
    public void setPlacement(boolean placement) { this.placement = placement; }

    public String getSituationFamiliale() { return situationFamiliale; }
    public void setSituationFamiliale(String situationFamiliale) { this.situationFamiliale = situationFamiliale; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }

    public TypePersonne getTypePersonne() { return typePersonne; }
    public void setTypePersonne(TypePersonne typePersonne) { this.typePersonne = typePersonne; }

    @Override
    public String toString() {
        return "Personne{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", dateNaissance=" + dateNaissance +
                ", ville='" + ville + '\'' +
                ", nombreEnfants=" + nombreEnfants +
                ", investissement=" + investissement +
                ", placement=" + placement +
                ", situationFamiliale='" + situationFamiliale + '\'' +
                ", createdAt=" + createdAt +
                ", score=" + score +
                ", typePersonne=" + typePersonne +
                '}';
    }

}
