package com.example.config;

import com.example.entity.QRCode;
import com.example.service.QRCodeService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initDatabase(QRCodeService qrCodeService) {
        return args -> {
            QRCode qrCode = new QRCode();
            qrCode.setIdentityDocument("000000000");
            qrCode.setVaccinationId(48L);
            qrCode.setVaccineName("Sputnik");
            qrCode.setVaccinationDate(LocalDate.of(2024, 1, 1));
            qrCode.setHash("158phjlhj");

            qrCodeService.save(qrCode);

            QRCode qrCodeFromData = qrCodeService.findByHash("158phjlhj");

            System.out.println("Данные успешно добавлены в БД!");
            System.out.println(qrCodeFromData.getIdentityDocument().toString());
        };
    }
}
