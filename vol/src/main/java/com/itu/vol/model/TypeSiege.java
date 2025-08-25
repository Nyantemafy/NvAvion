package com.itu.vol.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "type_siege")
public class TypeSiege {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_type_siege")
    private Integer id;

    @Column(length = 50)
    private String rubrique;

    @Column(name = "prix_", precision = 15, scale = 2)
    private BigDecimal prix;

    // --- Constructeurs ---
    public TypeSiege() {
    }

    public TypeSiege(String rubrique, BigDecimal prix) {
        this.rubrique = rubrique;
        this.prix = prix;
    }

    // --- Getters & Setters ---
    public Integer getId() {
        return id;
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
