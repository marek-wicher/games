package com.game.battleship.dto;

import com.game.battleship.model.Game;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameStateDto {
    private BoardDto humanBoard;
    private BoardDto computerBoard;
    private String gameStatus;
    private String currentPlayer;
    private String message;
    private boolean gameOver;
    private String winner;

    public static GameStateDto fromGame(Game game) {
        return GameStateDto.builder()
                .humanBoard(BoardDto.fromBoard(game.getHuman().getBoard()))
                .computerBoard(BoardDto.fromBoard(game.getComputer().getBoard()))
                .gameStatus(game.getStatus().getState().name())
                .currentPlayer(game.getStatus().getCurrentPlayer().getName())
                .message(game.getStatus().getMessage())
                .gameOver(game.getStatus().getState() == Game.GameState.GAME_OVER)
                .winner(determineWinner(game))
                .build();
    }

    private static String determineWinner(Game game) {
        if (game.getStatus().getState() != Game.GameState.GAME_OVER) {
            return null;
        }
        if (game.getHuman().getBoard().allShipsSunk()) {
            return "COMPUTER";
        }
        if (game.getComputer().getBoard().allShipsSunk()) {
            return "HUMAN";
        }
        return null;
    }
}
