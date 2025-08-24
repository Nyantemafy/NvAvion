package com.itu.vol.dto;

import java.math.BigDecimal;

public class PrixCalculeResponse {

    private Integer age;
    private String categorieAge;
    private BigDecimal prixEco;
    private BigDecimal prixBusiness;
    private String error;

    // Constructeur par défaut
    public PrixCalculeResponse() {
    }

    // Constructeur avec tous les paramètres
    public PrixCalculeResponse(Integer age, String categorieAge, BigDecimal prixEco, BigDecimal prixBusiness,
            String error) {
        this.age = age;
        this.categorieAge = categorieAge;
        this.prixEco = prixEco;
        this.prixBusiness = prixBusiness;
        this.error = error;
    }

    // Getters et Setters
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

    public BigDecimal getPrixEco() {
        return prixEco;
    }

    public void setPrixEco(BigDecimal prixEco) {
        this.prixEco = prixEco;
    }

    public BigDecimal getPrixBusiness() {
        return prixBusiness;
    }

    public void setPrixBusiness(BigDecimal prixBusiness) {
        this.prixBusiness = prixBusiness;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
