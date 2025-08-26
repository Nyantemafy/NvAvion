package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Reservation {
    private Long idReservation;
    private LocalDateTime dateReservation;
    private BigDecimal prixTotal;
    private Long idVol;
    private Long idUser;
    private Integer siegeBusiness;
    private Integer siegeEco;

    // Propriétés étendues (pour les jointures et l'affichage)
    private String numeroVol;
    private LocalDateTime dateVol;
    private String villeDestination;
    private String usernameUser;
    private String pseudoAvion;
    private Map<Long, Integer> siegeBusinessParCategorie;
    private Map<Long, Integer> siegeEcoParCategorie;

    // Constructeurs
    public Reservation() {
    }

    public Reservation(LocalDateTime dateReservation, BigDecimal prixTotal, Long idVol, Long idUser,
            Integer siegeBusiness, Integer siegeEco) {
        this.dateReservation = dateReservation;
        this.prixTotal = prixTotal;
        this.idVol = idVol;
        this.idUser = idUser;
        this.siegeBusiness = siegeBusiness;
        this.siegeEco = siegeEco;
    }

    // Getters et Setters
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

    // Propriétés étendues
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

    public Map<Long, Integer> getSiegeBusinessParCategorie() {
        if (siegeBusinessParCategorie == null) {
            siegeBusinessParCategorie = new HashMap<>();
        }
        return siegeBusinessParCategorie;
    }

    public void setSiegeBusinessParCategorie(Map<Long, Integer> siegeBusinessParCategorie) {
        this.siegeBusinessParCategorie = siegeBusinessParCategorie;
    }

    public Map<Long, Integer> getSiegeEcoParCategorie() {
        if (siegeEcoParCategorie == null) {
            siegeEcoParCategorie = new HashMap<>();
        }
        return siegeEcoParCategorie;
    }

    public void setSiegeEcoParCategorie(Map<Long, Integer> siegeEcoParCategorie) {
        this.siegeEcoParCategorie = siegeEcoParCategorie;
    }

    // Méthodes utilitaires pour faciliter l'ajout de sièges
    public void addSiegeBusiness(Long categorieId, Integer quantite) {
        getSiegeBusinessParCategorie().put(categorieId, quantite);
    }

    public void addSiegeEco(Long categorieId, Integer quantite) {
        getSiegeEcoParCategorie().put(categorieId, quantite);
    }

    // Méthode pour convertir les données en format Map pour
    // calculateReservationPrice
    public Map<Long, Map<String, Integer>> getQuantitiesForCalculation() {
        Map<Long, Map<String, Integer>> quantities = new HashMap<>();

        // Pour chaque catégorie dans siegeBusinessParCategorie
        for (Map.Entry<Long, Integer> entry : getSiegeBusinessParCategorie().entrySet()) {
            Long categorieId = entry.getKey();
            Integer quantite = entry.getValue();

            Map<String, Integer> seats = quantities.getOrDefault(categorieId, new HashMap<>());
            seats.put("business", quantite);
            quantities.put(categorieId, seats);
        }

        // Pour chaque catégorie dans siegeEcoParCategorie
        for (Map.Entry<Long, Integer> entry : getSiegeEcoParCategorie().entrySet()) {
            Long categorieId = entry.getKey();
            Integer quantite = entry.getValue();

            Map<String, Integer> seats = quantities.getOrDefault(categorieId, new HashMap<>());
            seats.put("eco", quantite);
            quantities.put(categorieId, seats);
        }

        return quantities;
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

    @Override
    public String toString() {
        return "Reservation{" +
                "idReservation=" + idReservation +
                ", dateReservation=" + dateReservation +
                ", prixTotal=" + prixTotal +
                ", idVol=" + idVol +
                ", idUser=" + idUser +
                ", siegeBusiness=" + siegeBusiness +
                ", siegeEco=" + siegeEco +
                ", numeroVol='" + numeroVol + '\'' +
                '}';
    }
}