package com.game.battleship.logic;

import com.game.battleship.model.Board;
import com.game.battleship.model.Game;
import com.game.battleship.model.Player;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;

import static com.game.battleship.Helpers.placeAllHumanShips;
import static com.game.battleship.Helpers.setupGameForAttack;
import static com.game.battleship.Helpers.setupGameForAttackWithNoComputerShips;
import static com.game.battleship.util.Commons.BOARD_SIZE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

class AttackServiceTests {
    private GameManager gameManager;
    private AttackService attackService;

    @BeforeEach
    void setUp() {
        var random = new SecureRandom();
        gameManager = new GameManager(random, new SimpleMeterRegistry());
        attackService = new AttackService(random);

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

    // ==================== attack Tests ====================

    @Test
    void shouldThrowExceptionWhenGameIsNull() {
        assertThrows(IllegalStateException.class, () -> {
            attackService.performAttackRound(null, 0, 0);
        });
    }

    @Test
    void shouldReturnFaultWhenGameNotInPlayingState() {
        var game = gameManager.startNewGame();
        // Game starts in PLACING_SHIPS state
        var result = attackService.performAttackRound(game, 0, 0);
        assertEquals(Board.ATTACK_RESULT.FAULT, result);
    }

    @Test
    void shouldReturnFaultWhenCurrentPlayerIsNotHuman() {
        var game = gameManager.startNewGame();
        // Setup game to PLAYING state
        placeAllHumanShips(game);
        // Set current player to AI instead of human
        game.getStatus().setCurrentPlayer(game.getComputer());

        var result = attackService.performAttackRound(game, 0, 0);
        assertEquals(Board.ATTACK_RESULT.FAULT, result);
    }

    @Test
    void shouldReturnMissWhenAttackingEmptyCell() {
        var game = setupGameForAttack();
        // Attack an empty cell (no ship)
        var result = attackService.performAttackRound(game, 5, 5);
        assertEquals(Board.ATTACK_RESULT.MISS, result);
    }

    @Test
    void shouldReturnHitWhenAttackingShip() {
        var game = setupGameForAttack();
        // Place a ship on AI board at position (0, 0)
        var ship = ShipFactory.createShipByTypeName("Destroyer");
        game.getComputer().getBoard().placeShip(ship, 0, 0, true);

        var result = attackService.performAttackRound(game, 0, 0);
        assertEquals(Board.ATTACK_RESULT.HIT, result);
    }

    @Test
    void shouldReturnSunkWhenAllShipCellsAreHit() {
        var game = setupGameForAttack();
        // The Destroyer (size 2) on AI board at position (9, 1)

        // Hit first cell
        var result = attackService.performAttackRound(game, 1, 9);
        // Hit second cell - should sink the ship
        result = attackService.performAttackRound(game, 2, 9);
        assertEquals(Board.ATTACK_RESULT.SUNK, result);
    }

    @Test
    void shouldTriggerComputerAttackAfterSuccessfulHit() {
        var computer = new Player("Computer");
        var computerSpy = spy(computer);
        var game = setupGameForAttack(new Player("Human"), computerSpy);
//        var ship = ShipFactory.createShipByTypeName("Destroyer");
//        game.getAi().getBoard().placeShip(ship, 0, 0, true);

        // Before attack, current player should be human
        assertEquals(game.getHuman(), game.getStatus().getCurrentPlayer());

        // After successful hit, Computer should take turn automatically
        attackService.performAttackRound(game, 0, 0);
        // Verify that computer's attack method was called, indicating it took its turn
        verify(computerSpy).attack(eq(game.getHuman()), anyInt(), anyInt());
    }

    @Test
    void shouldChangeGameStateToGameOverWhenComputerIsDefeated() {
        var game = setupGameForAttackWithNoComputerShips();
        // Place only one ship (Destroyer) on AI board
        var ship = ShipFactory.createShipByTypeName("Destroyer");
        game.getComputer().getBoard().placeShip(ship, 0, 0, true);

        // Sink the only ship
        attackService.performAttackRound(game, 0, 0);
        var result = attackService.performAttackRound(game, 0, 1);

        assertEquals(Board.ATTACK_RESULT.SUNK, result);
        assertEquals(Game.GameState.GAME_OVER, game.getStatus().getState());
    }

    @Test
    void shouldReturnFaultWhenAttackingSamePositionTwice() {
        var game = setupGameForAttack();
        // First attack on empty cell
        attackService.performAttackRound(game, 4, 4);
        // Try to attack same position again
        var result = attackService.performAttackRound(game, 4, 4);
        assertEquals(Board.ATTACK_RESULT.FAULT, result);
    }

    @Test
    void shouldNotChangeGameStateWhenAttackMisses() {
        var game = setupGameForAttack();
        var initialState = game.getStatus().getState();
        attackService.performAttackRound(game, 6, 6);
        assertEquals(initialState, game.getStatus().getState());
    }

    @Test
    void shouldSetProperMessageAfterSuccessfulAttack() {
        var game = setupGameForAttack();
        var ship = ShipFactory.createShipByTypeName("Destroyer");
        game.getComputer().getBoard().placeShip(ship, 2, 2, true);

        // Miss
        attackService.performAttackRound(game, 8, 8);
        var messageAfterMiss = game.getStatus().getMessage();

        // Hit
        attackService.performAttackRound(game, 2, 2);
        var messageAfterHit = game.getStatus().getMessage();

        // Messages should be updated
        assertTrue(messageAfterMiss.contains("AI") || messageAfterMiss.contains("attacked"));
        assertTrue(messageAfterHit.contains("AI") || messageAfterHit.contains("attacked"));
    }
}
