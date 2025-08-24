package com.itu.vol.repository;

import com.itu.vol.model.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface PrixAgeVolRepository extends JpaRepository<PrixAgeVol, Long> {

        List<PrixAgeVol> findByVol(Vol vol);

        boolean existsByVolAndTypeSiegeAndCategorieAge(Vol vol, TypeSiege siege, CategorieAge cat);

        Optional<PrixAgeVol> findByVolAndTypeSiegeAndCategorieAge(Vol vol, TypeSiege siege, CategorieAge cat);

        List<PrixAgeVol> findByVolOrderByTypeSiegeAscCategorieAgeAsc(Vol vol);

        List<PrixAgeVol> findByVolAndTypeSiege(Vol vol, TypeSiege siege);

        @Query("SELECT pav FROM PrixAgeVol pav JOIN pav.categorieAge ca " +
                        "WHERE pav.vol = :vol AND pav.typeSiege = :siege " +
                        "AND ca.ageMin <= :age AND (ca.ageMax IS NULL OR ca.ageMax >= :age) " +
                        "AND ca.isActive = true")
        Optional<PrixAgeVol> findPrixForVolSiegeAndAge(
                        @Param("vol") Vol vol,
                        @Param("siege") TypeSiege siege,
                        @Param("age") int age);

        @Query(value = "SELECT calculer_prix_age(:idVol, :idTypeSiege, :dateNaissance)", nativeQuery = true)
        BigDecimal calculerPrixAge(
                        @Param("idVol") Long idVol,
                        @Param("idTypeSiege") Long idTypeSiege,
                        @Param("dateNaissance") java.sql.Date dateNaissance);

        void deleteByVol(Vol vol);
}
