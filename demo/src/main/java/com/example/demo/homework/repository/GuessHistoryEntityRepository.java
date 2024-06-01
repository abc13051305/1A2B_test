package com.example.demo.homework.repository;

import com.example.demo.homework.entity.GuessHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GuessHistoryEntityRepository extends JpaRepository<GuessHistoryEntity, String> {
    List<GuessHistoryEntity> findByPlayerId(String playerId);

    boolean existsByGameId(String uuid);
}