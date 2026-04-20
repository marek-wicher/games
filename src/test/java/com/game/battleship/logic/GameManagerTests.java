package com.game.battleship.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;

import static com.game.battleship.util.Commons.BOARD_SIZE;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameManagerTests {
    private GameManager gameManager;

    @BeforeEach
    void setUp() {
        var random = new SecureRandom();
        gameManager = new GameManager(random);

    }

    // ==================== placeShip Tests ====================

    @Test
    void shouldThrowExceptionFromPlaceShip() {
        assertThrows(IllegalStateException.class, () -> {
            gameManager.placeShip(null, "Destroyer", 0, 0, true);
        });
    }

    @Test
    void shouldThrowExceptionForUnknownShipType() {
        var game = gameManager.startNewGame();
        assertThrows(IllegalArgumentException.class, () -> {
            gameManager.placeShip(game, "unknown", 0, 0, true);
        });
    }

    @Test
    void shouldPlaceShip() {
        var game = gameManager.startNewGame();
        var result = gameManager.placeShip(game, "Destroyer", 0, 0, true);
        assertTrue(result);
    }

    @Test
    void shouldNotPlaceShipOutsideBoard() {
        var game = gameManager.startNewGame();
        var result = gameManager.placeShip(game, "Destroyer", BOARD_SIZE, 0, true);
        assertFalse(result);
    }
}
