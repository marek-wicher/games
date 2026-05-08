package com.game.battleship.controller;

import com.game.battleship.dto.*;
import com.game.battleship.logic.AttackService;
import com.game.battleship.logic.GameManager;
import com.game.battleship.model.Board;
import com.game.battleship.model.Game;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/game")
@Tag(name = "Game REST API", description = "Handles game-related REST operations for the Battleship game")
@RequiredArgsConstructor
@Validated
@Timed(value = "gameRestController", description = "Time taken to execute game REST API methods")
@CrossOrigin(origins = {"http://localhost:8080", "http://localhost:5173"}, allowCredentials = "true")
public class GameRestController {
    private final GameManager gameManager;
    private final AttackService attackService;

    @PostMapping("/start")
    @Operation(summary = "Start a new game", description = "Initializes a new Battleship game")
    @Timed("gameRestController.startGame")
    public GameStateDto startGame(HttpSession session) {
        log.atDebug().setMessage("Starting new game...").log();
        Game game = gameManager.startNewGame();
        session.setAttribute("game", game);
        return GameStateDto.fromGame(game);
    }

    @GetMapping("/state")
    @Operation(summary = "Get current game state", description = "Returns the current state of the game")
    @Timed("gameRestController.getGameState")
    public GameStateDto getGameState(HttpSession session) {
        Game game = getGameFromSession(session);
        return GameStateDto.fromGame(game);
    }

    @PostMapping("/place-ship")
    @Operation(summary = "Place a ship on the board", description = "Places a human player's ship at the specified position and orientation")
    @Timed("gameRestController.placeShip")
    public PlaceShipResponseDto placeShip(
            @RequestBody PlaceShipRequestDto request,
            HttpSession session) {
        
        log.atDebug()
                .setMessage("Placing ship: {} at ({}, {}) horizontal: {}")
                .addArgument(request.getShipName())
                .addArgument(request.getRow())
                .addArgument(request.getCol())
                .addArgument(request.isHorizontal())
                .log();

        Game game = getGameFromSession(session);
        boolean success = gameManager.placeShip(game, request.getShipName(), request.getRow(), request.getCol(), request.isHorizontal());
        session.setAttribute("game", game);

        String message = success ? "Ship placed successfully" : "Failed to place ship - invalid position or ship already placed";

        return PlaceShipResponseDto.builder()
                .success(success)
                .message(message)
                .gameState(GameStateDto.fromGame(game))
                .build();
    }

    @PostMapping("/attack")
    @Operation(summary = "Perform an attack", description = "Human player attacks at the specified position on the opponent's board")
    @Timed("gameRestController.attack")
    public AttackResponseDto attack(
            @RequestBody AttackRequestDto request,
            HttpSession session) {
        
        log.atDebug()
                .setMessage("Attacking at ({}, {})")
                .addArgument(request.getRow())
                .addArgument(request.getCol())
                .log();

        Game game = getGameFromSession(session);
        var attackResult = attackService.performAttackRound(game, request.getRow(), request.getCol());
        session.setAttribute("game", game);

        boolean hit = attackResult == Board.ATTACK_RESULT.HIT || attackResult == Board.ATTACK_RESULT.SUNK;
        boolean shipSunk = attackResult == Board.ATTACK_RESULT.SUNK;
        boolean gameOver = game.getStatus().getState() == Game.GameState.GAME_OVER;
        String message = determineAttackMessage(attackResult, game);

        return AttackResponseDto.builder()
                .hit(hit)
                .shipSunk(shipSunk)
                .gameOver(gameOver)
                .message(message)
                .gameState(GameStateDto.fromGame(game))
                .build();
    }

    @PostMapping("/reset")
    @Operation(summary = "Reset the game", description = "Resets the current game and starts a new one")
    @Timed("gameRestController.resetGame")
    public GameStateDto resetGame(HttpSession session) {
        log.atDebug().setMessage("Resetting game...").log();
        Game game = gameManager.startNewGame();
        session.setAttribute("game", game);
        return GameStateDto.fromGame(game);
    }

    private Game getGameFromSession(HttpSession session) {
        return Optional.ofNullable(session.getAttribute("game"))
                .filter(Game.class::isInstance)
                .map(Game.class::cast)
                .orElseThrow(() -> new IllegalStateException("Game not found in session"));
    }

    private String determineAttackMessage(Board.ATTACK_RESULT attackResult, Game game) {
        return switch (attackResult) {
            case FAULT -> "Invalid attack position";
            case MISS -> "Miss! Computer's turn.";
            case HIT -> "Hit! Your turn again.";
            case SUNK -> "Ship sunk! Your turn again.";
            default -> "Unknown attack result";
        };
    }
}