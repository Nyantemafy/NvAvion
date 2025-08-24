package com.itu.vol.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class ConfigPrixForm {

    @NotNull(message = "Le type de siège est obligatoire")
    private Integer idTypeSiege;

    @NotNull(message = "La catégorie d'âge est obligatoire")
    private Integer idCategorieAge;

    @NotNull(message = "Le prix de base est obligatoire")
    @DecimalMin(value = "0.01", message = "Le prix de base doit être positif")
    private BigDecimal prixBase;

    @NotNull(message = "Le multiplicateur est obligatoire")
    @DecimalMin(value = "0.1", message = "Le multiplicateur doit être au moins 0.1")
    private BigDecimal multiplicateur;

    // Constructeur par défaut
    public ConfigPrixForm() {
    }

    // Constructeur avec tous les champs
    public ConfigPrixForm(Integer idTypeSiege, Integer idCategorieAge, BigDecimal prixBase, BigDecimal multiplicateur) {
        this.idTypeSiege = idTypeSiege;
        this.idCategorieAge = idCategorieAge;
        this.prixBase = prixBase;
        this.multiplicateur = multiplicateur;
    }

    // Getters et Setters
    public Integer getIdTypeSiege() {
        return idTypeSiege;
    }

    public void setIdTypeSiege(Integer idTypeSiege) {
        this.idTypeSiege = idTypeSiege;
    }

    public Integer getIdCategorieAge() {
        return idCategorieAge;
    }

    public void setIdCategorieAge(Integer idCategorieAge) {
        this.idCategorieAge = idCategorieAge;
    }

    public BigDecimal getPrixBase() {
        return prixBase;
    }

    public void setPrixBase(BigDecimal prixBase) {
        this.prixBase = prixBase;
    }

    public BigDecimal getMultiplicateur() {
        return multiplicateur;
    }

    public void setMultiplicateur(BigDecimal multiplicateur) {
        this.multiplicateur = multiplicateur;
    }
}
