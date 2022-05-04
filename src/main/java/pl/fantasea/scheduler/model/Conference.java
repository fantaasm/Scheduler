package pl.fantasea.scheduler.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import pl.fantasea.scheduler.model.enums.Subject;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "conferences")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Conference extends EntityBase {

    @NotNull
    @NotBlank(message = "Conference name cannot be empty")
    @Column(name = "name")
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "subject")
    private Subject subject;

    @NotNull
    @Column(name = "start_date")
    private LocalDateTime startDate;

    @NotNull
    @Column(name = "end_date")
    private LocalDateTime endDate;

    @NotNull
    @Column(name = "max_participants")
    private Integer maxParticipants;

    @ManyToMany(mappedBy = "conferences")
    @JsonManagedReference
    private Set<User> registeredUsers = new HashSet<>();

    @Override
    public String toString() {
        return getClass().getSimpleName()
                + "(" + "id = " + id + ", "
                + "name = " + name + ", "
                + "subject = " + subject + ", "
                + "startDate = " + startDate + ", "
                + "endDate = " + endDate + ", "
                + "maxParticipants = " + maxParticipants + ", "
                + "createdAt = " + createdAt + ", "
                + "modifiedAt = " + modifiedAt + ")";
    }
}

