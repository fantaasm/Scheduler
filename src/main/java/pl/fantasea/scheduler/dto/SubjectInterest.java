package pl.fantasea.scheduler.dto;

import pl.fantasea.scheduler.model.enums.Subject;

public interface SubjectInterest {
    Subject getSubject();
    float getInterest();
}
