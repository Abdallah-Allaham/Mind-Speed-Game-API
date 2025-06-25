package com.circa.mind_speed_api.dto;

import lombok.Data;

@Data
public class BestScoreDetailsDto {
    private String question;
    private double answer;
    private double timeTaken;
}