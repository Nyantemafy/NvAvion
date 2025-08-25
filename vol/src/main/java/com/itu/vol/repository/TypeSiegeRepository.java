package com.itu.vol.repository;

import com.itu.vol.model.TypeSiege;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeSiegeRepository extends JpaRepository<TypeSiege, Integer> {
}
