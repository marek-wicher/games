package com.game.battleship.controller;

import com.game.battleship.logic.AttackService;
import com.game.battleship.logic.GameFactory;
import com.game.battleship.logic.GameManager;
import com.game.battleship.model.Board;
import com.game.battleship.model.Game;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.game.battleship.util.Commons.ErrorCodes.PAGE_UNKNOWN_ERROR;
import static com.game.battleship.util.Commons.ErrorCodes.VALIDATION_ERROR;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GameController.class)
public class GameControllerAttackTests {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GameManager gameManager;
    @MockitoBean
    private AttackService attackService;
    private Game testGame;

    @BeforeEach
    void setUp() {
        testGame = GameFactory.createBasicGame();
    }

    @Test
    void testAttackSuccessful() throws Exception {
        testGame.getStatus().setState(Game.GameState.PLAYING);
        testGame.getStatus().setCurrentPlayer(testGame.getHuman());
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("game", testGame);

        when(attackService.performAttackRound(testGame, 0, 0)).thenReturn(Board.ATTACK_RESULT.HIT);

        mockMvc.perform(post("/attack")
                        .param("row", "0")
                        .param("col", "0")
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("game"))
                .andExpect(flash().attribute("attackResult", Board.ATTACK_RESULT.HIT));
    }

    @Test
    void testAttackMiss() throws Exception {
        testGame.getStatus().setState(Game.GameState.PLAYING);
        testGame.getStatus().setCurrentPlayer(testGame.getHuman());
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("game", testGame);

        when(attackService.performAttackRound(testGame, 1, 1)).thenReturn(Board.ATTACK_RESULT.MISS);

        mockMvc.perform(post("/attack")
                        .param("row", "1")
                        .param("col", "1")
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("game"))
                .andExpect(flash().attribute("attackResult", Board.ATTACK_RESULT.MISS));
    }

    @Test
    void testAttackWithInvalidParameters() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("game", testGame);

        mockMvc.perform(post("/attack")
                        .param("row", "-1")
                        .param("col", "0")
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("game"))
                .andExpect(flash().attribute("error", VALIDATION_ERROR));
    }

    @Test
    void testAttackWithNoGameInSession() throws Exception {
        mockMvc.perform(post("/attack")
                        .param("row", "0")
                        .param("col", "0")
                        .session(new MockHttpSession()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("error", PAGE_UNKNOWN_ERROR));
    }
}
