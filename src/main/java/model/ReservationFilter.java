package model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ReservationFilter {
    private String numeroVol;
    private String villeDestination;
    private String username;
    private BigDecimal prixMin;
    private BigDecimal prixMax;
    private LocalDate dateReservationDebut;
    private LocalDate dateReservationFin;
    private LocalDate dateVolDebut;
    private LocalDate dateVolFin;
    private Boolean hasBusinessSeats;
    private Boolean hasEcoSeats;
    private Integer minSieges;
    private Integer maxSieges;

    // Constructeur
    public ReservationFilter() {
    }

    // Getters et Setters
    public String getNumeroVol() {
        return numeroVol;
    }

    public void setNumeroVol(String numeroVol) {
        this.numeroVol = numeroVol;
    }

    public String getVilleDestination() {
        return villeDestination;
    }

    public void setVilleDestination(String villeDestination) {
        this.villeDestination = villeDestination;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public BigDecimal getPrixMin() {
        return prixMin;
    }

    public void setPrixMin(BigDecimal prixMin) {
        this.prixMin = prixMin;
    }

    public BigDecimal getPrixMax() {
        return prixMax;
    }

    public void setPrixMax(BigDecimal prixMax) {
        this.prixMax = prixMax;
    }

    public LocalDate getDateReservationDebut() {
        return dateReservationDebut;
    }

    public void setDateReservationDebut(LocalDate dateReservationDebut) {
        this.dateReservationDebut = dateReservationDebut;
    }

    public LocalDate getDateReservationFin() {
        return dateReservationFin;
    }

    public void setDateReservationFin(LocalDate dateReservationFin) {
        this.dateReservationFin = dateReservationFin;
    }

    public LocalDate getDateVolDebut() {
        return dateVolDebut;
    }

    public void setDateVolDebut(LocalDate dateVolDebut) {
        this.dateVolDebut = dateVolDebut;
    }

    public LocalDate getDateVolFin() {
        return dateVolFin;
    }

    public void setDateVolFin(LocalDate dateVolFin) {
        this.dateVolFin = dateVolFin;
    }

    public Boolean getHasBusinessSeats() {
        return hasBusinessSeats;
    }

    public void setHasBusinessSeats(Boolean hasBusinessSeats) {
        this.hasBusinessSeats = hasBusinessSeats;
    }

    public Boolean getHasEcoSeats() {
        return hasEcoSeats;
    }

    public void setHasEcoSeats(Boolean hasEcoSeats) {
        this.hasEcoSeats = hasEcoSeats;
    }

    public Integer getMinSieges() {
        return minSieges;
    }

    public void setMinSieges(Integer minSieges) {
        this.minSieges = minSieges;
    }

    public Integer getMaxSieges() {
        return maxSieges;
    }

    public void setMaxSieges(Integer maxSieges) {
        this.maxSieges = maxSieges;
    }

    @Override
    public String toString() {
        return "ReservationFilter{" +
                "numeroVol='" + numeroVol + '\'' +
                ", villeDestination='" + villeDestination + '\'' +
                ", username='" + username + '\'' +
                ", prixMin=" + prixMin +
                ", prixMax=" + prixMax +
                ", dateReservationDebut=" + dateReservationDebut +
                ", dateReservationFin=" + dateReservationFin +
                ", hasBusinessSeats=" + hasBusinessSeats +
                ", hasEcoSeats=" + hasEcoSeats +
                '}';
    }
}