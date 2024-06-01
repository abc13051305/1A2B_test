package com.example.demo.homework.service;

import com.example.demo.homework.dto.GameStatusDTO;
import com.example.demo.homework.entity.GameEntity;


public interface GameService {
    GameEntity createGame();

    Boolean checkIfUuidExists(String uuid);

    Boolean checkIfStateIsGuess(String uuid);

    Boolean checkIfGuesserTurn(String uuid , String guesserId);

    GameStatusDTO getGame(String uuid);

    GameEntity getGameEntity(String uuid);

    void updateGame(GameEntity gameEntity);

    String playGame(String gameId, String guesserId, String guessNumber);

}
