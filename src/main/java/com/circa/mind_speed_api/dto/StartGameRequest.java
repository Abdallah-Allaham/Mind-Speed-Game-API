package com.circa.mind_speed_api.dto;

import lombok.Data;

@Data
public class StartGameRequest {
    private String name;
    private int difficulty;
}
