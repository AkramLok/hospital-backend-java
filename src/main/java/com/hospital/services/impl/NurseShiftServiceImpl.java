package com.hospital.services.impl;

import com.hospital.entities.Nurse;
import com.hospital.entities.NurseShift;
import com.hospital.repositories.NurseRepository;
import com.hospital.repositories.NurseShiftRepository;
import com.hospital.services.NurseShiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class NurseShiftServiceImpl implements NurseShiftService {

    @Autowired
    private NurseShiftRepository nurseShiftRepository;

    @Autowired
    private NurseRepository nurseRepository;

    @Override
    public NurseShift createShift(NurseShift shift) {
        return nurseShiftRepository.save(shift);
    }

    @Override
    public void generateShifts(LocalDate startDate, LocalDate endDate) {
        List<Nurse> nurses = nurseRepository.findAll();
        int nurseCount = nurses.size();
        int nurseIndex = 0;

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            // Assign nurses to day shift
            for (int i = 0; i < 2; i++) {
                NurseShift existingDayShift = nurseShiftRepository.findByShiftDateAndShiftTypeAndNurse(date, "jour", nurses.get((nurseIndex + i) % nurseCount));
                NurseShift dayShift = existingDayShift != null ? existingDayShift : new NurseShift();
                dayShift.setShiftDate(date);
                dayShift.setShiftType("jour");
                dayShift.setNurse(nurses.get((nurseIndex + i) % nurseCount));
                nurseShiftRepository.save(dayShift);
            }

            nurseIndex += 2; // Increment by 2 for the next set of nurses

            // Assign nurses to night shift
            for (int i = 0; i < 2; i++) {
                NurseShift existingNightShift = nurseShiftRepository.findByShiftDateAndShiftTypeAndNurse(date, "nuit", nurses.get((nurseIndex + i) % nurseCount));
                NurseShift nightShift = existingNightShift != null ? existingNightShift : new NurseShift();
                nightShift.setShiftDate(date);
                nightShift.setShiftType("nuit");
                nightShift.setNurse(nurses.get((nurseIndex + i) % nurseCount));
                nurseShiftRepository.save(nightShift);
            }

            nurseIndex += 2; // Increment by 2 for the next set of nurses
        }
    }


    @Override
    public List<NurseShift> getNurseShifts(LocalDate startDate, LocalDate endDate, String shiftType) {
        if (shiftType != null && !shiftType.isEmpty()) {
            return nurseShiftRepository.findByShiftDateBetweenAndShiftType(startDate, endDate, shiftType);
        } else {
            return nurseShiftRepository.findByShiftDateBetween(startDate, endDate);
        }
    }

    @Override
    public NurseShift addShift(LocalDate date, String shiftType, Long nurseId) {
        Nurse nurse = nurseRepository.findById(nurseId)
                .orElseThrow(() -> new RuntimeException("Nurse not found"));

        NurseShift shift = new NurseShift();
        shift.setShiftDate(date);
        shift.setShiftType(shiftType);
        shift.setNurse(nurse);

        return nurseShiftRepository.save(shift);
    }

    @Override
    public NurseShift updateShift(Long shiftId, Long nurseId) {
        NurseShift shift = nurseShiftRepository.findById(shiftId)
                .orElseThrow(() -> new RuntimeException("Shift not found"));

        shift.setNurse(nurseRepository.findById(nurseId)
                .orElseThrow(() -> new RuntimeException("Nurse not found")));

        return nurseShiftRepository.save(shift);
    }

    @Override
    public void deleteShift(Long shiftId) {
        nurseShiftRepository.deleteById(shiftId);
    }
}