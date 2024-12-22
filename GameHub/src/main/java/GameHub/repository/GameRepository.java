package GameHub.repository;

import GameHub.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;


public interface GameRepository extends JpaRepository<Game, Integer> {

}
