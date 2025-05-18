package com.medical.homevisits.auth.workplace.repository;

import com.medical.homevisits.auth.workplace.entity.Workplace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkplaceRepository extends JpaRepository<Workplace, UUID> {
    Optional<Workplace> findByName(String name);
}
