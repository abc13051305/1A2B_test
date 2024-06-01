package com.example.demo.homework.controller;

import com.example.demo.homework.dto.*;
import com.example.demo.homework.entity.GameEntity;
import com.example.demo.homework.service.GameService;
import com.example.demo.homework.service.GuessHistoryerService;
import com.example.demo.homework.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/games")
public class GameController {
    @Autowired
    GameService gameService;

    @Autowired
    PlayerService playerService;

    @Autowired
    GuessHistoryerService guessHistoryerService;

    //創建一場 1A2B 遊戲
    @PostMapping()
    public GameResponse createGame() {
        GameResponse.Data data = new GameResponse.Data();
        GameResponse gameResponse = new GameResponse();

        GameEntity gameEntity = gameService.createGame();
        data.setGameId(gameEntity.getUuid());
        data.setPlayer1Id(gameEntity.getPlayer1Id());
        data.setPlayer2Id(gameEntity.getPlayer2Id());
        gameResponse.setData(data);
        gameResponse.setMessage("The game has been created.");
        return gameResponse;
    }

    //玩家設置自己的數字答案
    @PutMapping("/{gameId}/players/{playerId}/answer")
    public ResponseEntity<AnswerResponse> setAnswer(@RequestParam String gameId, @RequestParam String playerId , @RequestBody NumberRequestDTO answer) {
        AnswerResponse answerResponse = new AnswerResponse();
        String message = "";

        //    1. 此遊戲存在
        //    2. 此玩家存在這場遊戲中(題目沒提到就先拿掉了)
        if(gameService.checkIfUuidExists(gameId)){
            //    3. 此玩家尚未設置答案。
            if(!playerService.hasAnswerSet(playerId)){
                //   4. 檢查answer是否valid(檢查了0-9、不能重複、長度為4)
                if(playerService.isValidGuess(answer.getNumber())){
                    //   5. 設置答案
                    if(playerService.setAnswer(playerId,answer.getNumber())){
                        message ="The answer has been set.";
                        answerResponse.setMessage(message);

                        //如果雙方皆設置答案，要更新遊戲狀態
                        //因為遊戲開始猜了所以turn_player_id要設為player1_id
                        if(playerService.checkIfAnswersAlready(gameId)){
                            GameEntity gameEntity = gameService.getGameEntity(gameId);
                            gameEntity.setState("guessing");
                            gameEntity.setTurnPlayer("player1");
                            gameService.updateGame(gameEntity);
                        }
                        return ResponseEntity.ok(answerResponse);
                    }
                    message = "set answer failed";
                    answerResponse.setMessage(message);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(answerResponse);
                }
                message ="The answer must be 4 non-repeating digits.";
                answerResponse.setMessage(message);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(answerResponse);
            }
            message ="The player has set up his answer. He can’t change his answer.";
            answerResponse.setMessage(message);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(answerResponse);
        }
        message ="The game "+gameId+" doesn’t exist.";
        answerResponse.setMessage(message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(answerResponse);
    }
    //取得當前遊戲狀態
    @GetMapping("/{gameId}")
    public GameStatusDTO getGameStatus(@RequestParam String gameId) {

        if(gameService.checkIfUuidExists(gameId)){
            return gameService.getGame(gameId);
        }

        return null;
    }
    //玩家輸入四位不重複數字來猜測對方玩家的答案
    @PostMapping("/{gameId}/guess")
    public ResponseEntity<Object> guess(@RequestParam String gameId , @RequestBody GuessDTO guessDTO) {
        AnswerResponse answerResponse = new AnswerResponse();
        String message = new String();

        //    1. 此遊戲存在
        if(gameService.checkIfUuidExists(gameId)){
            //    2. 此遊戲已進入猜謎階段
            if(gameService.checkIfStateIsGuess(gameId)) {
                //3.正輪到此猜謎者的玩家回合。
                if (gameService.checkIfGuesserTurn(gameId, guessDTO.getGuesserId())) {
                    //   4. 檢查guess是否valid(檢查了0-9、不能重複、長度為4)
                    if (playerService.isValidGuess(guessDTO.getNumber())) {
                        String result = gameService.playGame(gameId, guessDTO.getGuesserId(), guessDTO.getNumber());

                        if(result.equals("4A0B")){
                            GameEndResponse gameEndResponse = new GameEndResponse();
                            GameEndResponse.Data data = new GameEndResponse.Data();
                            data.setWinnerId(guessDTO.getGuesserId());
                            gameEndResponse.setData(data);
                            gameEndResponse.setMessage("The player "+ guessDTO.getGuesserId() + " wins!");
                            return ResponseEntity.ok(gameEndResponse);
                        }else{
                            GameContinueResponse gameContinueResponse = new GameContinueResponse();
                            GameContinueResponse.Data data = new GameContinueResponse.Data();
                            data.setResult(result);
                            gameContinueResponse.setData(data);
                            gameContinueResponse.setMessage(result);
                            return ResponseEntity.ok(gameContinueResponse);
                        }
                    }
                    message = "The guess must be 4 non-repeating digits.";
                    answerResponse.setMessage(message);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(answerResponse);
                }
                message ="The player can only guess during his turn!";
                answerResponse.setMessage(message);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(answerResponse);
            }
            message ="The players must set their answers before they guess.";
            answerResponse.setMessage(message);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(answerResponse);
        }
        message ="The game "+gameId+" doesn’t exist.";
        answerResponse.setMessage(message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(answerResponse);
    }



}
