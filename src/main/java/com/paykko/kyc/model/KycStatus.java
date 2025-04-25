package com.paykko.kyc.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.paykko.kyc.model.enums.Status;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class KycStatus {
    @Id
    private String memberId;
    @Enumerated(EnumType.STRING)
    private Status status;
    private String reason;
    private Date createdAt;
    private Date updatedAt;
    private int numberOfChecks;

    /**
     * Constructs a new KycStatus instance with the given member ID.
     * Initializes the status to PENDING, sets the createdAt timestamp to the current date,
     * and sets the initial number of checks to 1.
     *
     * @param memberId the unique identifier of the member for which the KYC status is being created
     */
    public KycStatus(String memberId) {
        this.memberId = memberId;
        this.status = Status.PENDING;
        this.createdAt = new Date();
        this.numberOfChecks = 1;
    }

}