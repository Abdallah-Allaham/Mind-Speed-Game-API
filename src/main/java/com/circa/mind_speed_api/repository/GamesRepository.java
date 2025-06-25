package com.circa.mind_speed_api.repository;

import com.circa.mind_speed_api.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GamesRepository extends JpaRepository<Game,Long> {
}
