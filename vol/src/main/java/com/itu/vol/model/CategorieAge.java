package com.itu.vol.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "categorie_age")
public class CategorieAge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categorie_age")
    private Long id;
    
    private String nom;
    
    @Column(name = "age_min")
    private Integer ageMin;
    
    @Column(name = "age_max")
    private Integer ageMax;
    
    @Column(name = "multiplicateur_prix")
    private BigDecimal multiplicateurPrix;
    
    private String description;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // Constructeurs
    public CategorieAge() {}
    
    public CategorieAge(String nom, Integer ageMin, Integer ageMax, BigDecimal multiplicateurPrix) {
        this.nom = nom;
        this.ageMin = ageMin;
        this.ageMax = ageMax;
        this.multiplicateurPrix = multiplicateurPrix;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    
    public Integer getAgeMin() { return ageMin; }
    public void setAgeMin(Integer ageMin) { this.ageMin = ageMin; }
    
    public Integer getAgeMax() { return ageMax; }
    public void setAgeMax(Integer ageMax) { this.ageMax = ageMax; }
    
    public BigDecimal getMultiplicateurPrix() { return multiplicateurPrix; }
    public void setMultiplicateurPrix(BigDecimal multiplicateurPrix) { this.multiplicateurPrix = multiplicateurPrix; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

// PrixAgeVol.java
package com.itu.vol.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "prix_age_vol")
public class PrixAgeVol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_prix_age_vol")
    private Long id;
    
    @Column(name = "id_vol")
    private Long idVol;
    
    @Column(name = "id_type_siege")
    private Long idTypeSiege;
    
    @Column(name = "id_categorie_age")
    private Long idCategorieAge;
    
    @Column(name = "prix_base")
    private BigDecimal prixBase;
    
    @Column(name = "prix_final", insertable = false, updatable = false)
    private BigDecimal prixFinal;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vol", insertable = false, updatable = false)
    private Vol vol;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_type_siege", insertable = false, updatable = false)
    private TypeSiege typeSiege;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categorie_age", insertable = false, updatable = false)
    private CategorieAge categorieAge;
    
    // Constructeurs
    public PrixAgeVol() {}
    
    public PrixAgeVol(Long idVol, Long idTypeSiege, Long idCategorieAge, BigDecimal prixBase) {
        this.idVol = idVol;
        this.idTypeSiege = idTypeSiege;
        this.idCategorieAge = idCategorieAge;
        this.prixBase = prixBase;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getIdVol() { return idVol; }
    public void setIdVol(Long idVol) { this.idVol = idVol; }
    
    public Long getIdTypeSiege() { return idTypeSiege; }
    public void setIdTypeSiege(Long idTypeSiege) { this.idTypeSiege = idTypeSiege; }
    
    public Long getIdCategorieAge() { return idCategorieAge; }
    public void setIdCategorieAge(Long idCategorieAge) { this.idCategorieAge = idCategorieAge; }
    
    public BigDecimal getPrixBase() { return prixBase; }
    public void setPrixBase(BigDecimal prixBase) { this.prixBase = prixBase; }
    
    public BigDecimal getPrixFinal() { return prixFinal; }
    public void setPrixFinal(BigDecimal prixFinal) { this.prixFinal = prixFinal; }
    
}