package com.itu.vol.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PrixAgeVolDTO {
    private Long idPrixAgeVol;
    private Long idVol;
    private String numeroVol;
    private Long idTypeSiege;
    private String typeSiege;
    private Long idCategorieAge;
    private String categorieNom;
    private BigDecimal prixBase;
    private BigDecimal multiplicateur;
    private BigDecimal prixFinal;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PrixAgeVolDTO() {
    }

    // Getters et Setters
    public Long getIdPrixAgeVol() {
        return idPrixAgeVol;
    }

    public void setIdPrixAgeVol(Long idPrixAgeVol) {
        this.idPrixAgeVol = idPrixAgeVol;
    }

    public Long getIdVol() {
        return idVol;
    }

    public void setIdVol(Long idVol) {
        this.idVol = idVol;
    }

    public String getNumeroVol() {
        return numeroVol;
    }

    public void setNumeroVol(String numeroVol) {
        this.numeroVol = numeroVol;
    }

    public Long getIdTypeSiege() {
        return idTypeSiege;
    }

    public void setIdTypeSiege(Long idTypeSiege) {
        this.idTypeSiege = idTypeSiege;
    }

    public String getTypeSiege() {
        return typeSiege;
    }

    public void setTypeSiege(String typeSiege) {
        this.typeSiege = typeSiege;
    }

    public Long getIdCategorieAge() {
        return idCategorieAge;
    }

    public void setIdCategorieAge(Long idCategorieAge) {
        this.idCategorieAge = idCategorieAge;
    }

    public String getCategorieNom() {
        return categorieNom;
    }

    public void setCategorieNom(String categorieNom) {
        this.categorieNom = categorieNom;
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

    public BigDecimal getPrixFinal() {
        return prixFinal;
    }

    public void setPrixFinal(BigDecimal prixFinal) {
        this.prixFinal = prixFinal;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
