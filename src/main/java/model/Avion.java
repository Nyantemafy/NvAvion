package model;

import java.time.LocalDate;

public class Avion {
    private Long idAvion;
    private String pseudo;
    private Integer siegeBusiness;
    private Integer siegeEco;
    private LocalDate dateFabrication;

    // Constructeurs
    public Avion() {
    }

    public Avion(String pseudo, Integer siegeBusiness, Integer siegeEco, LocalDate dateFabrication) {
        this.pseudo = pseudo;
        this.siegeBusiness = siegeBusiness;
        this.siegeEco = siegeEco;
        this.dateFabrication = dateFabrication;
    }

    // Getters et Setters
    public Long getIdAvion() {
        return idAvion;
    }

    public void setIdAvion(Long idAvion) {
        this.idAvion = idAvion;
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
