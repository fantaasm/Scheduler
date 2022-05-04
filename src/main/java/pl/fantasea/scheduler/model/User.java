package pl.fantasea.scheduler.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class User extends EntityBase {

    @NotNull
    @Column(name = "login", unique = true)
    private String login;
    @NotNull
    @Column(name = "email", unique = true)
    private String email;
    @ManyToMany
    @JoinTable(name = "user_conference",
               joinColumns = @JoinColumn(name = "user_id"),
               inverseJoinColumns = @JoinColumn(name = "conference_id"))
    @JsonBackReference
    private Set<Conference> conferences = new HashSet<>();

    public User(String login, String email) {
        this.login = login;
        this.email = email;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()
                + "(" + "id = " + id + ", "
                + "createdAt = " + createdAt + ", "
                + "modifiedAt = " + modifiedAt + ", "
                + "login = " + login + ", "
                + "email = " + email + ")";
    }
}