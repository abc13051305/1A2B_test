package com.example.demo.homework.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.sql.Timestamp;

@Entity
@Data
@Table(name = "guess_history", schema = "public", catalog = "game")
@EntityListeners(AuditingEntityListener.class)
public class GuessHistoryEntity {
    @Id
    @Column(name = "uuid", columnDefinition = "VARCHAR(36)")
    private String uuid;

    @Basic
    @Column(name = "guess_number")
    private String guessNumber;

    @Basic
    @Column(name = "answer")
    private String answer;

    @Basic
    @Column(name = "round")
    private Integer round;

    @Basic
    @Column(name = "player_id")
    private String playerId;

    @Basic
    @Column(name = "game_id")
    private String gameId;

    @Basic
    @Column(name = "result")
    private String result;

    @Basic
    @CreatedDate
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_date_time", nullable = false, updatable = false)
    private Timestamp createDateTime;

    @Basic
    @LastModifiedDate
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "update_date_time")
    private Timestamp updateDateTime;

}
