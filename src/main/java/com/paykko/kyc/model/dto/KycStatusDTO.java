package com.paykko.kyc.model.dto;

import com.paykko.kyc.model.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KycStatusDTO {
    private String userId;
    private Status status;
    private String reason;

    public void setUserId(String userId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setStatus(Status status) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setReason(String reason) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}