package com.game.battleship.controller;

import com.game.battleship.Helpers;
import com.game.battleship.logic.AttackService;
import com.game.battleship.logic.GameManager;
import com.game.battleship.model.Board;
import com.game.battleship.model.Game;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GameController.class)
public class GameControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AttackService attackService;

    @MockitoBean
    private GameManager gameManager;

    @Test
    void testMainViewWithGameInSession() throws Exception {
        mockMvc.perform(get("/")
                        .sessionAttr("game", Helpers.setupGameForAttack()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("abortGameQuestion"))
                .andExpect(model().attribute("abortGameQuestion", true))
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

    @Test
    void testMainViewWithInvalidSession() throws Exception {
        mockMvc.perform(get("/")
                        .sessionAttr("game", new Object()))
                .andExpect(status().isOk())
                .andExpect(model().attributeDoesNotExist("abortGameQuestion"))
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

    @Test
    void testMainViewWithNoGameInSession() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(model().attributeDoesNotExist("abortGameQuestion"))
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

    @Test
    void testGameViewWithGameInSession() throws Exception {
        var gameInSession = Helpers.setupGameForAttack();
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("game", gameInSession);
        mockMvc.perform(get("/game")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("game"))
                .andExpect(model().attribute("game", gameInSession))
                .andExpect(content().contentType("text/html;charset=UTF-8"));
        assertEquals(gameInSession, session.getAttribute("game"),
                "Game in session should be the same as the one in the model and same as provided game in session");
    }

    @Test
    void testGameViewWithInvalidSession() throws Exception {
        when(gameManager.startNewGame()).thenReturn(Helpers.setupGameForAttack());
        var invalidGameObj = new Object();
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("game", invalidGameObj);
        mockMvc.perform(get("/game")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("game"))
                .andExpect(model().attribute("game", session.getAttribute("game")))
                .andExpect(content().contentType("text/html;charset=UTF-8"));
        assertNotEquals(invalidGameObj, session.getAttribute("game"),
                "New game object should be provided in session," +
                        "and should be the same as the one in the model");

    }

    @Test
    void testGameViewWithEmptySession() throws Exception {
        when(gameManager.startNewGame()).thenReturn(Helpers.setupGameForAttack());
        MockHttpSession session = new MockHttpSession();
        mockMvc.perform(get("/game")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("game"))
                .andExpect(model().attribute("game", session.getAttribute("game")))
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

    @Test
    void testAttackSuccessful() throws Exception {
        var game = Helpers.setupGameForAttack();
        game.getStatus().setState(Game.GameState.PLAYING);
        game.getStatus().setCurrentPlayer(game.getHuman());
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("game", game);

        when(attackService.performAttackRound(game, 0, 0)).thenReturn(Board.ATTACK_RESULT.HIT);

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
        var game = Helpers.setupGameForAttack();
        game.getStatus().setState(Game.GameState.PLAYING);
        game.getStatus().setCurrentPlayer(game.getHuman());
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("game", game);

        when(attackService.performAttackRound(game, 1, 1)).thenReturn(Board.ATTACK_RESULT.MISS);

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
        var game = Helpers.setupGameForAttack();
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("game", game);

        mockMvc.perform(post("/attack")
                        .param("row", "-1")
                        .param("col", "0")
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("game"))
                .andExpect(flash().attribute("error", 10));
    }

    @Test
    void testAttackWithNoGameInSession() throws Exception {
        mockMvc.perform(post("/attack")
                        .param("row", "0")
                        .param("col", "0")
                        .session(new MockHttpSession()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("error", 30));
    }
}
