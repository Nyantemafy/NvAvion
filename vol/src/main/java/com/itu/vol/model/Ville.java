package com.itu.vol.model;

import jakarta.persistence.*;

@Entity
@Table(name = "ville")
public class Ville {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ville")
    private Long id;

    @Column(length = 50)
    private String nom;

    // --- Constructeurs ---
    public Ville() {
    }

    public Ville(String nom) {
        this.nom = nom;
    }

    // --- Getters & Setters ---
    public Long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }
}
