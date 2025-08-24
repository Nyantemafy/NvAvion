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

    @Column(name = "id_ville", nullable = false)
    private Long idVille;

    @Column(name = "id_avion")
    private Long idAvion;

    // --- Constructeurs ---
    public Vol() {
    }

    public Vol(String numeroVol, LocalDateTime dateVol, Long idVille, Long idAvion) {
        this.numeroVol = numeroVol;
        this.dateVol = dateVol;
        this.idVille = idVille;
        this.idAvion = idAvion;
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
}
