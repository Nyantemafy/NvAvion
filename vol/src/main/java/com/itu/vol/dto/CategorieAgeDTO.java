package com.itu.vol.dto;

import java.math.BigDecimal;

public class CategorieAgeDTO {
    private String nom;
    private Integer ageMin;
    private Integer ageMax;
    private BigDecimal multiplicateurPrix;
    private String description;

    public CategorieAgeDTO() {
        // Constructeur par défaut
    }

    // Méthode utilitaire pour créer une copie
    public CategorieAgeDTO copy() {
        CategorieAgeDTO dto = new CategorieAgeDTO();
        dto.setNom(this.nom);
        dto.setAgeMin(this.ageMin);
        dto.setAgeMax(this.ageMax);
        dto.setMultiplicateurPrix(this.multiplicateurPrix);
        dto.setDescription(this.description);
        return dto;
    }

    // Getters et Setters
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Integer getAgeMin() {
        return ageMin;
    }

    public void setAgeMin(Integer ageMin) {
        this.ageMin = ageMin;
    }

    public Integer getAgeMax() {
        return ageMax;
    }

    public void setAgeMax(Integer ageMax) {
        this.ageMax = ageMax;
    }

    public BigDecimal getMultiplicateurPrix() {
        return multiplicateurPrix;
    }

    public void setMultiplicateurPrix(BigDecimal multiplicateurPrix) {
        this.multiplicateurPrix = multiplicateurPrix;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
