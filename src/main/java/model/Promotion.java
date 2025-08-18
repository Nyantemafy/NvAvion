package model;

import java.time.LocalDateTime;

public class Promotion {
    private Long idPromotion;
    private String nom;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Integer reductionPourcentage;
    private Long idVol;

    // Constructeurs
    public Promotion() {
    }

    public Promotion(String nom, LocalDate dateDebut, LocalDate dateFin, Integer reductionPourcentage, Long idVol) {
        this.nom = nom;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.reductionPourcentage = reductionPourcentage;
        this.idVol = idVol;
    }

    // Getters et Setters
    public Long getIdPromotion() {
        return idPromotion;
    }

    public void setIdPromotion(Long idPromotion) {
        this.idPromotion = idPromotion;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public Integer getReductionPourcentage() {
        return reductionPourcentage;
    }

    public void setReductionPourcentage(Integer reductionPourcentage) {
        this.reductionPourcentage = reductionPourcentage;
    }

    public Long getIdVol() {
        return idVol;
    }

    public void setIdVol(Long idVol) {
        this.idVol = idVol;
    }
}
