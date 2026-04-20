package com.game.battleship.model;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import static com.game.battleship.util.Commons.BOARD_SIZE;

public class Board {
    private static final int SIZE = BOARD_SIZE;
    private final Cell[][] cells;
    private final List<Ship> ships;

    public Board() {
        cells = new Cell[SIZE][SIZE];
        ships = new ArrayList<>();
        initializeBoard();
    }

    public enum ATTACK_RESULT {
        FAULT, MISS, HIT, SUNK
    }

    public Cell[][] getCells() {
        Cell[][] newCell = new Cell[cells.length][];
        for(int i=0;i<cells.length;i++){
            newCell[i] = new Cell[cells[i].length];
            for(int j=0;j<cells[i].length;j++) {
                var cell = cells[i][j];
                newCell[i][j] = new Cell() {
                    @Override
                    public boolean hasShip() {
                        return cell.hasShip();
                    }
                    @Override
                    public boolean isHitShip() {
                        return cell.isHitShip();
                    }
                    @Override
                    public boolean isMiss() {
                        return cell.isMiss();
                    }
                };
            }
        }
        return newCell;
    }

    public int getShipsCount() {
        return CollectionUtils.size(ships);
    }

    private void initializeBoard() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                cells[i][j] = new Cell();
            }
        }
    }

    private Cell getCell(int row, int col) {
        if (row < 0 || row >= SIZE || col < 0 || col >= SIZE) {
            return null;
        }
        return cells[row][col];
    }

    public boolean placeShip(Ship ship, int startRow, int startCol, boolean horizontal) {
        List<Cell> shipCells = new ArrayList<>();
        int endRow = horizontal ? startRow : startRow + ship.getSize() - 1;
        int endCol = horizontal ? startCol + ship.getSize() - 1 : startCol;

        if (endRow >= SIZE || endCol >= SIZE) {
            return false;
        }

        // Check if all cells are free
        for (int i = startRow; i <= endRow; i++) {
            for (int j = startCol; j <= endCol; j++) {
                if (cells[i][j].hasShip()) return false;
            }
        }

        // Place the ship
        for (int i = startRow; i <= endRow; i++) {
            for (int j = startCol; j <= endCol; j++) {
                cells[i][j].setShip(ship);
                shipCells.add(cells[i][j]);
            }
        }
        ship.setCells(shipCells);
        ships.add(ship);
        return true;
    }

    public ATTACK_RESULT attack(int row, int col) {
        Cell cell = getCell(row, col);
        if (cell == null || cell.isHit()) {
            return ATTACK_RESULT.FAULT;
        }
        cell.setHit(true);
        var result = ATTACK_RESULT.MISS;
        if (cell.hasShip()) {
            result = cell.getShip().checkIfSunk() ? ATTACK_RESULT.SUNK : ATTACK_RESULT.HIT;
        }
        return result;
    }

    public boolean allShipsSunk() {
        return ships.stream().allMatch(Ship::isSunk);
    }

    public boolean hasShip(String shipName) {
        return ships.stream().anyMatch(s -> s.getName().equalsIgnoreCase(shipName));
    }
}
