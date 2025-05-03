package com.example.service;

import com.example.dto.VaccinationEventDto;
import com.example.entity.QRCode;
import com.example.repository.QRCodeRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class QRCodeListener {
    private final QRCodeService qrCodeService;
    private final ObjectMapper objectMapper;

    public QRCodeListener(QRCodeService qrCodeService, ObjectMapper objectMapper) {
        this.qrCodeService = qrCodeService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "vaccination-topic", groupId = "qrcode-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeVaccinationMessage(String message) throws JsonProcessingException {
        log.info(">>> Получение данных из топика 'vaccination-topic'.");
        VaccinationEventDto vaccinationEvent = objectMapper.readValue(message, VaccinationEventDto.class);

        String rawData = vaccinationEvent.getVaccinationId() + vaccinationEvent.getIdentityDocument() +
                vaccinationEvent.getVaccineName() + vaccinationEvent.getVaccinationDate();
        String hash = DigestUtils.md5DigestAsHex(rawData.getBytes(StandardCharsets.UTF_8));

        QRCode qrCode = new QRCode();
        qrCode.setVaccinationId(vaccinationEvent.getVaccinationId());
        qrCode.setIdentityDocument(vaccinationEvent.getIdentityDocument());
        qrCode.setVaccineName(vaccinationEvent.getVaccineName());
        qrCode.setVaccinationDate(vaccinationEvent.getVaccinationDate());
        qrCode.setHash(hash);

        qrCodeService.save(qrCode);
    }
}
