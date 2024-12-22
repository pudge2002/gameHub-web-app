package GameHub.repository;

import GameHub.model.Developer;
import org.springframework.data.jpa.repository.JpaRepository;


public interface DeveloperRepository extends JpaRepository<Developer, Integer> {

}
