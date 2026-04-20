package com.game.battleship.logic;

import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static com.game.battleship.util.Commons.QR_CODED_SIZE;
import static com.google.zxing.BarcodeFormat.QR_CODE;

@Slf4j
@Component
public class CodesManager {
    /**
     * Generates a QR code image as a byte array for the given data string.
     * If the data is blank or an error occurs during generation, an empty byte array is returned.
     * @param data
     * @return
     */
    public byte[] getQRCodeAsBytes(String data) {
        if(StringUtils.isBlank(data)) {
            log.atWarn()
                .setMessage("Data for QR code is blank, returning empty byte array")
                .log();
            return new byte [0];
        }
        QRCodeWriter writer = new QRCodeWriter();
        try {
            var bitMatrix = writer.encode(data, QR_CODE, QR_CODED_SIZE, QR_CODED_SIZE);
            var baos = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "png", baos);
            return baos.toByteArray();
        } catch (WriterException | IOException e) {
            log.atDebug()
                .setMessage("Error generating QR code: {}")
                .addArgument(e::getMessage)
                .log();
            return new byte [0];
        }
    }
}
