package com.game.battleship.controller;

import com.game.battleship.logic.AttackService;
import com.game.battleship.logic.GameManager;
import com.game.battleship.model.Game;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

import static com.game.battleship.util.Commons.BOARD_SIZE;
import static com.game.battleship.util.Commons.PageViews.GAME_VIEW;
import static com.game.battleship.util.Commons.PageViews.HOME_VIEW;
import static com.game.battleship.util.Commons.PageViews.REDIRECT_TO_GAME_VIEW;

@Slf4j
@Controller
@Tag(name = "Game Controller", description = "Handles game-related operations for the Battleship game")
@RequiredArgsConstructor
public class GameController {
    private final GameManager gameManager;
    private final AttackService attackService;

    @GetMapping("/")
    @Operation(summary = "Display home page", description = "Shows the home page, with option to abort current game if one is in progress.")
    public String index(HttpSession session, Model model) {
        Optional.ofNullable(session.getAttribute("game"))
            .filter(Game.class::isInstance)
            .map(Game.class::cast)
            .ifPresent(g -> {
                log.atDebug()
                        .setMessage("Game in progress..")
                        .log();
                model.addAttribute("abortGameQuestion", true);
            });
        return HOME_VIEW;
    }

    @GetMapping("/game")
    @Operation(summary = "Start or resume game", description = "Starts a new game or resumes existing one, with option to abort the current game.")
    public String game(
            @Parameter(description = "If true, aborts current game and starts a new one",
                    required = false)
            @RequestParam(value = "abortGame") Optional<Boolean> abortGame,
            HttpSession session,
            Model model) {
        var game = Optional.ofNullable(session.getAttribute("game"))
                .filter(Game.class::isInstance)
                .map(Game.class::cast)
                .orElse(null);

        if (game == null || abortGame.orElse(false)) {
            log.atDebug()
                    .setMessage("Starting new game..")
                    .log();
            game = gameManager.startNewGame();
        }
        model.addAttribute("game", game);
        session.setAttribute("game", game);
        return GAME_VIEW;
    }

    @PostMapping("/placeShip")
    @Operation(summary = "Place a ship on the board", description = "Places a human player's ship at the specified position and orientation.")
    public String placeShip(@Parameter(description = "Name of the ship to place") @RequestParam String shipName,
                           @Parameter(description = "Row position on the board") @RequestParam @Max(BOARD_SIZE -1) @Min(0) int row,
                           @Parameter(description = "Column position on the board") @RequestParam @Max(BOARD_SIZE -1) @Min(0) int col,
                           @Parameter(description = "Whether the ship is placed horizontally") @RequestParam Optional<Boolean> horizontal,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        var game = Optional.ofNullable(session.getAttribute("game"))
                .filter(Game.class::isInstance)
                .map(Game.class::cast)
                .orElseThrow(() -> new IllegalStateException("Game not found in session when trying to place ship"));
        var result = gameManager.placeShip(game, shipName, row, col, horizontal.orElse(false));
        redirectAttributes.addFlashAttribute("placeShipResult", result);
        return REDIRECT_TO_GAME_VIEW;
    }

    @PostMapping("/attack")
    @Operation(summary = "Perform an attack", description = "Human player attacks at the specified position on the opponent's board.")
    public String attack(@Parameter(description = "Row position to attack") @RequestParam @Max(BOARD_SIZE -1) @Min(0) int row,
                         @Parameter(description = "Column position to attack") @RequestParam @Max(BOARD_SIZE -1) @Min(0) int col,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        var game = Optional.ofNullable(session.getAttribute("game"))
                .filter(Game.class::isInstance)
                .map(Game.class::cast)
                .orElseThrow(() -> new IllegalStateException("Game not found in session when trying to place ship"));
        var result = attackService.performAttackRound(game, row, col);
        redirectAttributes.addFlashAttribute("attackResult", result);
        return REDIRECT_TO_GAME_VIEW;
    }

    @PostMapping("/newGame")
    @Operation(summary = "Start a new game", description = "Resets and starts a new Battleship game.")
    public String newGame(HttpSession session) {
        var game = gameManager.startNewGame();
        session.setAttribute("game", game);
        return REDIRECT_TO_GAME_VIEW;
    }
}
