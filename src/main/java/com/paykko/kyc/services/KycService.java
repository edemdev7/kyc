package com.paykko.kyc.services;

import com.amazonaws.services.s3.AmazonS3;
import com.paykko.kyc.model.KycStatus;
import com.paykko.kyc.model.dto.KycStatusDTO;
import com.paykko.kyc.model.enums.Status;
import com.paykko.kyc.repository.KycStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class KycService {

    private final KycStatusRepository kycStatusRepository;
    private final AmazonS3 amazonS3;
    private static final String BUCKET_NAME = "kyc-uploads-bucket";

    public KycService() {
        this.kycStatusRepository = null;
        this.amazonS3 = null;
    }

    /**
     * Soumet des documents pour vérification KYC
     */
    public KycStatusDTO submitKycDocuments(String memberId, MultipartFile photoId, MultipartFile selfie) {
        try {
            // Upload vers S3
            uploadToS3(memberId, photoId, "photo_id.jpg");
            uploadToS3(memberId, selfie, "selfie.jpg");

            // Créer ou mettre à jour le statut KYC
            KycStatus kycStatus = kycStatusRepository.findById(memberId)
                    .map(status -> {
                        status.setStatus(Status.PENDING);
                        status.setUpdatedAt(new Date());
                        status.setNumberOfChecks(status.getNumberOfChecks() + 1);
                        return status;
                    })
                    .orElse(new KycStatus(memberId));

            kycStatus = kycStatusRepository.save(kycStatus);
            return convertToDTO(kycStatus);

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la soumission des documents KYC: " + e.getMessage());
        }
    }

    /**
     * Récupère le statut KYC d'un membre
     */
    public KycStatusDTO getKycStatus(String memberId) {
        KycStatus status = kycStatusRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Statut KYC non trouvé pour le membre: " + memberId));
        return convertToDTO(status);
    }

    /**
     * Met à jour le statut KYC (utilisé par Lambda)
     */
    public KycStatusDTO updateKycStatus(String memberId, Status newStatus, String reason) {
        KycStatus status = kycStatusRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Statut KYC non trouvé pour le membre: " + memberId));

        status.setStatus(newStatus);
        status.setReason(reason);
        status.setUpdatedAt(new Date());

        status = kycStatusRepository.save(status);
        return convertToDTO(status);
    }

    @SuppressWarnings("UseSpecificCatch")
    private void uploadToS3(String memberId, MultipartFile file, String fileName) {
        try {
            String key = String.format("%s/%s", memberId, fileName);
            amazonS3.putObject(BUCKET_NAME, key, file.getInputStream(), null);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'upload vers S3: " + e.getMessage());
        }
    }

    private KycStatusDTO convertToDTO(KycStatus status) {
        return new KycStatusDTO(
                status.getMemberId(),
                status.getStatus(),
                status.getReason()
        );
    }
}