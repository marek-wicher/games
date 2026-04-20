package com.game.battleship;

import com.game.battleship.logic.GameFactory;
import com.game.battleship.model.Game;
import com.game.battleship.model.Player;
import com.game.battleship.model.Ship;

import java.util.Arrays;
import java.util.List;

import static com.game.battleship.model.Ship.ShipType.BATTLESHIP;
import static com.game.battleship.model.Ship.ShipType.CARRIER;
import static com.game.battleship.model.Ship.ShipType.CRUISER;
import static com.game.battleship.model.Ship.ShipType.DESTROYER;
import static com.game.battleship.model.Ship.ShipType.SUBMARINE;

public class Helpers {
    // ==================== Helper Methods ====================

    /**
     * Sets up a game in PLAYING state with all human ships placed.
     * The human player is set as the current player.
     */
    public static Game setupGameForAttack() {
        var game =  GameFactory.createBasicGame();
        placeAllComputerShips(game);
        placeAllHumanShips(game);
        game.getStatus().setState(Game.GameState.PLAYING);
        game.getStatus().setCurrentPlayer(game.getHuman());
        return game;
    }

    public static Game setupGameForAttack(Player human, Player computer) {
        var game =  Game.builder()
                .human(human)
                .computer(computer)
                .status(Game.GameStatus.builder()
                    .state(Game.GameState.PLACING_SHIPS)
                    .message("Place your ships!")
                    .build())
                .build();
        placeAllComputerShips(game);
        placeAllHumanShips(game);
        game.getStatus().setState(Game.GameState.PLAYING);
        game.getStatus().setCurrentPlayer(game.getHuman());
        return game;
    }

    public static Game setupGameForAttackWithNoComputerShips() {
        var game =  GameFactory.createBasicGame();
        placeAllHumanShips(game);
        game.getStatus().setState(Game.GameState.PLAYING);
        game.getStatus().setCurrentPlayer(game.getHuman());
        return game;
    }

    /**
     * Places all 5 required ships for the human player.
     * This transitions the game to PLAYING state.
     */
    public static void placeAllHumanShips(Game game) {
        List<Ship> ships = Arrays.asList(
                new Ship(CARRIER.getName(), CARRIER.getSize()),
                new Ship(BATTLESHIP.getName(), BATTLESHIP.getSize()),
                new Ship(CRUISER.getName(), CRUISER.getSize()),
                new Ship(SUBMARINE.getName(), SUBMARINE.getSize()),
                new Ship(DESTROYER.getName(), DESTROYER.getSize())
        );
        game.getHuman().getBoard().placeShip(ships.get(0), 0, 0, true);
        game.getHuman().getBoard().placeShip(ships.get(1), 1, 0, true);
        game.getHuman().getBoard().placeShip(ships.get(2), 2, 0, true);
        game.getHuman().getBoard().placeShip(ships.get(3), 3, 0, true);
        game.getHuman().getBoard().placeShip(ships.get(4), 4, 0, true);
    }

    /**
     * Places all 5 required ships for the computer player.
     */
    public static void placeAllComputerShips(Game game) {
        List<Ship> ships = Arrays.asList(
                new Ship(CARRIER.getName(), CARRIER.getSize()),
                new Ship(BATTLESHIP.getName(), BATTLESHIP.getSize()),
                new Ship(CRUISER.getName(), CRUISER.getSize()),
                new Ship(SUBMARINE.getName(), SUBMARINE.getSize()),
                new Ship(DESTROYER.getName(), DESTROYER.getSize())
        );
        int col = 0;
        for (Ship ship : ships) {
            int row = 1;
            col++;
            game.getComputer().getBoard().placeShip(ship, row, col++, false);
        }
    }
}
