package com.circa.mind_speed_api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Table(name = "Games")
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gameId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int difficulty;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime endAt;

    @Column
    private boolean isActive = true;

    @Column(nullable = false)
    private int totalCorrectAnswers = 0;

    @Column(nullable = false)
    private int totalQuestions = 0;

    private double totalTimeSpent = 0;

    private double fastestAnswerTime;
}
