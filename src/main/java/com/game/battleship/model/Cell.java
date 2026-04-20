package com.game.battleship.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
public class Cell {
    @Accessors(fluent = true) //Change setter name convention
    private boolean hasShip;
    @Setter
    private boolean isHit;
    private Ship ship;

    public Cell() {
        this.hasShip = false;
        this.isHit = false;
        this.ship = null;
    }

    public void setShip(Ship ship) {
        this.ship = ship;
        this.hasShip = ship != null;
    }

    public boolean isHitShip() {
        return hasShip && isHit;
    }

    public boolean isMiss() {
        return !hasShip && isHit;
    }
}
