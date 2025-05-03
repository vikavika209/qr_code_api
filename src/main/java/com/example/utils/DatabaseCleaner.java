package com.example.utils;

import com.example.repository.QRCodeRepository;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

@Component
public class DatabaseCleaner {

    private final QRCodeRepository qrCodeRepository;

    public DatabaseCleaner(QRCodeRepository qrCodeRepository) {
        this.qrCodeRepository = qrCodeRepository;
    }

    @PreDestroy
    public void clearDatabase() {
        qrCodeRepository.deleteAll();
        System.out.println("База данных очищена перед завершением приложения.");
    }
}
