package com.paykko.kyc.services;

import com.paykko.kyc.model.KycStatus;
import com.paykko.kyc.model.dto.KycStatusDTO;
import com.paykko.kyc.model.enums.Status;
import com.paykko.kyc.repository.KycStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class KycStatusService {

    @Autowired
    private KycStatusRepository kycStatusRepository;

    // Générer des données fictives
    public void generateMockData(int count) {
        List<KycStatus> mockData = new ArrayList<>();
        Status[] statuses = Status.values();

        for (int i = 0; i < count; i++) {
            KycStatus status = new KycStatus();
            status.setUserId(UUID.randomUUID().toString());
            status.setStatus(statuses[i % statuses.length]);
            
            if (status.getStatus() == Status.REJECTED) {
                status.setReason("Document non valide");
            }
            
            mockData.add(status);
        }

        kycStatusRepository.saveAll(mockData);
    }

    // Récupérer tous les statuts
    public List<KycStatusDTO> getAllStatuses() {
        return kycStatusRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Récupérer un statut par ID
    public KycStatusDTO getStatusById(String userId) {
        return kycStatusRepository.findById(userId)
                .map(this::toDTO)
                .orElse(null);
    }

    // Convertir Entity vers DTO
    private KycStatusDTO toDTO(KycStatus entity) {
        KycStatusDTO dto = new KycStatusDTO();
        dto.setUserId(entity.getUserId());
        dto.setStatus(entity.getStatus());
        dto.setReason(entity.getReason());
        return dto;
    }
}