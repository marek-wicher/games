package com.game.battleship.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceShipRequestDto {
    private String shipName;
    private int row;
    private int col;
    private boolean horizontal;
}

