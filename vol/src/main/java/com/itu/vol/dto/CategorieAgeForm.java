package com.itu.vol.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class CategorieAgeForm {

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 50, message = "Le nom ne peut pas dépasser 50 caractères")
    private String nom;

    @NotNull(message = "L'âge minimum est obligatoire")
    @Min(value = 0, message = "L'âge minimum doit être positif")
    @Max(value = 120, message = "L'âge minimum ne peut pas dépasser 120")
    private Integer ageMin;

    @Min(value = 0, message = "L'âge maximum doit être positif")
    @Max(value = 120, message = "L'âge maximum ne peut pas dépasser 120")
    private Integer ageMax;

    @NotNull(message = "Le multiplicateur est obligatoire")
    @DecimalMin(value = "0.1", message = "Le multiplicateur doit être au moins 0.1")
    @DecimalMax(value = "10.0", message = "Le multiplicateur ne peut pas dépasser 10")
    private BigDecimal multiplicateurPrix;

    @Size(max = 255, message = "La description ne peut pas dépasser 255 caractères")
    private String description;

    // Constructeurs
    public CategorieAgeForm() {
    }

    public CategorieAgeDTO toDTO() {
        CategorieAgeDTO dto = new CategorieAgeDTO();
        dto.setNom(this.nom);
        dto.setAgeMin(this.ageMin);
        dto.setAgeMax(this.ageMax);
        dto.setMultiplicateurPrix(this.multiplicateurPrix);
        dto.setDescription(this.description);
        return dto;
    }

}