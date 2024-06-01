package com.example.demo.homework.dto;

import lombok.Data;

import java.util.List;

@Data
public class GameStatusDTO {
    private GameInfo data;
    @Data
    public static class GameInfo {
        private String gameId;
        private String state;
        private String player1Id;
        private String player2Id;
        private String turnPlayerId;
        private List<String> guessHistory;
    }
}

