package model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class VolFilter {
    private String promotionNom;
    private BigDecimal prixMin;
    private BigDecimal prixMax;
    private String typeSiege;
    private String villeDestination;
    private String numeroVol;
    private LocalDate dateVolDebut;
    private LocalDate dateVolFin;

    // Constructeur
    public VolFilter() {
    }

    // Getters et Setters
    public String getPromotionNom() {
        return promotionNom;
    }

    public void setPromotionNom(String promotionNom) {
        this.promotionNom = promotionNom;
    }

    public BigDecimal getPrixMin() {
        return prixMin;
    }

    public void setPrixMin(BigDecimal prixMin) {
        this.prixMin = prixMin;
    }

    public BigDecimal getPrixMax() {
        return prixMax;
    }

    public void setPrixMax(BigDecimal prixMax) {
        this.prixMax = prixMax;
    }

    public String getTypeSiege() {
        return typeSiege;
    }

    public void setTypeSiege(String typeSiege) {
        this.typeSiege = typeSiege;
    }

    public String getVilleDestination() {
        return villeDestination;
    }

    public void setVilleDestination(String villeDestination) {
        this.villeDestination = villeDestination;
    }

    public String getNumeroVol() {
        return numeroVol;
    }

    public void setNumeroVol(String numeroVol) {
        this.numeroVol = numeroVol;
    }

    public LocalDate getDateVolDebut() {
        return dateVolDebut;
    }

    public void setDateVolDebut(LocalDate dateVolDebut) {
        this.dateVolDebut = dateVolDebut;
    }

    public LocalDate getDateVolFin() {
        return dateVolFin;
    }

    public void setDateVolFin(LocalDate dateVolFin) {
        this.dateVolFin = dateVolFin;
    }
}
