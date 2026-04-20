package com.game.battleship.controller;

import com.game.battleship.Helpers;
import com.game.battleship.logic.AttackService;
import com.game.battleship.logic.GameManager;
import com.game.battleship.model.Game;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static com.game.battleship.util.Commons.ErrorCodes.PAGE_UNKNOWN_ERROR;
import static com.game.battleship.util.Commons.ErrorCodes.VALIDATION_ERROR;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GameController.class)
@DisplayName("GameController - placeShip Method Tests")
class GameControllerPlaceShipTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GameManager gameManager;

    @MockitoBean
    private AttackService attackService;

    private Game testGame;
//    private HttpSession mockSession;

    @BeforeEach
    void setUp() {
        testGame = Helpers.setupGameForAttack();
//        mockSession = mock(HttpSession.class);
    }

    // ==================== Success Cases ====================

    @Test
    @DisplayName("Should place ship successfully when game exists and placement succeeds")
    void shouldPlaceShipSuccessfully() throws Exception {
        // Given
        when(gameManager.placeShip(testGame, "Destroyer", 0, 0, false))
                .thenReturn(true);

        // When & Then
        MvcResult result = mockMvc.perform(
                post("/placeShip")
                        .param("shipName", "Destroyer")
                        .param("row", "0")
                        .param("col", "0")
                        .param("horizontal", "false")
                        .sessionAttr("game", testGame)
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("game"))
                .andExpect(flash().attributeExists("placeShipResult"))
                .andReturn();

        // Verify
        assertTrue((Boolean) result.getFlashMap().get("placeShipResult"));
        verify(gameManager, times(1)).placeShip(testGame, "Destroyer", 0, 0, false);
    }

    @Test
    @DisplayName("Should place ship with horizontal orientation")
    void shouldPlaceShipHorizontally() throws Exception {
        // Given
        when(gameManager.placeShip(testGame, "Battleship", 2, 3, true))
                .thenReturn(true);

        // When & Then
        mockMvc.perform(
                post("/placeShip")
                        .param("shipName", "Battleship")
                        .param("row", "2")
                        .param("col", "3")
                        .param("horizontal", "true")
                        .sessionAttr("game", testGame)
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("game"))
                .andExpect(flash().attributeExists("placeShipResult"))
                .andReturn();

        verify(gameManager).placeShip(testGame, "Battleship", 2, 3, true);
    }

    @Test
    @DisplayName("Should place ship with default horizontal value (false) when not provided")
    void shouldPlaceShipWithDefaultHorizontalValue() throws Exception {
        // Given
        when(gameManager.placeShip(testGame, "Carrier", 1, 1, false))
                .thenReturn(true);

        // When & Then
        mockMvc.perform(
                post("/placeShip")
                        .param("shipName", "Carrier")
                        .param("row", "1")
                        .param("col", "1")
                        // horizontal parameter not provided
                        .sessionAttr("game", testGame)
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("placeShipResult"));

        verify(gameManager).placeShip(testGame, "Carrier", 1, 1, false);
    }

    @Test
    @DisplayName("Should place ship at various valid positions")
    void shouldPlaceShipAtVariousPositions() throws Exception {
        // Given
        int[][] validPositions = {{0, 0}, {5, 5}, {9, 9}, {4, 7}};

        for (int[] pos : validPositions) {
            when(gameManager.placeShip(testGame, "Destroyer", pos[0], pos[1], false))
                    .thenReturn(true);

            // When & Then
            mockMvc.perform(
                    post("/placeShip")
                            .param("shipName", "Destroyer")
                            .param("row", String.valueOf(pos[0]))
                            .param("col", String.valueOf(pos[1]))
                            .param("horizontal", "false")
                            .sessionAttr("game", testGame)
            )
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("game"));
        }
    }

    // ==================== Failure Cases ====================

    @Test
    @DisplayName("Should handle placement failure gracefully")
    void shouldHandlePlacementFailure() throws Exception {
        // Given
        when(gameManager.placeShip(testGame, "Carrier", 0, 0, true))
                .thenReturn(false);

        // When & Then
        MvcResult result = mockMvc.perform(
                post("/placeShip")
                        .param("shipName", "Carrier")
                        .param("row", "0")
                        .param("col", "0")
                        .param("horizontal", "true")
                        .sessionAttr("game", testGame)
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("game"))
                .andExpect(flash().attributeExists("placeShipResult"))
                .andReturn();

        // Verify the result is false
        assertFalse((Boolean) result.getFlashMap().get("placeShipResult"));
    }

    @Test
    @DisplayName("Should handle exception from GameManager")
    void shouldHandleGameManagerException() throws Exception {
        // Given
        when(gameManager.placeShip(any(), anyString(), anyInt(), anyInt(), anyBoolean()))
                .thenThrow(new IllegalArgumentException("Unknown ship type"));

        // When & Then
        mockMvc.perform(
                post("/placeShip")
                        .param("shipName", "InvalidShip")
                        .param("row", "0")
                        .param("col", "0")
                        .param("horizontal", "false")
                        .sessionAttr("game", testGame)
        )
                .andExpect(flash().attribute("error", PAGE_UNKNOWN_ERROR));
    }

    // ==================== Validation Tests ====================

    @Test
    @DisplayName("Should reject row parameter below minimum (less than 0)")
    void shouldRejectRowBelowMinimum() throws Exception {
        // When & Then
        mockMvc.perform(
                post("/placeShip")
                        .param("shipName", "Destroyer")
                        .param("row", "-1")
                        .param("col", "0")
                        .param("horizontal", "false")
                        .sessionAttr("game", testGame)
        )
                .andExpect(flash().attribute("error", VALIDATION_ERROR));

        verify(gameManager, never()).placeShip(any(), any(), anyInt(), anyInt(), anyBoolean());
    }

    @Test
    @DisplayName("Should reject row parameter above maximum (greater than or equal to 10)")
    void shouldRejectRowAboveMaximum() throws Exception {
        // When & Then
        mockMvc.perform(
                post("/placeShip")
                        .param("shipName", "Destroyer")
                        .param("row", "10")
                        .param("col", "0")
                        .param("horizontal", "false")
                        .sessionAttr("game", testGame)
        )
                .andExpect(flash().attribute("error", VALIDATION_ERROR));

        verify(gameManager, never()).placeShip(any(), any(), anyInt(), anyInt(), anyBoolean());
    }

    @Test
    @DisplayName("Should reject col parameter below minimum (less than 0)")
    void shouldRejectColBelowMinimum() throws Exception {
        // When & Then
        mockMvc.perform(
                post("/placeShip")
                        .param("shipName", "Destroyer")
                        .param("row", "0")
                        .param("col", "-1")
                        .param("horizontal", "false")
                        .sessionAttr("game", testGame)
        )
                .andExpect(flash().attribute("error", VALIDATION_ERROR));

        verify(gameManager, never()).placeShip(any(), any(), anyInt(), anyInt(), anyBoolean());
    }

    @Test
    @DisplayName("Should reject col parameter above maximum (greater than or equal to 10)")
    void shouldRejectColAboveMaximum() throws Exception {
        // When & Then
        mockMvc.perform(
                post("/placeShip")
                        .param("shipName", "Destroyer")
                        .param("row", "0")
                        .param("col", "10")
                        .param("horizontal", "false")
                        .sessionAttr("game", testGame)
        )
                .andExpect(flash().attribute("error", VALIDATION_ERROR));

        verify(gameManager, never()).placeShip(any(), any(), anyInt(), anyInt(), anyBoolean());
    }

    @Test
    @DisplayName("Should accept valid boundary values (0 and 9)")
    void shouldAcceptValidBoundaryValues() throws Exception {
        // Given
        when(gameManager.placeShip(testGame, "Destroyer", 0, 9, false))
                .thenReturn(true);

        // When & Then
        mockMvc.perform(
                post("/placeShip")
                        .param("shipName", "Destroyer")
                        .param("row", "0")
                        .param("col", "9")
                        .param("horizontal", "false")
                        .sessionAttr("game", testGame)
        )
                .andExpect(status().is3xxRedirection());

        verify(gameManager).placeShip(testGame, "Destroyer", 0, 9, false);
    }

    @Test
    @DisplayName("Should reject missing shipName parameter")
    void shouldRejectMissingShipName() throws Exception {
        // When & Then
        mockMvc.perform(
                post("/placeShip")
                        .param("row", "0")
                        .param("col", "0")
                        .param("horizontal", "false")
                        .sessionAttr("game", testGame)
        )
                .andExpect(flash().attribute("error", PAGE_UNKNOWN_ERROR));

        verify(gameManager, never()).placeShip(any(), any(), anyInt(), anyInt(), anyBoolean());
    }

    @Test
    @DisplayName("Should reject missing row parameter")
    void shouldRejectMissingRow() throws Exception {
        // When & Then
        mockMvc.perform(
                post("/placeShip")
                        .param("shipName", "Destroyer")
                        .param("col", "0")
                        .param("horizontal", "false")
                        .sessionAttr("game", testGame)
        )
                .andExpect(flash().attribute("error", PAGE_UNKNOWN_ERROR));

        verify(gameManager, never()).placeShip(any(), any(), anyInt(), anyInt(), anyBoolean());
    }

    @Test
    @DisplayName("Should reject missing col parameter")
    void shouldRejectMissingCol() throws Exception {
        // When & Then
        mockMvc.perform(
                post("/placeShip")
                        .param("shipName", "Destroyer")
                        .param("row", "0")
                        .param("horizontal", "false")
                        .sessionAttr("game", testGame)
        )
                .andExpect(flash().attribute("error", PAGE_UNKNOWN_ERROR));

        verify(gameManager, never()).placeShip(any(), any(), anyInt(), anyInt(), anyBoolean());
    }

    // ==================== Session and Game State Tests ====================

    @Test
    @DisplayName("Should handle null game in session by passing null to GameManager")
    void shouldHandleNullGameInSession() throws Exception {
        // When & Then
        mockMvc.perform(
                post("/placeShip")
                        .param("shipName", "Destroyer")
                        .param("row", "0")
                        .param("col", "0")
                        .param("horizontal", "false")
                        // No game in session
        )
                .andExpect(flash().attribute("error", PAGE_UNKNOWN_ERROR));

        // GameManager should not be called
        verify(gameManager, never()).placeShip(any(), any(), anyInt(), anyInt(), anyBoolean());
    }

    @Test
    @DisplayName("Should handle non-Game object in session as null")
    void shouldHandleNonGameObjectInSession() throws Exception {
        // When & Then
        mockMvc.perform(
                post("/placeShip")
                        .param("shipName", "Destroyer")
                        .param("row", "0")
                        .param("col", "0")
                        .param("horizontal", "false")
                        .sessionAttr("game", "This is not a Game object")
        )
                .andExpect(flash().attribute("error", PAGE_UNKNOWN_ERROR));

        // GameManager should not be called
        verify(gameManager, never()).placeShip(any(), any(), anyInt(), anyInt(), anyBoolean());
    }

    @Test
    @DisplayName("Should properly set flash attribute with placement result")
    void shouldSetFlashAttributeWithResult() throws Exception {
        // Given
        when(gameManager.placeShip(testGame, "Submarine", 3, 3, true))
                .thenReturn(true);

        // When
        MvcResult result = mockMvc.perform(
                post("/placeShip")
                        .param("shipName", "Submarine")
                        .param("row", "3")
                        .param("col", "3")
                        .param("horizontal", "true")
                        .sessionAttr("game", testGame)
        )
                .andExpect(flash().attributeExists("placeShipResult"))
                .andReturn();

        // Then
        Object flashAttribute = result.getFlashMap().get("placeShipResult");
        assertNotNull(flashAttribute);
        assertInstanceOf(Boolean.class, flashAttribute);
        assertTrue((Boolean) flashAttribute);
    }

    @Test
    @DisplayName("Should always redirect to game view regardless of placement result")
    void shouldAlwaysRedirectToGameView() throws Exception {
        // Given - placement fails
        when(gameManager.placeShip(testGame, "Carrier", 5, 5, false))
                .thenReturn(false);

        // When & Then
        mockMvc.perform(
                post("/placeShip")
                        .param("shipName", "Carrier")
                        .param("row", "5")
                        .param("col", "5")
                        .param("horizontal", "false")
                        .sessionAttr("game", testGame)
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("game"));
    }

    // ==================== Integration-like Tests ====================

    @Test
    @DisplayName("Should handle multiple consecutive ship placements")
    void shouldHandleMultipleConsecutiveShipPlacements() throws Exception {
        // Given
        String[] shipNames = {"Carrier", "Battleship", "Cruiser", "Submarine", "Destroyer"};
        for (int i = 0; i < shipNames.length; i++) {
            when(gameManager.placeShip(testGame, shipNames[i], i, 0, true))
                    .thenReturn(true);
        }

        // When & Then
        for (int i = 0; i < shipNames.length; i++) {
            mockMvc.perform(
                    post("/placeShip")
                            .param("shipName", shipNames[i])
                            .param("row", String.valueOf(i))
                            .param("col", "0")
                            .param("horizontal", "true")
                            .sessionAttr("game", testGame)
            )
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("game"));
        }

        // Verify all placements were called
        for (int i = 0; i < shipNames.length; i++) {
            verify(gameManager).placeShip(testGame, shipNames[i], i, 0, true);
        }
    }

    @Test
    @DisplayName("Should not place ship if game manager throws IllegalStateException")
    void shouldNotPlaceShipOnIllegalState() throws Exception {
        // Given
        when(gameManager.placeShip(testGame, "Destroyer", 0, 0, false))
                .thenThrow(new IllegalStateException("Game not found in session"));

        // When & Then
        mockMvc.perform(
                post("/placeShip")
                        .param("shipName", "Destroyer")
                        .param("row", "0")
                        .param("col", "0")
                        .param("horizontal", "false")
                        .sessionAttr("game", testGame)
        )
                .andExpect(flash().attribute("error", PAGE_UNKNOWN_ERROR));
    }

    @Test
    @DisplayName("Should pass correct parameters to GameManager")
    void shouldPassCorrectParametersToGameManager() throws Exception {
        // Given
        String shipName = "Battleship";
        int row = 4;
        int col = 7;
        boolean horizontal = true;

        when(gameManager.placeShip(testGame, shipName, row, col, horizontal))
                .thenReturn(true);

        // When
        mockMvc.perform(
                post("/placeShip")
                        .param("shipName", shipName)
                        .param("row", String.valueOf(row))
                        .param("col", String.valueOf(col))
                        .param("horizontal", String.valueOf(horizontal))
                        .sessionAttr("game", testGame)
        )
                .andExpect(status().is3xxRedirection());

        // Then - verify exact parameters
        verify(gameManager, times(1)).placeShip(testGame, shipName, row, col, horizontal);
    }
}

