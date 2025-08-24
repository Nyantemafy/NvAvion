package com.itu.vol.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "avion")
public class Avion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_avion")
    private Long id;

    @Column(length = 50)
    private String pseudo;

    @Column(name = "siege_business")
    private Integer siegeBusiness;

    @Column(name = "siege_eco")
    private Integer siegeEco;

    @Column(name = "date_fabrication")
    private LocalDate dateFabrication;

    // --- Constructeurs ---
    public Avion() {
    }

    public Avion(String pseudo, Integer siegeBusiness, Integer siegeEco, LocalDate dateFabrication) {
        this.pseudo = pseudo;
        this.siegeBusiness = siegeBusiness;
        this.siegeEco = siegeEco;
        this.dateFabrication = dateFabrication;
    }

    // --- Getters & Setters ---
    public Long getId() {
        return id;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public Integer getSiegeBusiness() {
        return siegeBusiness;
    }

    public void setSiegeBusiness(Integer siegeBusiness) {
        this.siegeBusiness = siegeBusiness;
    }

    public Integer getSiegeEco() {
        return siegeEco;
    }

    public void setSiegeEco(Integer siegeEco) {
        this.siegeEco = siegeEco;
    }

    public LocalDate getDateFabrication() {
        return dateFabrication;
    }

    public void setDateFabrication(LocalDate dateFabrication) {
        this.dateFabrication = dateFabrication;
    }
}
