package com.game.battleship.controller;

import com.game.battleship.logic.CodesManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j
@Controller
@Tag(name = "QR Code Controller", description = "Handles QR code generation for URLs")
public class QrCodeController {
    private final CodesManager codesManager;

    @Value("${app.base.url:http://localhost/}")
    private String baseUrl;


    public QrCodeController(CodesManager codesManager) {
        this.codesManager = codesManager;
    }

    @GetMapping("qrCode/{page}")
    @Operation(summary = "Generate QR code for URL", description = "Generates a QR code image for the specified page URL.")
    public ResponseEntity<byte[]> getUrlQrCode(@Parameter(description = "The page path to generate QR code for") @PathVariable String page) {

        var qrCode = codesManager.getQRCodeAsBytes(baseUrl + page);

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);

        return new ResponseEntity<> (qrCode, headers, HttpStatus.OK);
    }
}
