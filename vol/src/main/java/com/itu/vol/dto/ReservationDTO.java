package com.itu.vol.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO pour les données de réservation reçues du projet principal
 */
public class ReservationDTO {

    @NotNull
    private Long idReservation;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateReservation;

    private BigDecimal prixTotal;

    private Long idVol;
    private Long idUser;
    private Integer siegeBusiness;
    private Integer siegeEco;

    // Propriétés étendues
    private String numeroVol;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateVol;

    private String villeDestination;
    private String usernameUser;
    private String pseudoAvion;
    private BigDecimal prixBusiness;
    private BigDecimal prixEco;

    // Constructeurs
    public ReservationDTO() {
    }

    // Getters et Setters
    public BigDecimal getPrixBusiness() {
        return prixBusiness;
    }

    public void setPrixBusiness(BigDecimal prixBusiness) {
        this.prixBusiness = prixBusiness;
    }

    public BigDecimal getPrixEco() {
        return prixEco;
    }

    public void setPrixEco(BigDecimal prixEco) {
        this.prixEco = prixEco;
    }

    public Long getIdReservation() {
        return idReservation;
    }

    public void setIdReservation(Long idReservation) {
        this.idReservation = idReservation;
    }

    public LocalDateTime getDateReservation() {
        return dateReservation;
    }

    public void setDateReservation(LocalDateTime dateReservation) {
        this.dateReservation = dateReservation;
    }

    public BigDecimal getPrixTotal() {
        return prixTotal;
    }

    public void setPrixTotal(BigDecimal prixTotal) {
        this.prixTotal = prixTotal;
    }

    public Long getIdVol() {
        return idVol;
    }

    public void setIdVol(Long idVol) {
        this.idVol = idVol;
    }

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
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

    public String getVilleDestination() {
        return villeDestination;
    }

    public void setVilleDestination(String villeDestination) {
        this.villeDestination = villeDestination;
    }

    public String getUsernameUser() {
        return usernameUser;
    }

    public void setUsernameUser(String usernameUser) {
        this.usernameUser = usernameUser;
    }

    public String getPseudoAvion() {
        return pseudoAvion;
    }

    public void setPseudoAvion(String pseudoAvion) {
        this.pseudoAvion = pseudoAvion;
    }

    // Méthodes utilitaires
    public Integer getTotalSieges() {
        int business = siegeBusiness != null ? siegeBusiness : 0;
        int eco = siegeEco != null ? siegeEco : 0;
        return business + eco;
    }

    public boolean hasBusinessSeats() {
        return siegeBusiness != null && siegeBusiness > 0;
    }

    public boolean hasEcoSeats() {
        return siegeEco != null && siegeEco > 0;
    }
}