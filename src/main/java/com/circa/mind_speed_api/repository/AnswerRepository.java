package com.circa.mind_speed_api.repository;

import com.circa.mind_speed_api.entity.Answer;
import com.circa.mind_speed_api.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnswerRepository extends JpaRepository<Answer,Long> {
    Optional<Answer> findByQuestion_QuestionId(Long questionId);
    List<Answer> findByGame(Game game);
}
