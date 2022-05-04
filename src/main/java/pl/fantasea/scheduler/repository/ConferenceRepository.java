package pl.fantasea.scheduler.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.fantasea.scheduler.dto.SubjectInterest;
import pl.fantasea.scheduler.model.Conference;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ConferenceRepository extends CrudRepository<Conference, Long> {
    @Query(value = "SELECT COUNT(DISTINCT user_conference.user_id) / CAST( (SELECT COUNT(DISTINCT user_conference.user_id) FROM user_conference) as FLOAT ) AS interest "
            + "FROM user_conference WHERE user_conference.conference_id = ?1"
            , nativeQuery = true)
    float findTotalInterestInConference(Long conferenceId);

    @Query(value = "SELECT Conferences.subject AS subject, COUNT(user_conference.user_id) / CAST( (SELECT COUNT(user_conference.user_id) FROM user_conference) AS FLOAT ) AS interest "
            + "FROM conferences INNER JOIN user_conference ON user_conference.conference_id = Conferences.id "
            + "GROUP BY subject "
            + "ORDER BY interest DESC"
            , nativeQuery = true)
    List<SubjectInterest> findTotalInterestInSubjects();

    @Query(value = "SELECT count(*) > 0 FROM user_conference AS uc "
           + "INNER JOIN conferences AS c ON uc.conference_id = c.id WHERE uc.user_id = ?1 AND ?2 BETWEEN c.start_date AND c.end_date"
            , nativeQuery = true)
    boolean isUserNotEligible(Long userId, LocalDateTime conferenceStartTime);

    boolean existsById(Long id);
}


