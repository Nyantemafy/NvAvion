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

    @Column(name = "id_vol", nullable = false)
    private Integer idVol;

    @Column(name = "id_type_siege", nullable = false)
    private Integer idTypeSiege;

    @Column(name = "id_categorie_age", nullable = false)
    private Integer idCategorieAge;

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

    @Column(name = "prix_final", precision = 15, scale = 2, insertable = false, updatable = false)
    private BigDecimal prixFinal;

    @Column(name = "created_at", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    // Constructeurs
    public PrixAgeVol() {
    }

    public PrixAgeVol(Vol vol, TypeSiege typeSiege, CategorieAge categorieAge, BigDecimal prixBase) {
        this.vol = vol;
        this.typeSiege = typeSiege;
        this.categorieAge = categorieAge;
        this.idVol = vol.getId();
        this.idTypeSiege = typeSiege.getId();
        this.idCategorieAge = categorieAge.getId();
        this.prixBase = prixBase;
    }

    // Getters et Setters (ajout des nouveaux champs)
    public Long getId() {
        return id;
    }

    public Integer getIdVol() {
        return idVol;
    }

    public void setIdVol(Integer idVol) {
        this.idVol = idVol;
    }

    public Integer getIdTypeSiege() {
        return idTypeSiege;
    }

    public void setIdTypeSiege(Integer idTypeSiege) {
        this.idTypeSiege = idTypeSiege;
    }

    public Integer getIdCategorieAge() {
        return idCategorieAge;
    }

    public void setIdCategorieAge(Integer idCategorieAge) {
        this.idCategorieAge = idCategorieAge;
    }

    public Vol getVol() {
        return vol;
    }

    public void setVol(Vol vol) {
        this.vol = vol;
        if (vol != null) {
            this.idVol = vol.getId();
        }
    }

    public TypeSiege getTypeSiege() {
        return typeSiege;
    }

    public void setTypeSiege(TypeSiege typeSiege) {
        this.typeSiege = typeSiege;
        if (typeSiege != null) {
            this.idTypeSiege = typeSiege.getId();
        }
    }

    public CategorieAge getCategorieAge() {
        return categorieAge;
    }

    public void setCategorieAge(CategorieAge categorieAge) {
        this.categorieAge = categorieAge;
        if (categorieAge != null) {
            this.idCategorieAge = categorieAge.getId();
        }
    }

    public BigDecimal getPrixBase() {
        return prixBase;
    }

    public void setPrixBase(BigDecimal prixBase) {
        this.prixBase = prixBase;
        calculatePrixFinal();
    }

    public BigDecimal getMultiplicateur() {
        return multiplicateur;
    }

    public void setMultiplicateur(BigDecimal multiplicateur) {
        this.multiplicateur = multiplicateur;
        calculatePrixFinal();
    }

    public BigDecimal getPrixFinal() {
        return prixFinal;
    }

    public void setPrixFinal(BigDecimal prixFinal) {
        this.prixFinal = prixFinal;
    }

    private void calculatePrixFinal() {
        if (this.prixBase != null && this.multiplicateur != null) {
            this.prixFinal = this.prixBase.multiply(this.multiplicateur);
        }
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        calculatePrixFinal();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
        calculatePrixFinal();
    }
}