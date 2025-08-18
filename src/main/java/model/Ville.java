package model;

public class Ville {
    private Long idVille;
    private String nom;

    // Constructeurs
    public Ville() {
    }

    public Ville(String nom) {
        this.nom = nom;
    }

    // Getters et Setters
    public Long getIdVille() {
        return idVille;
    }

    public void setIdVille(Long idVille) {
        this.idVille = idVille;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }
}
