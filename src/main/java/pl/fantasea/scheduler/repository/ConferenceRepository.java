package pl.fantasea.scheduler.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.fantasea.scheduler.model.Conference;

@Repository
public interface ConferenceRepository extends CrudRepository<Conference, Long> {
}
