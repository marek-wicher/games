package com.game.battleship.logic;

import com.game.battleship.model.Game;
import com.game.battleship.model.Ship;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static com.game.battleship.util.Commons.BOARD_SIZE;
import static com.game.battleship.util.Commons.MAX_LOOP_ITERATIONS;
import static com.game.battleship.model.Ship.ShipType.BATTLESHIP;
import static com.game.battleship.model.Ship.ShipType.CARRIER;
import static com.game.battleship.model.Ship.ShipType.CRUISER;
import static com.game.battleship.model.Ship.ShipType.DESTROYER;
import static com.game.battleship.model.Ship.ShipType.SUBMARINE;

@Slf4j
@Component
@RequiredArgsConstructor
@Observed(name = "gameManager")
@Timed("gameMangerTimed")
public class GameManager {
    private final Random numbersGenerator;
    private final MeterRegistry meterRegistry;

    /**
     * Places a ship for the human player on the board. Validates game state,
     * checks for duplicate ships,
     * and updates game state to PLAYING when all ships are placed.
     * @param game the current game instance
     * @param shipType the type of ship to place
     * @param row the starting row position
     * @param col the starting column position
     * @param horizontal whether the ship is placed horizontally
     * @return true if the ship was placed successfully, false otherwise
     */
    @Timed("gameMangerTimed.placeShip")
    public boolean placeShip(Game game, String shipType, int row, int col, boolean horizontal) {
        var result = Optional.ofNullable(game)
            .map(isSuccess -> placeHumanShip(game, shipType, row, col, horizontal))
            .orElseThrow(() ->
                    new IllegalStateException("Game not found in session when trying to place ship")
            );
        log.atDebug()
                .setMessage("Placing ship '{}' at ({}, {}) with horizontal={} was successful: {}")
                .addArgument(shipType)
                .addArgument(row)
                .addArgument(col)
                .addArgument(horizontal)
                .addArgument(result)
                .log();
        return result;
    }

    /**
     * Starts a new game by creating a new Game instance and placing the computer's ships randomly on the board.
     * @return the newly created Game instance with computer ships placed
     */
    @Timed("gameMangerTimed.startNewGame")
    public Game startNewGame() {
        var game = GameFactory.createBasicGame();
        placeComputerShips(game);
        return game;
    }

    private void placeComputerShips(Game game) {
        List<Ship> ships = Arrays.asList(
                new Ship(CARRIER.getName(), CARRIER.getSize()),
                new Ship(BATTLESHIP.getName(), BATTLESHIP.getSize()),
                new Ship(CRUISER.getName(), CRUISER.getSize()),
                new Ship(SUBMARINE.getName(), SUBMARINE.getSize()),
                new Ship(DESTROYER.getName(), DESTROYER.getSize())
        );
        for (Ship ship : ships) {
            boolean placed = false;
            int attempts = 0;
            while (!placed && attempts++ < MAX_LOOP_ITERATIONS) {
                int row = numbersGenerator.nextInt(BOARD_SIZE);
                int col = numbersGenerator.nextInt(BOARD_SIZE);
                boolean horizontal = numbersGenerator.nextDouble() < 0.5;
                placed = game.getComputer().getBoard().placeShip(ship, row, col, horizontal);
                Counter.builder("placeComputerShips.attempt")
                        .tag("ship.type", ship.getName())
                        .register(meterRegistry)
                        .increment();
            }
            if (!placed) {
                throw new IllegalStateException("Failed to perform Computer attack after " + MAX_LOOP_ITERATIONS + " attempts. Check the board state for issues.");
            }
        }
    }

    private boolean placeHumanShip(Game game, String shipType, int row, int col, boolean horizontal) {
        if (game.getStatus().getState() != Game.GameState.PLACING_SHIPS) return false;

        // Check if ship already placed
        if (game.getHuman().getBoard().hasShip(shipType)) return false;

        Ship ship = ShipFactory.createShipByTypeName(shipType);

        boolean placed = game.getHuman().getBoard().placeShip(ship, row, col, horizontal);
        if (placed && game.getHuman().getBoard().getShipsCount() == 5) {
            game.getStatus().setState(Game.GameState.PLAYING);
            game.getStatus().setCurrentPlayer(game.getHuman());
            game.getStatus().setMessage("Your turn! Attack the Computer's board.");
            log.atDebug()
                    .setMessage("All ships placed. Game state changed to PLAYING. Current player: HUMAN")
                    .log();
        }
        Counter.builder("placeHumanShips.attempt")
                .tag("ship.type", ship.getName())
                .register(meterRegistry)
                .increment();
        return placed;
    }
}