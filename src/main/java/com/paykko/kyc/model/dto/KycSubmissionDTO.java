
package com.paykko.kyc.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KycSubmissionDTO {
    private String photoIdUrl;
    private String photoSelfieUrl;
    private String firstName;
    private String lastName;
    private String gender;
    private String dateOfBirth;
    private String memberId;
}