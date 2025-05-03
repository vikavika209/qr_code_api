package com.example.controller;

import com.example.entity.QRCode;
import com.example.service.QRCodeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/qr")
public class QRCodeController {
    private final QRCodeService qrCodeService;

    public QRCodeController(QRCodeService qrCodeService) {
        this.qrCodeService = qrCodeService;
    }

    @GetMapping("/{passport}")
    public ResponseEntity<QRCode> getQRCodeByPassport(@PathVariable("passport") String passport) {
        QRCode qrCode = qrCodeService.findByPassport(passport);

        if(qrCode == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(qrCode);
    }

    @GetMapping("/check")
    public ResponseEntity<Boolean> verifyQRCode(@RequestParam("code") String code) {
        boolean isValid = qrCodeService.verifyQRCode(code);
        return ResponseEntity.ok().body(isValid);
    }
}
