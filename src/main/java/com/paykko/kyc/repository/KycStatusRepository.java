package com.paykko.kyc.repository;

import com.paykko.kyc.model.KycStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KycStatusRepository extends JpaRepository<KycStatus, String> {
    KycStatus findByUserId(String userId);
}