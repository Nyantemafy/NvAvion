package model;

import java.time.LocalDateTime;
import java.math.BigDecimal;

public class Vol {
    private Long idVol;
    private String numeroVol;
    private LocalDateTime dateVol;
    private Long idVille;
    private Long idAvion;

    private String nomVilleDestination;
    private String pseudoAvion;
    private BigDecimal prixMin;
    private BigDecimal prixMax;
    private String promotionNom;
    private Integer promotionReduction;

    public Vol() {
    }

    public Vol(String numeroVol, LocalDateTime dateVol, Long idVille, Long idAvion) {
        this.numeroVol = numeroVol;
        this.dateVol = dateVol;
        this.idVille = idVille;
        this.idAvion = idAvion;
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

    public LocalDateTime getDateVol() {
        return dateVol;
    }

    public void setDateVol(LocalDateTime dateVol) {
        this.dateVol = dateVol;
    }

    public Long getIdVille() {
        return idVille;
    }

    public void setIdVille(Long idVille) {
        this.idVille = idVille;
    }

    public Long getIdAvion() {
        return idAvion;
    }

    public void setIdAvion(Long idAvion) {
        this.idAvion = idAvion;
    }

    public String getNomVilleDestination() {
        return nomVilleDestination;
    }

    public void setNomVilleDestination(String nomVilleDestination) {
        this.nomVilleDestination = nomVilleDestination;
    }

    public String getPseudoAvion() {
        return pseudoAvion;
    }

    public void setPseudoAvion(String pseudoAvion) {
        this.pseudoAvion = pseudoAvion;
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

    public String getPromotionNom() {
        return promotionNom;
    }

    public void setPromotionNom(String promotionNom) {
        this.promotionNom = promotionNom;
    }

    public Integer getPromotionReduction() {
        return promotionReduction;
    }

    public void setPromotionReduction(Integer promotionReduction) {
        this.promotionReduction = promotionReduction;
    }
}
