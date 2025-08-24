package com.itu.vol.repository;

import com.itu.vol.dto.*;
import com.itu.vol.model.PrixAgeVol;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface PrixAgeVolRepository extends JpaRepository<PrixAgeVol, Long> {

    Optional<PrixAgeVol> findByIdVolAndIdTypeSiegeAndIdCategorieAge(
            Long idVol, Long idTypeSiege, Long idCategorieAge);

    List<PrixAgeVol> findByIdVolOrderByIdTypeSiegeAscIdCategorieAgeAsc(Long idVol);

    List<PrixAgeVol> findByIdVolAndIdTypeSiege(Long idVol, Long idTypeSiege);

    @Query("SELECT pav FROM PrixAgeVol pav JOIN pav.categorieAge ca " +
            "WHERE pav.idVol = :idVol AND pav.idTypeSiege = :idTypeSiege " +
            "AND ca.ageMin <= :age AND (ca.ageMax IS NULL OR ca.ageMax >= :age) " +
            "AND ca.isActive = true")
    Optional<PrixAgeVol> findPrixForVolSiegeAndAge(
            @Param("idVol") Long idVol,
            @Param("idTypeSiege") Long idTypeSiege,
            @Param("age") int age);

    @Query(value = """
            SELECT calculer_prix_age(:idVol, :idTypeSiege, :dateNaissance)
            """, nativeQuery = true)
    BigDecimal calculerPrixAge(
            @Param("idVol") Long idVol,
            @Param("idTypeSiege") Long idTypeSiege,
            @Param("dateNaissance") java.sql.Date dateNaissance);

    void deleteByIdVol(Long idVol);
}