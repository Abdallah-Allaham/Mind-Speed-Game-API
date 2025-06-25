package com.circa.mind_speed_api.controller;

import com.circa.mind_speed_api.dto.*;
import com.circa.mind_speed_api.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/game")
public class GameController {

    @Autowired
    private GameService gameService;

    @PostMapping("/start")
    public ResponseEntity<StartGameResponse> startGame(@RequestBody StartGameRequest request) {
        return ResponseEntity.ok(gameService.startGame(request));
    }

    @PostMapping("/{gameId}/submit")
    public ResponseEntity<SubmitResponse> submit(
            @PathVariable Long gameId, @RequestBody SubmitRequest request)
    {
        SubmitResponse response = gameService.submitAnswer(request,gameId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{gameId}/end")
    public ResponseEntity<EndGameResponse> end(@PathVariable Long gameId){
        return ResponseEntity.ok(gameService.endGame(gameId));
    }
}
