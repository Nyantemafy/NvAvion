package com.itu.vol.repository;

import com.itu.vol.dto.*;
import com.itu.vol.model.CategorieAge;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategorieAgeRepository extends JpaRepository<CategorieAge, Long> {

    List<CategorieAge> findByIsActiveTrueOrderByAgeMin();

    @Query("SELECT ca FROM CategorieAge ca WHERE ca.ageMin <= :age " +
            "AND (ca.ageMax IS NULL OR ca.ageMax >= :age) AND ca.isActive = true")
    Optional<CategorieAge> findCategorieForAge(@Param("age") int age);

    @Query("SELECT ca FROM CategorieAge ca WHERE ca.nom = :nom AND ca.isActive = true")
    Optional<CategorieAge> findByNomAndIsActiveTrue(@Param("nom") String nom);

    @Query("SELECT ca FROM CategorieAge ca WHERE ca.isActive = true")
    List<CategorieAge> findByIsActiveTrue();

}
