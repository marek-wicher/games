package com.game.battleship.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class Game {
    private Player human;
    private Player computer;
    private GameStatus status;

    public enum GameState {
        PLACING_SHIPS, PLAYING, GAME_OVER
    }

    @Getter
    @Setter
    @Builder
    public static class GameStatus {
        private Player currentPlayer;
        private GameState state;
        private String message;
    }
}
