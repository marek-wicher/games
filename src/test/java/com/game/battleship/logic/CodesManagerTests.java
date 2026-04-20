package com.game.battleship.logic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CodesManagerTests {
    @Test
    void shouldGenerateQRCodeAsBytes() {
            CodesManager codesManager = new CodesManager();
            byte[] qrCodeBytes = codesManager.getQRCodeAsBytes("Test QR Code");
            assertNotNull(qrCodeBytes);
            assertTrue(qrCodeBytes.length > 0);
    }

    @Test
    void shouldReturnEmptyByteArrayForBlankData() {
        CodesManager codesManager = new CodesManager();
        byte[] qrCodeBytes = codesManager.getQRCodeAsBytes("");
        assertNotNull(qrCodeBytes);
        assertEquals(0, qrCodeBytes.length);
    }
}
