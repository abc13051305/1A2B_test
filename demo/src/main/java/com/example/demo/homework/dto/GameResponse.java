package com.example.demo.homework.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
public class GameResponse {

    private Data data;
    private String message;
    @lombok.Data
    public static class Data {
        private String gameId;
        private String player1Id;
        private String player2Id;

    }
}
