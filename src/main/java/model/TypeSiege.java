package model;

import java.math.BigDecimal;

public class TypeSiege {
    private Long idTypeSiege;
    private String rubrique;
    private BigDecimal prix;

    // Constructeurs
    public TypeSiege() {
    }

    public TypeSiege(String rubrique, BigDecimal prix) {
        this.rubrique = rubrique;
        this.prix = prix;
    }

    // Getters et Setters
    public Long getIdTypeSiege() {
        return idTypeSiege;
    }

    public void setIdTypeSiege(Long idTypeSiege) {
        this.idTypeSiege = idTypeSiege;
    }

    public String getRubrique() {
        return rubrique;
    }

    public void setRubrique(String rubrique) {
        this.rubrique = rubrique;
    }

    public BigDecimal getPrix() {
        return prix;
    }

    public void setPrix(BigDecimal prix) {
        this.prix = prix;
    }
}
