package com.devKiq.qrcode_generator.controller;

import com.devKiq.qrcode_generator.dto.QrCodeGenerateRequestDTO;
import com.devKiq.qrcode_generator.dto.QrCodeGenerateResponseDTO;
import com.devKiq.qrcode_generator.service.QrCodeGeneratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/qrcode")
public class QrCodeController {

    private final QrCodeGeneratorService qrCodeGeneratorService;

    public QrCodeController(QrCodeGeneratorService qrCodeGeneratorService) {
        this.qrCodeGeneratorService = qrCodeGeneratorService;
    }

    @PostMapping("/generate")
    public ResponseEntity<QrCodeGenerateResponseDTO> generate(@RequestBody QrCodeGenerateRequestDTO request) {
        try {
            QrCodeGenerateResponseDTO response = qrCodeGeneratorService.generateAndUploadQrCode(request.text());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
