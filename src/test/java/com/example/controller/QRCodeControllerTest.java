package com.example.controller;

import com.example.entity.QRCode;
import com.example.service.QRCodeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(QRCodeController.class)
public class QRCodeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QRCodeService qrCodeService;

    private QRCode sampleQRCode;

    @BeforeEach
    void setUp() {
        sampleQRCode = new QRCode();
        sampleQRCode.setId(1L);
        sampleQRCode.setIdentityDocument("123456789");
        sampleQRCode.setVaccinationId(42L);
        sampleQRCode.setVaccineName("Sputnik");
        sampleQRCode.setVaccinationDate(LocalDate.of(2023, 1, 1));
        sampleQRCode.setHash("abc123");
    }

    @Test
    public void testGetQRCodeByPassport_ReturnsQRCode() throws Exception {
        when(qrCodeService.findByPassport("123456789")).thenReturn(sampleQRCode);

        mockMvc.perform(get("/qr/123456789"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.identityDocument").value("123456789"))
                .andExpect(jsonPath("$.vaccineName").value("Sputnik"));
    }

    @Test
    public void testGetQRCodeByPassport_ReturnsNull() throws Exception {
        when(qrCodeService.findByPassport("123456789")).thenReturn(null);

        mockMvc.perform(get("/qr/123456789"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testVerifyQRCode_ReturnsTrue() throws Exception {
        when(qrCodeService.verifyQRCode("abc123")).thenReturn(true);

        mockMvc.perform(get("/qr/check").param("code", "abc123"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    public void testVerifyQRCode_ReturnsFalse() throws Exception {
        when(qrCodeService.verifyQRCode("invalid")).thenReturn(false);

        mockMvc.perform(get("/qr/check").param("code", "invalid"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }
}
