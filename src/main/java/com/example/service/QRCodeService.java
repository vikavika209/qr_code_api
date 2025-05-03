package com.example.service;

import com.example.entity.QRCode;
import com.example.repository.QRCodeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class QRCodeService {
    private final QRCodeRepository qrCodeRepository;

    public QRCodeService(QRCodeRepository qrCodeRepository) {
        this.qrCodeRepository = qrCodeRepository;
    }

    @Transactional
    public QRCode save(QRCode qrCode) {

        log.info("Проверка уникальности QR кода: '{}'.", qrCode.getHash());
        QRCode isUnique = qrCodeRepository
                .findByIdentityDocumentAndVaccineNameAndVaccinationDate(
                        qrCode.getIdentityDocument(),
                        qrCode.getVaccineName(),
                        qrCode.getVaccinationDate()
                ).orElse(null);

        if(isUnique == null) {
            log.info("Сохранен новый QR код: " +
                            "паспорт пациента № = {}, " +
                            "вакцина = {}, " +
                            "дата вакцинации = {}, " +
                            "id вакцинации = {}.",
                    qrCode.getIdentityDocument(),
                    qrCode.getVaccineName(),
                    qrCode.getVaccinationDate(),
                    qrCode.getVaccinationId());
        return qrCodeRepository.save(qrCode);
        }

        else {
            log.error("QR: '{}' не уникален.", qrCode.getHash());
            return isUnique;
        }
    }

    public QRCode findByHash(String hash) {
        log.info("Поиск QR кода с hash: {}.", hash);
        QRCode qrCode = qrCodeRepository.findByHash(hash).orElse(null);
        if (qrCode == null) {
            log.info("QR код с hash: {} не найден >>> возврат null.", hash);
        }
        else log.info("QR код с hash: {} найден.", hash);
        return qrCode;
    }

    public QRCode findByPassport(String passport) {
        List<QRCode> qrCodeList = qrCodeRepository.findByIdentityDocumentOrderedByDate(passport, PageRequest.of(0, 1));

        if (qrCodeList.isEmpty()) {
            log.error("QR кода для паспорта: {} не найдены.", passport);
            return null;
        }
        else log.info("QR кода для паспорта: {} найдены.", passport);
        Optional<QRCode> latest = qrCodeList.stream().findFirst();

        return latest.orElse(null);
    }

    @Transactional
    public QRCode update(QRCode qrCode) {
        log.info("Поиск QR кода с id: {}.", qrCode.getId());
        QRCode foundQRCode = qrCodeRepository.findById(qrCode.getId()).orElse(null);
        if (foundQRCode == null) {
            log.info("QR код с id: {} не найден.", qrCode.getId());
            qrCodeRepository.save(qrCode);
            log.info("QR код с id: {} сохранен в базу.", qrCode.getId());
        } else {
            log.info("QR код с id: {} найден.", qrCode.getId());
            qrCodeRepository.save(qrCode);
            log.info("QR код с id: {} обновлён.", qrCode.getId());
        }
        return qrCode;
    }

    public void delete(Long id) {
        log.info("Удаление QR кода с id: {}.", id);
        qrCodeRepository.deleteById(id);
    }

    public boolean verifyQRCode(String hash) {
        QRCode qrCode = findByHash(hash);

        if (qrCode == null) {
            log.error("QR код с хэш: {} не найден.", hash);
            return false;
        }

        LocalDate validFromDate = LocalDate.of(2022, 1, 1);

        if (qrCode.getIdentityDocument() != null &&
                qrCode.getVaccinationDate() != null &&
                qrCode.getVaccinationDate().isAfter(validFromDate)) {
            return true;
        }
        return false;
    }
}
