package com.paykko.kyc.model.dto;

import com.paykko.kyc.model.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KycStatusDTO {
    private String memberId;
    private Status status;
    private String reason;
}