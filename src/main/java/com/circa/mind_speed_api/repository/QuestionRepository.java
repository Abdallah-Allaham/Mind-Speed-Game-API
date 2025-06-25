package com.circa.mind_speed_api.repository;

import com.circa.mind_speed_api.entity.Game;
import com.circa.mind_speed_api.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question,Long> {
    List<Question> findByGameOrderByCreatedAtDesc(Game game);
    List<Question> findByGame(Game game);
}
