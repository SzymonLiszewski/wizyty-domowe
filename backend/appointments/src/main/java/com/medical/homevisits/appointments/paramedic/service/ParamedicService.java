package com.medical.homevisits.appointments.paramedic.service;

import com.medical.homevisits.appointments.doctor.entity.Doctor;
import com.medical.homevisits.appointments.doctor.repository.DoctorRepository;
import com.medical.homevisits.appointments.paramedic.entity.Paramedic;
import com.medical.homevisits.appointments.paramedic.repository.ParamedicRepository;

import java.util.List;

import com.medical.homevisits.appointments.workplace.entity.Workplace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Service
public class ParamedicService {
    private final ParamedicRepository paramedicRepository;

    @Autowired
    public ParamedicService(ParamedicRepository paramedicRepository) {
        this.paramedicRepository = paramedicRepository;
    }
    public List<Paramedic> getParamedicByWorkPlace(Workplace place) {
        return paramedicRepository.findByWorkPlace(place);
    }

    
    public void create(Paramedic paramedic){
    	paramedicRepository.save(paramedic);
    }
}
