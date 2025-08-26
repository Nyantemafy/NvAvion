package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CategorieAge {
    private int idCategorieAge;
    private String nom;
    private int ageMin;
    private Integer ageMax;
    private BigDecimal multiplicateurPrix;
    private String description;
    private Boolean isActive;
    private LocalDateTime createdAt;

    // Constructeurs
    public CategorieAge() {
    }

    public CategorieAge(int idCategorieAge, String nom, int ageMin, Integer ageMax,
            BigDecimal multiplicateurPrix, String description,
            Boolean isActive, LocalDateTime createdAt) {
        this.idCategorieAge = idCategorieAge;
        this.nom = nom;
        this.ageMin = ageMin;
        this.ageMax = ageMax;
        this.multiplicateurPrix = multiplicateurPrix;
        this.description = description;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }

    // Getters et setters
    public int getIdCategorieAge() {
        return idCategorieAge;
    }

    public void setIdCategorieAge(int idCategorieAge) {
        this.idCategorieAge = idCategorieAge;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getAgeMin() {
        return ageMin;
    }

    public void setAgeMin(int ageMin) {
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

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}