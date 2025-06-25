package com.circa.mind_speed_api.dto;

import lombok.Data;


@Data
public class SubmitResponse {
    private String result;
    private double timeTaken;
    private NextQuestion nextQuestion;
    private String currentScore;
}

