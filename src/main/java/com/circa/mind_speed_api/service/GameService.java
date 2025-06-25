package com.circa.mind_speed_api.service;

import com.circa.mind_speed_api.dto.*;
import com.circa.mind_speed_api.entity.Answer;
import com.circa.mind_speed_api.entity.Game;
import com.circa.mind_speed_api.entity.Question;
import com.circa.mind_speed_api.repository.AnswerRepository;
import com.circa.mind_speed_api.repository.GamesRepository;
import com.circa.mind_speed_api.repository.QuestionRepository;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class GameService {

    @Autowired
    private GamesRepository gamesRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private AnswerRepository answerRepository;

    private static final double EPSILON = 0.01;


    public StartGameResponse startGame(StartGameRequest request){
        if (request.getDifficulty() < 1 || request.getDifficulty() > 4){
            throw new IllegalArgumentException("Difficulty must be between 1 and 4");
        }

        Game game = new Game();
        game.setName(request.getName());
        game.setDifficulty(request.getDifficulty());
        game.setCreatedAt(LocalDateTime.now());
        game = gamesRepository.save(game);

        Question question = getQuestion(game);
        question = questionRepository.save(question);

        StartGameResponse response = new StartGameResponse();
        response.setMessage("Hello "+ request.getName()+" find your submit API URL below");
        response.setQuestion(question.getExpression());
        response.setSubmitUrl("/game/"+game.getGameId()+"/submit");
        response.setTimeStarted(game.getCreatedAt());

        return response;
    }

    public SubmitResponse submitAnswer(SubmitRequest request, long gameId){
        Game game = gamesRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found with ID: " + gameId));

        if (!game.isActive()) {
            throw new IllegalStateException("Game with ID: " + gameId + " is not active. Cannot submit answers.");
        }

        Optional<Question> latestUnansweredQuestion = questionRepository.findByGameOrderByCreatedAtDesc(game)
                .stream()
                .filter(q -> answerRepository.findByQuestion_QuestionId(q.getQuestionId()).isEmpty())
                .findFirst();


        Question currentQuestion = latestUnansweredQuestion.orElseThrow(
                () -> new IllegalStateException("No active question found for Game ID: " + gameId + ". Perhaps it's already answered or no questions generated.")
        );

        LocalDateTime submissionTime = LocalDateTime.now();
        double timeTakenSeconds = ChronoUnit.MILLIS.between(currentQuestion.getCreatedAt(), submissionTime) / 1000.0;
        timeTakenSeconds = Math.round(timeTakenSeconds * 100.0) / 100.0;

        boolean isCorrect = Math.abs(request.getAnswer() - currentQuestion.getCorrectAnswer()) < EPSILON;

        Answer answer = new Answer();
        answer.setQuestion(currentQuestion);
        answer.setGame(game);
        answer.setPlayerAnswer(request.getAnswer());
        answer.setSubmittedAt(submissionTime);
        answer.setIsCorrect(isCorrect);
        answer.setTimeTaken(timeTakenSeconds);
        answerRepository.save(answer);

        game.setTotalQuestions(game.getTotalQuestions() + 1);
        if (isCorrect) {
            game.setTotalCorrectAnswers(game.getTotalCorrectAnswers() + 1);
            if (timeTakenSeconds < game.getFastestAnswerTime()) {
                game.setFastestAnswerTime(timeTakenSeconds);
            }
        }
        game.setTotalTimeSpent(game.getTotalTimeSpent() + timeTakenSeconds);
        gamesRepository.save(game);

        Question nextQuestion = getQuestion(game);
        questionRepository.save(nextQuestion);


        SubmitResponse response = new SubmitResponse();
        response.setResult(isCorrect ? "Good job " + game.getName() + ", your answer is correct!" : "Sorry " + game.getName() + ", your answer is incorrect.");
        response.setTimeTaken(timeTakenSeconds);
        response.setCurrentScore(game.getTotalCorrectAnswers() + "/" + game.getTotalQuestions());

        NextQuestion question = new NextQuestion();
        question.setQuestion(nextQuestion.getExpression());
        question.setSubmitUrl("/game/" + game.getGameId() + "/submit");
        question.setCreatedAt(nextQuestion.getCreatedAt());
        response.setNextQuestion(question);

        return response;

    }

    public EndGameResponse endGame(Long gameId) {
        Game game = gamesRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found with ID: " + gameId));

        if (!game.isActive()) {
            throw new IllegalStateException("Game with ID: " + gameId + " is already ended.");
        }
        game.setActive(false);
        game.setEndAt(LocalDateTime.now());
        gamesRepository.save(game);

        List<Question> allQuestions = questionRepository.findByGame(game);
        List<Answer> allAnswers = answerRepository.findByGame(game);

        List<HistoryEntryDto> history = new ArrayList<>();
        for (Question question : allQuestions) {
            Optional<Answer> correspondingAnswer = allAnswers.stream()
                    .filter(ans -> ans.getQuestion().getQuestionId().equals(question.getQuestionId()))
                    .findFirst();

            HistoryEntryDto entry = new HistoryEntryDto();
            entry.setQuestion(question.getExpression());
            entry.setCorrectAnswer(question.getCorrectAnswer());

            if (correspondingAnswer.isPresent()) {
                Answer answer = correspondingAnswer.get();
                entry.setPlayerAnswer(answer.getPlayerAnswer());
                entry.setTimeTaken(answer.getTimeTaken());
                entry.setIsCorrect(answer.getIsCorrect());
            } else {
                entry.setPlayerAnswer(null);
                entry.setTimeTaken(null);
                entry.setIsCorrect(null);
            }
            history.add(entry);
        }

        Optional<Answer> bestAnswer = allAnswers.stream()
                .filter(Answer::getIsCorrect)
                .min(Comparator.comparingDouble(Answer::getTimeTaken));

        BestScoreDetailsDto bestScoreDetails = null;
        if (bestAnswer.isPresent()) {
            Answer ba = bestAnswer.get();
            bestScoreDetails = new BestScoreDetailsDto();
            bestScoreDetails.setQuestion(ba.getQuestion().getExpression());
            bestScoreDetails.setAnswer(ba.getQuestion().getCorrectAnswer());
            bestScoreDetails.setTimeTaken(ba.getTimeTaken());
        }


        EndGameResponse response = new EndGameResponse();
        response.setName(game.getName());
        response.setDifficulty(game.getDifficulty());
        response.setCurrentScore(game.getTotalCorrectAnswers() + "/" + game.getTotalQuestions());
        response.setTotalTimeSpent(game.getTotalTimeSpent());
        response.setBestScoreDetails(bestScoreDetails);
        response.setHistory(history);

        return response;
    }

    private Question getQuestion(Game game) {
        char[] operations = {'+','-','*','/'};
        Random random = new Random();
        int rang = switch (game.getDifficulty()) {
            case 1 -> 10;
            case 2 -> 100;
            case 3 -> 1000;
            case 4 -> 10000;
            default -> 10;
        };

        int num = random.nextInt(rang);
        StringBuilder expressionBuilder = new StringBuilder(String.valueOf(num));

        for (int i = 0; i < game.getDifficulty(); i++) {
            char operation = operations[random.nextInt(operations.length)];

            num = random.nextInt(rang);

            if (operation == '/' && num==0){
                num++;
            }
            expressionBuilder.append(" ").append(operation).append(" ").append(num);
        }

        String expression = expressionBuilder.toString();

        Expression exp = new ExpressionBuilder(expression).build();
        double result = exp.evaluate();

        result = Math.round(result * 100.0)/100.0;


        Question question = new Question();
        question.setExpression(expressionBuilder.toString());
        question.setCorrectAnswer(result);
        question.setGame(game);
        question.setCreatedAt(LocalDateTime.now());

        return question;

    }
}
