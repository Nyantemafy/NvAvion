package model;

import java.math.BigDecimal;

public class PrixDetail {
    private String typeSiege;
    private String categorieAge;
    private BigDecimal prixBase;
    private BigDecimal multiplicateur;
    private BigDecimal prixFinal;
    private int ageUser;

    // Ajouts pour parseQuantities et getPrixDetailsForVol
    private Long categorieAgeId;
    private int quantite;
    private BigDecimal prixUnitaire;
    private BigDecimal prixTotal;

    // Getters et Setters
    public String getTypeSiege() {
        return typeSiege;
    }

    public void setTypeSiege(String typeSiege) {
        this.typeSiege = typeSiege;
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

    public int getAgeUser() {
        return ageUser;
    }

    public void setAgeUser(int ageUser) {
        this.ageUser = ageUser;
    }

    public Long getCategorieAgeId() {
        return categorieAgeId;
    }

    public void setCategorieAgeId(Long categorieAgeId) {
        this.categorieAgeId = categorieAgeId;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public BigDecimal getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(BigDecimal prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public BigDecimal getPrixTotal() {
        return prixTotal;
    }

    public void setPrixTotal(BigDecimal prixTotal) {
        this.prixTotal = prixTotal;
    }
}
