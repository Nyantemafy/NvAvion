package com.itu.vol.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vol")
public class Vol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vol")
    private Long id;

    @Column(name = "numero_vol_", length = 50)
    private String numeroVol;

    @Column(name = "date_vol_")
    private LocalDateTime dateVol;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ville", insertable = false, updatable = false)
    private Ville ville;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_avion", insertable = false, updatable = false)
    private Avion avion;

    // --- Constructeurs ---
    public Vol() {
    }

    public Vol(String numeroVol, LocalDateTime dateVol, Ville ville, Avion avion) {
        this.numeroVol = numeroVol;
        this.dateVol = dateVol;
        this.ville = ville;
        this.avion = avion;
    }

    // --- Getters & Setters ---
    public Long getId() {
        return id;
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

    public Ville getVille() {
        return ville;
    }

    public void setVille(Ville ville) {
        this.ville = ville;
    }

    public Avion getAvion() {
        return avion;
    }

    public void setAvion(Avion avion) {
        this.avion = avion;
    }
}
