package com.example.demo.homework.service;

import com.example.demo.homework.entity.PlayerEntity;

public interface PlayerService {
    Boolean hasAnswerSet(String uuid);

    Boolean isValidGuess(String answer);

    Boolean setAnswer(String uuid, String answer);

    Boolean checkIfAnswersAlready(String gameUuid);

}
