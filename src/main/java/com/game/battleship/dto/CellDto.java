package com.game.battleship.dto;

import com.game.battleship.model.Cell;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CellDto {
    private int row;
    private int col;
    private String status;
    private boolean hasShip;

    public static CellDto fromCell(Cell cell, int row, int col) {
        return CellDto.builder()
                .row(row)
                .col(col)
                .status(getCellStatus(cell))
                .hasShip(cell.hasShip())
                .build();
    }

    private static String getCellStatus(Cell cell) {
        if (cell.isHitShip()) {
            return "HIT";
        } else if (cell.isMiss()) {
            return "MISS";
        } else {
            return "EMPTY";
        }
    }
}
