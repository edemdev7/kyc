package com.paykko.kyc.web.controller;

import com.paykko.kyc.model.dto.KycStatusDTO;
import com.paykko.kyc.services.KycStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/kyc")
public class KycController {

    @Autowired
    private KycStatusService kycStatusService;

    // Générer des données de test
    @PostMapping("/generate-mock-data")
    public String generateMockData(@RequestParam(defaultValue = "5") int count) {
        kycStatusService.generateMockData(count);
        return count + " enregistrements fictifs générés avec succès";
    }

    // Récupérer tous les statuts
    @GetMapping("/statuses")
    public List<KycStatusDTO> getAllStatuses() {
        return kycStatusService.getAllStatuses();
    }

    // Récupérer un statut spécifique
    @GetMapping("/status/{userId}")
    public KycStatusDTO getStatus(@PathVariable String userId) {
        return kycStatusService.getStatusById(userId);
    }
}
