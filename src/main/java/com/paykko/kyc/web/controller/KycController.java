
package com.paykko.kyc.web.controller;

import com.paykko.kyc.model.dto.KycStatusDTO;
import com.paykko.kyc.model.dto.KycSubmissionDTO;
import com.paykko.kyc.services.KycService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/kyc")
@RequiredArgsConstructor
@Tag(name = "KYC Controller", description = "Endpoints pour la gestion des vérifications KYC")
public class KycController {

    private final KycService kycService;

    @PostMapping("/submit")
    @Operation(summary = "Soumettre une vérification KYC",
            description = "Soumet les photos et les informations personnelles pour la vérification KYC")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vérification soumise avec succès",
                    content = @Content(schema = @Schema(implementation = KycStatusDTO.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<KycStatusDTO> submitKyc(
            @Valid @RequestBody KycSubmissionDTO submission) {
        KycStatusDTO result = kycService.submitKyc(submission);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/status/{memberId}")
    @Operation(summary = "Obtenir le statut KYC",
            description = "Récupère le statut actuel de la vérification KYC d'un membre")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statut trouvé",
                    content = @Content(schema = @Schema(implementation = KycStatusDTO.class))),
            @ApiResponse(responseCode = "404", description = "KYC non trouvé pour ce membre"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<KycStatusDTO> getKycStatus(
            @Parameter(description = "ID du membre", required = true)
            @PathVariable String memberId) {
        KycStatusDTO status = kycService.getKycStatus(memberId);
        return ResponseEntity.ok(status);
    }
}