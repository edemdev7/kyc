package com.paykko.kyc.model.dto.response;

import com.paykko.kyc.model.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Réponse à la soumission KYC")
public class KycSubmissionResponseDTO {
    @Schema(description = "Identifiant du membre")
    private String memberId;

    @Schema(description = "Statut de la vérification")
    private Status status;

    @Schema(description = "Message de retour")
    private String message;

    @Schema(description = "Identifiant de la vérification")
    private String verificationId;
}