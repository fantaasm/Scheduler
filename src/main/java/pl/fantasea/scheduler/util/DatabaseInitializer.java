package pl.fantasea.scheduler.util;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import pl.fantasea.scheduler.model.Conference;
import pl.fantasea.scheduler.model.User;
import pl.fantasea.scheduler.model.enums.Subject;
import pl.fantasea.scheduler.repository.ConferenceRepository;
import pl.fantasea.scheduler.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DatabaseInitializer {
    private final ConferenceRepository conferenceRepository;
    private final UserRepository userRepository;

    @Bean
    public void initCourses() {

        var conference1Start = LocalDateTime.of(2021, Month.JUNE, 1, 10, 0);
        var conference1End = LocalDateTime.of(2021, Month.JUNE, 1, 11, 45);

        var conference2Start = LocalDateTime.of(2021, Month.JUNE, 1, 12, 0);
        var conference2End = LocalDateTime.of(2021, Month.JUNE, 1, 13, 45);

        var conference3Start = LocalDateTime.of(2021, Month.JUNE, 1, 14, 0);
        var conference3End = LocalDateTime.of(2021, Month.JUNE, 1, 15, 45);

        var conferences = List.of(new Conference()
                                          .setName("Java Spring Conference")
                                          .setSubject(Subject.BACK_END)
                                          .setMaxParticipants(5)
                                          .setStartDate(conference1Start)
                                          .setEndDate(conference1End),
                                  new Conference()
                                          .setName("ASP.NET Conference")
                                          .setSubject(Subject.BACK_END)
                                          .setMaxParticipants(5)
                                          .setStartDate(conference1Start)
                                          .setEndDate(conference1End),
                                  new Conference()
                                          .setName("Express Conference")
                                          .setSubject(Subject.BACK_END)
                                          .setMaxParticipants(5)
                                          .setStartDate(conference1Start)
                                          .setEndDate(conference1End),
                                  new Conference()
                                          .setName("Next.js Conference")
                                          .setSubject(Subject.FRONT_END)
                                          .setMaxParticipants(5)
                                          .setStartDate(conference2Start)
                                          .setEndDate(conference2End),
                                  new Conference()
                                          .setName("Vue.js Conference")
                                          .setSubject(Subject.FRONT_END)
                                          .setMaxParticipants(5)
                                          .setStartDate(conference2Start)
                                          .setEndDate(conference2End),
                                  new Conference()
                                          .setName("Angular Conference")
                                          .setSubject(Subject.FRONT_END)
                                          .setMaxParticipants(5)
                                          .setStartDate(conference2Start)
                                          .setEndDate(conference2End),
                                  new Conference()
                                          .setName("PostgreSQL Conference")
                                          .setSubject(Subject.DATABASE)
                                          .setMaxParticipants(5)
                                          .setStartDate(conference3Start)
                                          .setEndDate(conference3End),
                                  new Conference()
                                          .setName("MySQL Conference")
                                          .setSubject(Subject.DATABASE)
                                          .setMaxParticipants(5)
                                          .setStartDate(conference3Start)
                                          .setEndDate(conference3End),
                                  new Conference()
                                          .setName("SQL Server Conference")
                                          .setSubject(Subject.DATABASE)
                                          .setMaxParticipants(5)
                                          .setStartDate(conference3Start)
                                          .setEndDate(conference3End));

        conferenceRepository.saveAll(conferences);
    }

    @Bean
    public void initUsers() {

        Collection<Conference> conferences = (Collection<Conference>) conferenceRepository.findAll();

        var users = List.of(new User().setLogin("jankowalski").setEmail("jan.kowalski@gmail.com").setConferences(getRandomConferences(conferences)),
                            new User().setLogin("jannowak").setEmail("jan.nowak@proton.mail").setConferences(getRandomConferences(conferences)),
                            new User().setLogin("maciek").setEmail("admin@fantasea.pl").setConferences(getRandomConferences(conferences)),
                            new User().setLogin("annamaria").setEmail("anna.wesolowska@sedzia.pl").setConferences(getRandomConferences(conferences)),
                            new User().setLogin("mariakowalska").setEmail("mariak@gmail.com").setConferences(getRandomConferences(conferences)),
                            new User().setLogin("dimitrii").setEmail("dimi3@yandex.ru").setConferences(getRandomConferences(conferences)));
        userRepository.saveAll(users);
    }

    // doesn't respect max size of conference but it's ok for this example
    private Set<Conference> getRandomConferences(Collection<Conference> conferences) {
        return conferences.stream().filter(conference -> new Random().nextInt(2) == 0).collect(Collectors.toSet());
    }
}
