package com.game.battleship.dto;

import com.game.battleship.model.Board;
import com.game.battleship.model.Cell;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardDto {
    private List<List<CellDto>> grid;
    private int shipsCount;

    public static BoardDto fromBoard(Board board) {
        Cell[][] cells = board.getCells();
        List<List<CellDto>> grid = new ArrayList<>();

        for (int row = 0; row < cells.length; row++) {
            List<CellDto> rowCells = new ArrayList<>();
            for (int col = 0; col < cells[row].length; col++) {
                Cell cell = cells[row][col];
                CellDto cellDto = CellDto.fromCell(cell, row, col);
                rowCells.add(cellDto);
            }
            grid.add(rowCells);
        }

        return BoardDto.builder()
                .grid(grid)
                .shipsCount(board.getShipsCount())
                .build();
    }
}
