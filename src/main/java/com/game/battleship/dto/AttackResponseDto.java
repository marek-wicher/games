package com.game.battleship.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttackResponseDto {
    private boolean hit;
    private boolean shipSunk;
    private boolean gameOver;
    private String message;
    private GameStateDto gameState;
}

