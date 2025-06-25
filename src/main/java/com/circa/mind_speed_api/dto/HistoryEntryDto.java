package com.circa.mind_speed_api.dto;

import lombok.Data;

@Data
public class HistoryEntryDto {
    private String question;
    private Double playerAnswer;
    private double correctAnswer;
    private Double timeTaken;
    private Boolean isCorrect;
}