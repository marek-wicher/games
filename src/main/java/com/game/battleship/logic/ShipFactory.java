package com.game.battleship.logic;

import com.game.battleship.model.Ship;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ShipFactory {
    /**
     * Creates a ship based on the provided type name.
     * The type name should match one of the predefined ship types
     * (e.g., "CARRIER", "BATTLESHIP", "CRUISER", "SUBMARINE", "DESTROYER").
     * @param typeName
     * @return
     */
    public static Ship createShipByTypeName(String typeName) {
        var shipTypeName = typeName.toUpperCase();

        try {
            var shipType = Ship.ShipType.valueOf(shipTypeName);
            return new Ship(shipType.getName(), shipType.getSize());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown ship type " + shipTypeName);
        }
    }
}
