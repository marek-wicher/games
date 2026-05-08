package com.game.battleship.logic;

import com.game.battleship.model.Board;
import com.game.battleship.model.Game;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

import static com.game.battleship.util.Commons.BOARD_SIZE;
import static com.game.battleship.util.Commons.MAX_LOOP_ITERATIONS;

@Slf4j
@Service
@RequiredArgsConstructor
@Observed(name = "attackService")
@Timed("attackServiceTimed")
public class AttackService {
    private final Random numbersGenerator;
    /**
     * Processes an attack from the human player on the AI's board.
     * Validates game state and current player,
     * updates game state to GAME_OVER if the AI loses,
     * and triggers the AI's turn if the attack is successful.
     * @param game the current game instance
     * @param row the row position to attack
     * @param col the column position to attack
     * @return the result of the attack
     */
    @Timed("attackServiceTimed.performAttackRound")
    public Board.ATTACK_RESULT performAttackRound(Game game, int row, int col) {
        var humanAttackResult = Optional.ofNullable(game)
                .map(isSuccess -> humanAttack(game, row, col))
                .orElseThrow(() -> {
                    log.atError()
                            .setMessage("Game not found in session when trying to attack")
                            .log();
                    return new IllegalStateException("Game not found in session when trying to attack");
                });
        if(!Board.ATTACK_RESULT.FAULT.equals(humanAttackResult)
                && Game.GameState.PLAYING.equals(game.getStatus().getState())) {
            computerAttack(game);
        }
        log.atDebug()
                .setMessage("Human attacking position at ({}, {}) was successful: {}. " +
                        "Computer attack performed as well")
                .addArgument(row)
                .addArgument(col)
                .addArgument(humanAttackResult)
                .log();
        return humanAttackResult;
    }

    private Board.ATTACK_RESULT humanAttack(Game game, int row, int col) {
        if (game.getStatus().getState() != Game.GameState.PLAYING || game.getStatus().getCurrentPlayer() != game.getHuman()) return Board.ATTACK_RESULT.FAULT;

        var hit = game.getHuman().attack(game.getComputer(), row, col);
        if (!hit.equals(Board.ATTACK_RESULT.FAULT)) {
            game.getStatus().setCurrentPlayer(game.getComputer());
            if (game.getComputer().hasLost()) {
                game.getStatus().setState(Game.GameState.GAME_OVER);
                game.getStatus().setMessage("You win!");
            }
        }
        return hit;
    }

    private void computerAttack(Game game) {
        if (game.getStatus().getState() != Game.GameState.PLAYING || game.getStatus().getCurrentPlayer() != game.getComputer()) {
            throw new IllegalStateException("Invalid game state or current player when trying to perform computer turn");
        }

        var attacked = Board.ATTACK_RESULT.FAULT;
        int attempts = 0;
        while (attacked.equals(Board.ATTACK_RESULT.FAULT)
                && attempts++ < MAX_LOOP_ITERATIONS) {
            int row = numbersGenerator.nextInt(BOARD_SIZE);
            int col = numbersGenerator.nextInt(BOARD_SIZE);
            attacked = game.getComputer().attack(game.getHuman(), row, col);
        }
        if (attacked.equals(Board.ATTACK_RESULT.FAULT)) {
            throw new IllegalStateException("Failed to perform Computer attack after " + MAX_LOOP_ITERATIONS + " attempts. Check the board state for issues.");
        }
        if (game.getHuman().hasLost()) {
            game.getStatus().setState(Game.GameState.GAME_OVER);
            game.getStatus().setMessage("Computer wins!");
        } else {
            game.getStatus().setCurrentPlayer(game.getHuman());
            game.getStatus().setMessage("Computer attacked. Your turn!");
        }
    }
}
