package com.itu.vol.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class InitPrixVolForm {

    @NotNull(message = "Le prix économique de base est obligatoire")
    @DecimalMin(value = "0.01", message = "Le prix économique doit être positif")
    private BigDecimal prixEcoBase;

    @NotNull(message = "Le prix business de base est obligatoire")
    @DecimalMin(value = "0.01", message = "Le prix business doit être positif")
    private BigDecimal prixBusinessBase;

    public InitPrixVolForm() {
    }

    // Constructeur avec paramètres
    public InitPrixVolForm(BigDecimal prixEcoBase, BigDecimal prixBusinessBase) {
        this.prixEcoBase = prixEcoBase;
        this.prixBusinessBase = prixBusinessBase;
    }

    // Getters et Setters
    public BigDecimal getPrixEcoBase() {
        return prixEcoBase;
    }

    public void setPrixEcoBase(BigDecimal prixEcoBase) {
        this.prixEcoBase = prixEcoBase;
    }

    public BigDecimal getPrixBusinessBase() {
        return prixBusinessBase;
    }

    public void setPrixBusinessBase(BigDecimal prixBusinessBase) {
        this.prixBusinessBase = prixBusinessBase;
    }
}
