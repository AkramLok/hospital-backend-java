package com.hospital.services.impl;

import com.hospital.entities.Bed;
import com.hospital.entities.BedState;
import com.hospital.entities.Patient;
import com.hospital.entities.Sector;
import com.hospital.repositories.BedRepository;
import com.hospital.repositories.PatientRepository;
import com.hospital.repositories.SectorRepository;
import com.hospital.services.BedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class BedServiceImpl implements BedService {

    @Autowired
    private BedRepository bedRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private SectorRepository sectorRepository;

    @Override
    public Bed assignPatientToBed(Long bedId, Long patientId) {
        Optional<Bed> bedOptional = bedRepository.findById(bedId);
        Optional<Patient> patientOptional = patientRepository.findById(patientId);

        if (!bedOptional.isPresent()) {
            throw new RuntimeException("Bed not found");
        }

        if (!patientOptional.isPresent()) {
            throw new RuntimeException("Patient not found");
        }

        Bed bed = bedOptional.get();
        Patient patient = patientOptional.get();

        if (bed.getCurrentPatient() != null) {
            throw new RuntimeException("Bed is not empty");
        }

        if (patient.getBed() != null) {
            throw new RuntimeException("Patient is already assigned to another bed");
        }

        // Assign the patient to the bed and update the state
        bed.setCurrentPatient(patient);
        patient.setBed(bed);
        bed.setStartDateTime(LocalDateTime.now());
        bed.setState(BedState.OCCUPIED);
        return bedRepository.save(bed);
    }


    @Override
    public Bed removePatientFromBed(Long bedId) {
        Optional<Bed> bedOptional = bedRepository.findById(bedId);

        if (!bedOptional.isPresent()) {
            throw new RuntimeException("Bed not found");
        }

        Bed bed = bedOptional.get();
        Patient patient = bed.getCurrentPatient();

        if (patient == null) {
            throw new RuntimeException("No patient assigned to the bed");
        }

        // Remove the patient from the bed and update the state
        patient.setBed(null);
        bed.setCurrentPatient(null);
        bed.setState(BedState.EMPTY);
        bed.setStartDateTime(null);

        patientRepository.save(patient);
        return bedRepository.save(bed);
    }


    @Override
    public Bed updateBedState(Long bedId) {
        Optional<Bed> bedOptional = bedRepository.findById(bedId);

        if (bedOptional.isPresent()) {
            Bed bed = bedOptional.get();

            // Logic to determine the new state
            // For now, toggle between EMPTY and OCCUPIED as an example
            if (bed.getState() == BedState.OCCUPIED) {
                bed.setState(BedState.EMPTY);
            } else {
                bed.setState(BedState.OCCUPIED);
            }

            return bedRepository.save(bed);
        } else {
            throw new RuntimeException("Bed not found");
        }
    }

    @Override
    public List<Bed> getAllBeds() {
        return bedRepository.findAll();
    }

    @Override
    public Bed getBedById(Long id) {
        return bedRepository.findById(id).orElseThrow(() -> new RuntimeException("Bed not found"));
    }

    @Override
    public List<Bed> getBedsBySectorId(Long sectorId) {
        return bedRepository.findBySectorId(sectorId);
    }


    @Override
    public Bed addBedToSector(Long sectorId) {
        Optional<Sector> sectorOptional = sectorRepository.findById(sectorId);

        if (sectorOptional.isPresent()) {
            Sector sector = sectorOptional.get();
            Bed bed = new Bed();
            bed.setSector(sector);
            bed.setState(BedState.EMPTY);
            return bedRepository.save(bed);
        } else {
            throw new RuntimeException("Sector not found");
        }
    }

}
