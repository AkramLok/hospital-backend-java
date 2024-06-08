package com.hospital.controllers;

import com.hospital.entities.Antecedent;
import com.hospital.payload.response.MessageResponse;
import com.hospital.services.AntecedentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/antecedents")
public class AntecedentController {

    @Autowired
    private AntecedentService antecedentService;

    @PostMapping("/patient/{patientId}")
    public ResponseEntity<?> addAntecedentToPatient(
            @PathVariable Long patientId,
            @RequestBody Antecedent antecedent) {
        antecedentService.addAntecedentToPatient(patientId, antecedent);
        return ResponseEntity.ok(new MessageResponse("Antecedent added to patient successfully!"));
    }
}