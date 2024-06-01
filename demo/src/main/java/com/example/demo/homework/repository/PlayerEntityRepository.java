package com.example.demo.homework.repository;

import com.example.demo.homework.entity.PlayerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerEntityRepository extends JpaRepository<PlayerEntity, String> {
    List<PlayerEntity> findByGameId(String gameId);
}