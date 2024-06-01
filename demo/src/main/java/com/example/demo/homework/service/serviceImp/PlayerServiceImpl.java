package com.example.demo.homework.service.serviceImp;

import com.example.demo.homework.entity.PlayerEntity;
import com.example.demo.homework.repository.PlayerEntityRepository;
import com.example.demo.homework.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PlayerServiceImpl implements PlayerService {
    @Autowired
    private PlayerEntityRepository playerEntityRepository;

    @Override
    public Boolean hasAnswerSet(String uuid) {
        PlayerEntity playerEntity = playerEntityRepository.findById(uuid).get();
        if (playerEntity.getAnswer() == null) {
            return false;
        }
        return true;
    }

    @Override
    public Boolean isValidGuess(String answer) {
        if (answer.length() != 4) {
            return false; // 不是四位數字
        }

        Set<Character> digitSet = new HashSet<>();

        for (char digitChar : answer.toCharArray()) {
            int digit = Character.getNumericValue(digitChar);

            // 檢查每一位數都是介於0-9
            if (digit < 0 || digit > 9) {
                return false;
            }

            // 檢查重複數字
            //利用set的特性(不儲存重複)
            if (!digitSet.add(digitChar)) {
                return false;
            }
        }

        return true;
    }

    @Transactional
    @Override
    public Boolean setAnswer(String uuid, String answer) {

        if (playerEntityRepository.findById(uuid).isPresent()) {
            PlayerEntity playerEntity = playerEntityRepository.findById(uuid).get();
            playerEntity.setAnswer(answer);

            try {
                playerEntityRepository.save(playerEntity);
                return true;
            } catch (Exception e) {
                return false;
            }

        } else {
            return false;
        }

    }

    @Override
    public Boolean checkIfAnswersAlready(String gameUuid) {
        List<PlayerEntity> playerEntityList = playerEntityRepository.findByGameId(gameUuid);
        if(playerEntityList.get(0).getAnswer() != null && playerEntityList.get(1).getAnswer() != null){
            return true;
        }
        return false;
    }

}
