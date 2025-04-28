
package com.paykko.kyc.services;

import com.paykko.kyc.common.exception.KycNotFoundException;
import com.paykko.kyc.model.KycStatus;
import com.paykko.kyc.model.KycVerificationResult;
import com.paykko.kyc.model.dto.KycSubmissionDTO;
import com.paykko.kyc.model.dto.KycStatusDTO;
import com.paykko.kyc.model.enums.Status;
import com.paykko.kyc.repository.KycStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class KycService {

    private final KycStatusRepository kycStatusRepository;
    private final AWSRekognitionService rekognitionService;
    private final AWSTextractService textractService;

    private static final double FACIAL_MATCH_THRESHOLD = 90.0;

    public KycStatusDTO submitKyc(KycSubmissionDTO submission) {
        // 1. Vérifier si un KYC existe déjà
        KycStatus existingStatus = kycStatusRepository.findById(submission.getMemberId())
                .orElse(new KycStatus(submission.getMemberId()));

        // 2. Comparaison des visages avec Rekognition
        double facialMatchScore = rekognitionService.compareFaces(
                submission.getPhotoIdUrl(),
                submission.getPhotoSelfieUrl()
        );

        // 3. Extraction du texte avec Textract
        KycVerificationResult verificationResult = textractService.extractAndVerifyData(
                submission.getPhotoIdUrl(),
                submission
        );

        // 4. Évaluation des résultats
        Status newStatus = evaluateResults(facialMatchScore, verificationResult);
        String reason = generateReason(facialMatchScore, verificationResult);

        // 5. Mise à jour du statut
        existingStatus.setStatus(newStatus);
        existingStatus.setReason(reason);
        existingStatus.setUpdatedAt(new Date());
        existingStatus.setNumberOfChecks(existingStatus.getNumberOfChecks() + 1);

        // 6. Sauvegarde en base de données
        KycStatus savedStatus = kycStatusRepository.save(existingStatus);

        // 7. Retour du DTO
        return new KycStatusDTO(
                savedStatus.getMemberId(),
                savedStatus.getStatus(),
                savedStatus.getReason()
        );
    }

    public KycStatusDTO getKycStatus(String memberId) {
        return kycStatusRepository.findById(memberId)
                .map(status -> new KycStatusDTO(
                        status.getMemberId(),
                        status.getStatus(),
                        status.getReason()
                ))
                .orElseThrow(() -> new KycNotFoundException("KYC not found for member: " + memberId));
    }

    private Status evaluateResults(double facialMatchScore, KycVerificationResult verificationResult) {
        if (facialMatchScore < FACIAL_MATCH_THRESHOLD || !verificationResult.isTextualDataMatch()) {
            return Status.REJECTED;
        }
        return Status.VALIDATED;
    }

    private String generateReason(double facialMatchScore, KycVerificationResult verificationResult) {
        StringBuilder reason = new StringBuilder();

        if (facialMatchScore < FACIAL_MATCH_THRESHOLD) {
            reason.append("La correspondance faciale est insuffisante. ");
        }

        if (!verificationResult.isTextualDataMatch()) {
            reason.append("Les données extraites ne correspondent pas aux données déclarées. ");
        }

        return !reason.isEmpty() ? reason.toString().trim() : "Vérification réussie";
    }
}