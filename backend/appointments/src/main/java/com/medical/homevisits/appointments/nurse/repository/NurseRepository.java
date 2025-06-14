package com.medical.homevisits.appointments.nurse.repository;

import com.medical.homevisits.appointments.nurse.entity.Nurse;
import com.medical.homevisits.appointments.workplace.entity.Workplace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NurseRepository extends JpaRepository<Nurse, UUID> {
    List<Nurse> findByWorkPlace(Workplace workPlace);
}
