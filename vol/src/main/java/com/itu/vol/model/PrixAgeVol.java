package com.itu.vol.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "prix_age_vol", uniqueConstraints = @UniqueConstraint(columnNames = { "id_vol", "id_type_siege",
        "id_categorie_age" }))
public class PrixAgeVol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_prix_age_vol")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vol", insertable = false, updatable = false)
    private Vol vol;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_type_siege", insertable = false, updatable = false)
    private TypeSiege typeSiege;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categorie_age", insertable = false, updatable = false)
    private CategorieAge categorieAge;

    @Column(name = "prix_base", precision = 15, scale = 2, nullable = false)
    private BigDecimal prixBase;

    @Column(name = "multiplicateur", precision = 6, scale = 2, nullable = false)
    private BigDecimal multiplicateur;

    @Column(name = "prix_final", precision = 15, scale = 2, insertable = false)
    private BigDecimal prixFinal;

    @Column(name = "created_at", updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    // --- Constructeurs ---
    public PrixAgeVol() {
    }

    public PrixAgeVol(Vol vol, TypeSiege typeSiege, CategorieAge categorieAge, BigDecimal prixBase) {
        this.vol = vol;
        this.typeSiege = typeSiege;
        this.categorieAge = categorieAge;
        this.prixBase = prixBase;
    }

    // --- Getters & Setters ---
    public Long getId() {
        return id;
    }

    public Vol getVol() {
        return vol;
    }

    public void setVol(Vol vol) {
        this.vol = vol;
    }

    public TypeSiege getTypeSiege() {
        return typeSiege;
    }

    public void setTypeSiege(TypeSiege typeSiege) {
        this.typeSiege = typeSiege;
    }

    public CategorieAge getCategorieAge() {
        return categorieAge;
    }

    public void setCategorieAge(CategorieAge categorieAge) {
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
