package com.example.demo.homework.service.serviceImp;

import com.example.demo.homework.dto.GameStatusDTO;
import com.example.demo.homework.entity.GameEntity;
import com.example.demo.homework.entity.GuessHistoryEntity;
import com.example.demo.homework.entity.PlayerEntity;
import com.example.demo.homework.repository.GameEntityRepository;
import com.example.demo.homework.repository.GuessHistoryEntityRepository;
import com.example.demo.homework.repository.PlayerEntityRepository;
import com.example.demo.homework.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class GameServiceImpl implements GameService {
    @Autowired
    private GameEntityRepository gameEntityRepository;
    @Autowired
    private GuessHistoryEntityRepository guessHistoryEntityRepository;
    @Autowired
    private PlayerEntityRepository playerEntityRepository;
    @Transactional
    @Override
    public GameEntity createGame() {
        GameEntity gameEntity = new GameEntity();
        PlayerEntity player1Entity = new PlayerEntity();
        PlayerEntity player2Entity = new PlayerEntity();

        String gameId = UUID.randomUUID().toString();
        String player1Id = UUID.randomUUID().toString();
        String player2Id = UUID.randomUUID().toString();

        player1Entity.setUuid(player1Id);
        player1Entity.setGameId(gameId);

        player2Entity.setUuid(player2Id);
        player2Entity.setGameId(gameId);

        //預設狀態為"setting-answer"
        gameEntity.setState("setting-answer");
        gameEntity.setUuid(gameId);
        gameEntity.setPlayer1Id(player1Id);
        gameEntity.setPlayer2Id(player2Id);
        //還沒開始玩，預設回合數為0
        gameEntity.setRounds(0);

        playerEntityRepository.save(player1Entity);
        playerEntityRepository.save(player2Entity);
        return gameEntityRepository.save(gameEntity);
    }

    @Override
    public Boolean checkIfUuidExists(String uuid) {
        if(gameEntityRepository.existsById(uuid)){
            GameEntity gameEntity = gameEntityRepository.findById(uuid).get();
            return true;
        }
        return false;
    }

    @Override
    public Boolean checkIfStateIsGuess(String uuid) {
        if (gameEntityRepository.findById(uuid).get().getState().equals("guessing")) {
            return true;
        }
        return false;
    }

    @Override
    public Boolean checkIfGuesserTurn(String uuid, String guesserId) {
        GameEntity gameEntity = gameEntityRepository.findById(uuid).get();
        String turnPlayerId = new String();
        if(gameEntity.getTurnPlayer().equals("player1")){
            turnPlayerId =gameEntity.getPlayer1Id();
        }else if(gameEntity.getTurnPlayer().equals("player2")){
            turnPlayerId = gameEntity.getPlayer2Id();
        }

        if(turnPlayerId.equals(guesserId)){
            return true;
        }
        return false;
    }

    @Override
    public GameStatusDTO getGame(String uuid) {
        GameStatusDTO gameStatusDTO = new GameStatusDTO();
        GameStatusDTO.GameInfo data = new GameStatusDTO.GameInfo();

        GameEntity gameEntity = gameEntityRepository.findById(uuid).get();
        data.setGameId(gameEntity.getUuid());
        data.setState(gameEntity.getState());
        data.setPlayer1Id(gameEntity.getPlayer1Id());
        data.setPlayer2Id(gameEntity.getPlayer2Id());

        //這個欄位只有當遊戲進入猜謎階段時才會顯示
        if(gameEntity.getState().equals("guessing")){
            if(gameEntity.getTurnPlayer().equals("player1")){
                data.setTurnPlayerId(gameEntity.getPlayer1Id());
            }else if(gameEntity.getTurnPlayer().equals("player2")){
                data.setTurnPlayerId(gameEntity.getPlayer2Id());
            }

            if(guessHistoryEntityRepository.existsByGameId(gameEntity.getUuid())) {
                List<String> guessHistory = new ArrayList<>();
                List<GuessHistoryEntity> guessHistoryEntityList =  guessHistoryEntityRepository.findByPlayerId(data.getTurnPlayerId());

                Collections.sort(guessHistoryEntityList, Comparator.comparingInt(GuessHistoryEntity::getRound));

                for (GuessHistoryEntity historyEntity : guessHistoryEntityList) {
                    guessHistory.add(historyEntity.getGuessNumber());
                }
                data.setGuessHistory(guessHistory);
            }
        }
        gameStatusDTO.setData(data);
        return gameStatusDTO;
    }

    @Override
    public GameEntity getGameEntity(String uuid) {
        return gameEntityRepository.findById(uuid).get();
    }

    @Transactional
    @Override
    public void updateGame(GameEntity gameEntity) {
        gameEntityRepository.save(gameEntity);
    }

    @Override
    public String playGame(String gameId, String guesserId, String guessNumber) {
        GuessHistoryEntity guessHistoryEntity = new GuessHistoryEntity();
        GameEntity gameEntity = gameEntityRepository.findById(gameId).get();

        int A = 0;
        int B = 0;
        boolean check[]=new boolean[4];
        String answerId;
        String answer = new String();
        String result = new String();
        String guessHistoryEntityId = UUID.randomUUID().toString();

        if(gameEntity.getTurnPlayer().equals("player1")){
            answerId = gameEntity.getPlayer1Id();
            gameEntity.setTurnPlayer("player2");
            answer = playerEntityRepository.findById(answerId).get().getAnswer();
        }else if(gameEntity.getTurnPlayer().equals("player2")){
            answerId = gameEntity.getPlayer2Id();
            gameEntity.setTurnPlayer("player1");
            answer = playerEntityRepository.findById(answerId).get().getAnswer();
        }

        //檢查有幾A
        for(int i=0;i<4;i++){
            if(answer.charAt(i)==guessNumber.charAt(i)){
                A++;
                check[i]=true;
            }
        }
        //檢查有幾B
        for(int i=0;i<4;i++){
            for(int j=0;j<4;j++){
                if(!check[j] && answer.charAt(j)==guessNumber.charAt(i)){
                    B++;
                    check[j]=true;
                    break;
                }
            }
        }
        result = A+"A"+B+"B";
        gameEntity.setRounds(gameEntity.getRounds()+1);
        gameEntity.setGuessHistoryId(guessHistoryEntityId);

        guessHistoryEntity.setUuid(guessHistoryEntityId);
        guessHistoryEntity.setGuessNumber(guessNumber);
        guessHistoryEntity.setAnswer(answer);
        guessHistoryEntity.setPlayerId(guesserId);
        guessHistoryEntity.setGameId(gameId);
        guessHistoryEntity.setResult(result);
        guessHistoryEntity.setRound(gameEntity.getRounds());

        gameEntityRepository.save(gameEntity);
        guessHistoryEntityRepository.save(guessHistoryEntity);

        return result;
    }

}
