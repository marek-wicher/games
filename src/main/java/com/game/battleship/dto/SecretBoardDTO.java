package com.game.battleship.dto;

import java.util.ArrayList;
import java.util.List;


public class SecretBoardDTO extends BoardDto {
    private SecretBoardDTO() {
    }
    public static BoardDto decorate(BoardDto boardDto) {
        var secretBoard = new SecretBoardDTO();
        secretBoard.setShipsCount(boardDto.getShipsCount());
        List<List<CellDto>> grid = new ArrayList<>();
        for (List<CellDto> row : boardDto.getGrid()) {
            List<CellDto> secretRow = new ArrayList<>();
            for (CellDto cell : row) {
                CellDto secretCell = new CellDto();
                secretCell.setCol(cell.getCol());
                secretCell.setRow(cell.getRow());
                secretCell.setStatus(cell.getStatus());
                secretRow.add(secretCell);
            }
            grid.add(secretRow);
        }
        secretBoard.setGrid(grid);
        return secretBoard;
    }
}
