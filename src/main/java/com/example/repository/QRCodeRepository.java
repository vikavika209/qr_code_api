package com.example.repository;

import com.example.entity.QRCode;
import jakarta.persistence.LockModeType;
import org.apache.kafka.common.quota.ClientQuotaAlteration;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface QRCodeRepository extends JpaRepository<QRCode, Long> {
    Optional<QRCode> findByHash(String hash);
    @Query("SELECT qr FROM QRCode qr WHERE qr.identityDocument = :passport ORDER BY qr.vaccinationDate DESC")
    List<QRCode> findByIdentityDocumentOrderedByDate(@Param("passport") String passport, Pageable pageable);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT qr FROM QRCode qr WHERE qr.vaccinationId = :vaccinationId")
    Optional<QRCode> findByVaccinationId(@Param("vaccinationId") Long vaccinationId);
    Optional<QRCode> findByIdentityDocumentAndVaccineNameAndVaccinationDate(String identityDocument, String vaccineName, LocalDate vaccinationDate);
}
