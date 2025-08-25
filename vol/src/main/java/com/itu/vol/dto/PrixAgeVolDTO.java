package com.itu.vol.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PrixAgeVolDTO {
    private Long idPrixAgeVol;
    private Integer idVol;
    private String numeroVol;
    private Integer idTypeSiege;
    private String typeSiege;
    private Integer idCategorieAge;
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

    public Integer getIdVol() {
        return idVol;
    }

    public void setIdVol(Integer idVol) {
        this.idVol = idVol;
    }

    public String getNumeroVol() {
        return numeroVol;
    }

    public void setNumeroVol(String numeroVol) {
        this.numeroVol = numeroVol;
    }

    public Integer getIdTypeSiege() {
        return idTypeSiege;
    }

    public void setIdTypeSiege(Integer idTypeSiege) {
        this.idTypeSiege = idTypeSiege;
    }

    public String getTypeSiege() {
        return typeSiege;
    }

    public void setTypeSiege(String typeSiege) {
        this.typeSiege = typeSiege;
    }

    public Integer getIdCategorieAge() {
        return idCategorieAge;
    }

    public void setIdCategorieAge(Integer idCategorieAge) {
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
