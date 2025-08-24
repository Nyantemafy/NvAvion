package com.itu.vol.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PrixCalculeDTO {
    private Long idVol;
    private String numeroVol;
    private Long idTypeSiege;
    private String typeSiege;
    private Long userId;
    private String username;
    private LocalDate dateNaissance;
    private Integer age;
    private String categorieAge;
    private BigDecimal prixBase;
    private BigDecimal prixFinal;
    private BigDecimal multiplicateur;

    // Constructeurs
    public PrixCalculeDTO() {
    }

    public PrixCalculeDTO(Long idVol, String numeroVol, Long idTypeSiege, String typeSiege,
            Long userId, String username, LocalDate dateNaissance, Integer age,
            String categorieAge, BigDecimal prixBase, BigDecimal prixFinal, BigDecimal multiplicateur) {
        this.idVol = idVol;
        this.numeroVol = numeroVol;
        this.idTypeSiege = idTypeSiege;
        this.typeSiege = typeSiege;
        this.userId = userId;
        this.username = username;
        this.dateNaissance = dateNaissance;
        this.age = age;
        this.categorieAge = categorieAge;
        this.prixBase = prixBase;
        this.prixFinal = prixFinal;
        this.multiplicateur = multiplicateur;
    }

    // Getters et Setters complets...
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getCategorieAge() {
        return categorieAge;
    }

    public void setCategorieAge(String categorieAge) {
        this.categorieAge = categorieAge;
    }

    public BigDecimal getPrixBase() {
        return prixBase;
    }

    public void setPrixBase(BigDecimal prixBase) {
        this.prixBase = prixBase;
    }

    public BigDecimal getPrixFinal() {
        return prixFinal;
    }

    public void setPrixFinal(BigDecimal prixFinal) {
        this.prixFinal = prixFinal;
    }

    public BigDecimal getMultiplicateur() {
        return multiplicateur;
    }

    public void setMultiplicateur(BigDecimal multiplicateur) {
        this.multiplicateur = multiplicateur;
    }
}