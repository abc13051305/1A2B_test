package com.example.demo;

import com.example.demo.homework.dto.GameStatusDTO;
import com.example.demo.homework.dto.GuessDTO;
import com.example.demo.homework.entity.GameEntity;
import com.example.demo.homework.service.GameService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
public class GameControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private GameService gameService;
    @Test
    public void testCreateGame() throws Exception {

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/games")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // 驗證回傳狀態碼是否為 200
        result.andExpect(status().isOk())
                // 驗證回傳 JSON
                .andExpect(jsonPath("$.data.gameId").isNotEmpty())
                .andExpect(jsonPath("$.data.player1Id").isNotEmpty())
                .andExpect(jsonPath("$.data.player2Id").isNotEmpty())
                .andExpect(jsonPath("$.message").value("The game has been created."));
    }


    @Test
    void testGetGameStatus() throws Exception {
        String existingGameId = "862de68a-10d3-4bbe-bd33-56ae12f3bf9f";

        when(gameService.checkIfUuidExists(existingGameId)).thenReturn(true);

        GameStatusDTO expectedGameStatus = new GameStatusDTO();
        GameStatusDTO.GameInfo gameInfo = new GameStatusDTO.GameInfo();
        gameInfo.setGameId(existingGameId);
        gameInfo.setState("setting-answer");
        gameInfo.setPlayer1Id("a979efdf-9685-4fdb-8007-98c7ef07c698");
        gameInfo.setPlayer2Id("305f3848-5a80-4791-8f04-79dab22f828a");
        gameInfo.setTurnPlayerId(null); // 因為還未進入猜謎階段
        gameInfo.setGuessHistory(Collections.emptyList());//所以這個也會是空的
        expectedGameStatus.setData(gameInfo);

        when(gameService.getGame(existingGameId)).thenReturn(expectedGameStatus);

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/games/{gameId}", existingGameId)
                .param("gameId", existingGameId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // 驗證回傳狀態碼是否為 200
        result.andExpect(status().isOk())
                // 驗證回傳
                .andExpect(jsonPath("$.data.gameId").value(existingGameId))
                .andExpect(jsonPath("$.data.state").value("setting-answer"))
                .andExpect(jsonPath("$.data.player1Id").value("a979efdf-9685-4fdb-8007-98c7ef07c698"))
                .andExpect(jsonPath("$.data.player2Id").value("305f3848-5a80-4791-8f04-79dab22f828a"))
                .andExpect(jsonPath("$.data.turnPlayerId").doesNotExist())
                .andExpect(jsonPath("$.data.guessHistory").isArray())
                .andExpect(jsonPath("$.data.guessHistory", hasSize(0)));
        
    }

    @Test
    public void testSetAnswer() throws Exception {
        String gameId = "862de68a-10d3-4bbe-bd33-56ae12f3bf9f";
        String playerId = "a979efdf-9685-4fdb-8007-98c7ef07c698";
        String answer = "1234";

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/games/{gameId}/players/{playerId}/answer", gameId, playerId)
                .param("gameId", gameId)
                .param("playerId", playerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"number\":\"" + answer + "\"}")
                .accept(MediaType.APPLICATION_JSON));

        // 驗證回傳狀態碼是否為 200
        //題目是204，但是目前的程式碼是200
        result.andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message").value("The game 862de68a-10d3-4bbe-bd33-56ae12f3bf9f doesn’t exist."));

    }

    @Test
    public void testGuess() throws Exception {

        String gameId = "yourGameId";
        GuessDTO guessDTO = new GuessDTO();
        guessDTO.setGuesserId("yourGuesserId");
        guessDTO.setNumber("1234");

        // 測試失敗的情況
        String nonExistingGameId = "nonExistingGameId";
        GuessDTO invalidGuessDTO = new GuessDTO();
        invalidGuessDTO.setGuesserId("invalidGuesserId");
        invalidGuessDTO.setNumber("12345");  // 超出範圍

        mockMvc.perform(post("/api/v1/games/{gameId}/guess", nonExistingGameId)
                        .param("gameId", nonExistingGameId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
