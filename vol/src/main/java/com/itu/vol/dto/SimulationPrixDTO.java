package com.itu.vol.dto;

import java.math.BigDecimal;

public class SimulationPrixDTO {
    private int age;
    private String categorieAge;
    private BigDecimal prixEconomique;
    private BigDecimal prixBusiness;
    private BigDecimal multiplicateur;

    public SimulationPrixDTO() {
    }

    // Getters et Setters
    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getCategorieAge() {
        return categorieAge;
    }

    public void setCategorieAge(String categorieAge) {
        this.categorieAge = categorieAge;
    }

    public BigDecimal getPrixEconomique() {
        return prixEconomique;
    }

    public void setPrixEconomique(BigDecimal prixEconomique) {
        this.prixEconomique = prixEconomique;
    }

    public BigDecimal getPrixBusiness() {
        return prixBusiness;
    }

    public void setPrixBusiness(BigDecimal prixBusiness) {
        this.prixBusiness = prixBusiness;
    }

    public BigDecimal getMultiplicateur() {
        return multiplicateur;
    }

    public void setMultiplicateur(BigDecimal multiplicateur) {
        this.multiplicateur = multiplicateur;
    }
}
