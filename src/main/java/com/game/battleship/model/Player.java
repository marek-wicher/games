package com.game.battleship.model;

import lombok.Getter;

import java.util.Optional;

@Getter
public class Player {
    private final String name;
    private final Board board;

    public Player(String name) {
        this.name = name;
        this.board = new Board();
    }

    public Board.ATTACK_RESULT attack(Player opponent, int row, int col) {
        return Optional.ofNullable(opponent)
                .map(Player::getBoard)
                .map(board  -> board.attack(row, col))
                .orElseThrow(() -> new IllegalArgumentException("Opponent cannot be null"));
    }

    public boolean hasLost() {
        return board.allShipsSunk();
    }
}
