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
@Table(name = "game", schema = "public", catalog = "game")
@EntityListeners(AuditingEntityListener.class)
public class GameEntity {
    @Id
    @Column(name = "uuid", columnDefinition = "VARCHAR(36)")
    private String uuid;

    @Basic
    @Column(name = "player1_id")
    private String player1Id;

    @Basic
    @Column(name = "player2_id")
    private String player2Id;

    @Basic
    @Column(name = "turn_player")
    private String turnPlayer;

    @Basic
    @Column(name = "state")
    private String state;

    @Basic
    @Column(name = "rounds")
    private Integer rounds;

    @Basic
    @Column(name = "guess_history_id")
    private String guessHistoryId;

    @Basic
    @CreatedDate
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_date_time", updatable = false)
    private Timestamp createDateTime;

    @Basic
    @LastModifiedDate
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "update_date_time")
    private Timestamp updateDateTime;

}
