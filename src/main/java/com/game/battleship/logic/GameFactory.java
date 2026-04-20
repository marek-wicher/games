package com.game.battleship.logic;

import com.game.battleship.model.Game;
import com.game.battleship.model.Player;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class GameFactory {
    /**
     * Creates a new game with default settings: human player, AI opponent, and computer ships already placed.
     * @return a new Game instance ready for play
     */
    public static Game createBasicGame() {
        return  Game.builder()
            .human(new Player("Human"))
            .computer(new Player("Computer"))
            .status(Game.GameStatus.builder()
                    .state(Game.GameState.PLACING_SHIPS)
                    .message("Place your ships!")
                    .build())
            .build();
    }
}
