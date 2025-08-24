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
    private Long idVol;

    @Column(name = "id_type_siege", nullable = false)
    private Long idTypeSiege;

    @Column(name = "id_categorie_age", nullable = false)
    private Long idCategorieAge;

    @Column(name = "prix_base", precision = 15, scale = 2, nullable = false)
    private BigDecimal prixBase;

    @Column(name = "multiplicateur", precision = 6, scale = 2, nullable = false)
    private BigDecimal multiplicateur;

    @Column(name = "prix_final", precision = 15, scale = 2, insertable = false, updatable = false)
    private BigDecimal prixFinal;

    @Column(name = "created_at", updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    // --- Constructeurs ---
    public PrixAgeVol() {
    }

    public PrixAgeVol(Long idVol, Long idTypeSiege, Long idCategorieAge, BigDecimal prixBase) {
        this.idVol = idVol;
        this.idTypeSiege = idTypeSiege;
        this.idCategorieAge = idCategorieAge;
        this.prixBase = prixBase;
    }

    // --- Getters & Setters ---
    public Long getId() {
        return id;
    }

    public Long getIdVol() {
        return idVol;
    }

    public void setIdVol(Long idVol) {
        this.idVol = idVol;
    }

    public Long getIdTypeSiege() {
        return idTypeSiege;
    }

    public void setIdTypeSiege(Long idTypeSiege) {
        this.idTypeSiege = idTypeSiege;
    }

    public Long getIdCategorieAge() {
        return idCategorieAge;
    }

    public void setIdCategorieAge(Long idCategorieAge) {
        this.idCategorieAge = idCategorieAge;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
