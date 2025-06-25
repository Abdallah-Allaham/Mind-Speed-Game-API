package com.circa.mind_speed_api.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StartGameResponse {
    private String message;
    private String submitUrl;
    private String question;
    private LocalDateTime timeStarted;
}
