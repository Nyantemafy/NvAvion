package model;

import java.math.BigDecimal;

public class PrixSiegeVol {
    private Long idPrixSiegeVol;
    private BigDecimal prix;
    private Long idTypeSiege;
    private Long idVol;

    // Propriétés étendues
    private String rubriqueTypeSiege;

    // Constructeurs
    public PrixSiegeVol() {
    }

    public PrixSiegeVol(BigDecimal prix, Long idTypeSiege, Long idVol) {
        this.prix = prix;
        this.idTypeSiege = idTypeSiege;
        this.idVol = idVol;
    }

    // Getters et Setters
    public Long getIdPrixSiegeVol() {
        return idPrixSiegeVol;
    }

    public void setIdPrixSiegeVol(Long idPrixSiegeVol) {
        this.idPrixSiegeVol = idPrixSiegeVol;
    }

    public BigDecimal getPrix() {
        return prix;
    }

    public void setPrix(BigDecimal prix) {
        this.prix = prix;
    }

    public Long getIdTypeSiege() {
        return idTypeSiege;
    }

    public void setIdTypeSiege(Long idTypeSiege) {
        this.idTypeSiege = idTypeSiege;
    }

    public Long getIdVol() {
        return idVol;
    }

    public void setIdVol(Long idVol) {
        this.idVol = idVol;
    }

    public String getRubriqueTypeSiege() {
        return rubriqueTypeSiege;
    }

    public void setRubriqueTypeSiege(String rubriqueTypeSiege) {
        this.rubriqueTypeSiege = rubriqueTypeSiege;
    }
}
