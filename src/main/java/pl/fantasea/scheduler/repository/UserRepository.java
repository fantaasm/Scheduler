package pl.fantasea.scheduler.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.fantasea.scheduler.dto.UserDto;
import pl.fantasea.scheduler.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByLogin(String login);

    Optional<User> findByLoginAndEmail(String login, String email);

    Optional<User> findByEmail(String email);

    Optional<User> findByLoginOrEmail(String login, String email);

    @Query("SELECT new pl.fantasea.scheduler.dto.UserDto(u.login, u.email) FROM User AS u")
    List<UserDto> getAllUsersWithEmail();
}