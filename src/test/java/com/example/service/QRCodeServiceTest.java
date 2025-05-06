package com.example.service;

import com.example.entity.QRCode;
import com.example.repository.QRCodeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QRCodeServiceTest {

    @InjectMocks
    private QRCodeService qrCodeService;

    @Mock
    private QRCodeRepository qrCodeRepository;

    @Test
    void testVerifyQRCode_ValidQRCode_ReturnsTrue() {
        String hash = "validHash";
        QRCode qrCode = new QRCode();
        qrCode.setHash(hash);
        qrCode.setIdentityDocument("123456789");
        qrCode.setVaccinationDate(LocalDate.of(2023, 5, 1));

        when(qrCodeRepository.findByHash(hash)).thenReturn(Optional.of(qrCode));

        boolean result = qrCodeService.verifyQRCode(hash);

        assertTrue(result);
    }

    @Test
    void testVerifyQRCode_QRCodeNotFound_ReturnsFalse() {
        String hash = "notFoundHash";
        when(qrCodeRepository.findByHash(hash)).thenReturn(Optional.empty());

        boolean result = qrCodeService.verifyQRCode(hash);

        assertFalse(result);
    }

    @Test
    void testVerifyQRCode_IdentityDocumentIsNull_ReturnsFalse() {
        String hash = "noIdHash";
        QRCode qrCode = new QRCode();
        qrCode.setHash(hash);
        qrCode.setIdentityDocument(null);
        qrCode.setVaccinationDate(LocalDate.of(2023, 5, 1));

        when(qrCodeRepository.findByHash(hash)).thenReturn(Optional.of(qrCode));

        boolean result = qrCodeService.verifyQRCode(hash);

        assertFalse(result);
    }

    @Test
    void testVerifyQRCode_VaccinationDateBefore2022_ReturnsFalse() {
        String hash = "oldDateHash";
        QRCode qrCode = new QRCode();
        qrCode.setHash(hash);
        qrCode.setIdentityDocument("123456789");
        qrCode.setVaccinationDate(LocalDate.of(2021, 12, 31));

        when(qrCodeRepository.findByHash(hash)).thenReturn(Optional.of(qrCode));

        boolean result = qrCodeService.verifyQRCode(hash);

        assertFalse(result);
    }

    @Test
    public void testSave_WhenQRCodeIsUnique_ShouldSaveSuccessfully() {
        QRCode sampleQRCode = new QRCode();
        sampleQRCode.setId(1L);
        sampleQRCode.setHash("abc123");
        sampleQRCode.setVaccinationId(42L);
        sampleQRCode.setIdentityDocument("123456789");
        sampleQRCode.setVaccinationDate(LocalDate.of(2023, 1, 1));

        when(qrCodeRepository.save(sampleQRCode)).thenReturn(sampleQRCode);

        QRCode saved = qrCodeService.save(sampleQRCode);

        assertNotNull(saved);
        assertEquals(sampleQRCode.getId(), saved.getId());
        verify(qrCodeRepository, times(1)).save(sampleQRCode);
    }

    @Test
    public void testSave_WhenQRCodeIsNotUnique_ShouldNotSaveSuccessfully() {
        QRCode sampleQRCode = new QRCode();
        sampleQRCode.setId(1L);
        sampleQRCode.setHash("abc123");
        sampleQRCode.setVaccinationId(42L);
        sampleQRCode.setIdentityDocument("123456789");
        sampleQRCode.setVaccineName("Спутник V");
        sampleQRCode.setVaccinationDate(LocalDate.of(2023, 1, 1));

        when(qrCodeRepository.findByIdentityDocumentAndVaccineNameAndVaccinationDate(
                "123456789",
                "Спутник V",
                LocalDate.of(2023, 1, 1))
        )
                .thenReturn(Optional.of(sampleQRCode));

        QRCode saved = qrCodeService.save(sampleQRCode);

        assertNotNull(saved);
        assertEquals(sampleQRCode.getId(), saved.getId());
        assertEquals(sampleQRCode.getHash(), saved.getHash());
        verify(qrCodeRepository, times(0)).save(sampleQRCode);
    }
}
