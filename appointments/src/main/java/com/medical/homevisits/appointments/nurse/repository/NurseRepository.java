package com.medical.homevisits.appointments.nurse.repository;

import com.medical.homevisits.appointments.nurse.entity.Nurse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NurseRepository extends JpaRepository<Nurse, UUID> {
}
