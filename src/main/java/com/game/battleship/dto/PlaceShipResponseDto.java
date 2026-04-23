package com.game.battleship.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceShipResponseDto {
    private boolean success;
    private String message;
    private GameStateDto gameState;
}

