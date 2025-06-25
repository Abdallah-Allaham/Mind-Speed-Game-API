package com.circa.mind_speed_api.dto;

import lombok.Data;
import java.util.List;

@Data
public class EndGameResponse {
    private String name;
    private int difficulty;
    private String currentScore;
    private double totalTimeSpent;
    private BestScoreDetailsDto bestScoreDetails;
    private List<HistoryEntryDto> history;
}