package com.game.battleship.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Optional;

public class Ship {
    @Getter
    private final String name;
    @Getter
    private final int size;
    @Setter
    private List<Cell> cells;
    @Getter
    private boolean sunk;

    public Ship(String name, int size) {
        this.name = name;
        this.size = size;
        this.sunk = false;
    }

    public boolean checkIfSunk() {
        sunk = Optional.ofNullable(cells)
                .map(c -> c.stream()
                        .allMatch(Cell::isHit))
                .orElse(false);
        return sunk;
    }

    public enum ShipType {
        CARRIER("Carrier", 5),
        BATTLESHIP("Battleship", 4),
        CRUISER("Cruiser", 3),
        SUBMARINE("Submarine", 3),
        DESTROYER("Destroyer", 2);

        private final String name;
        private final int size;

        ShipType(String name, int size) {
            this.name = name;
            this.size = size;
        }

        public String getName() {
            return name;
        }

        public int getSize() {
            return size;
        }
    }
}
