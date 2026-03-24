package gym.repository.core.session;
import gym.domain.Session;
import gym.repository.core.AbstractFilter;

import java.time.LocalDate;

public class SessionOnDate implements AbstractFilter<Session> {
    private final LocalDate date;
    public SessionOnDate(LocalDate date) {
        this.date = date;
    }
    @Override
    public boolean accept(Session session) {
        return session.getDateTime() != null && session.getDateTime().toLocalDate().equals(date);
    }
}
