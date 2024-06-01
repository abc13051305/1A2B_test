package com.example.demo.homework.dto;

import lombok.Data;

@Data
public class GameEndResponse {

    private Data data;
    private String message;
    @lombok.Data
    public static class Data {
        private String result = "4A";
        private String winnerId;
    }
}
