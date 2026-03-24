package gym.repository.core.session;
import gym.domain.Session;
import gym.repository.core.FilteredRepository;
import gym.repository.core.InMemoryRepository;

import java.util.Map;
import java.time.LocalDateTime;
public class SessionRepository extends FilteredRepository<Integer,Session> {
    public SessionRepository() {
        create(new Session(1, 1, LocalDateTime.of(2025, 10, 20, 18, 0), "Leg day"));
        create(new Session(2, 2, LocalDateTime.of(2025, 10, 21, 19, 0), "Push workout"));
        create(new Session(3, 1, LocalDateTime.of(2025, 10, 22, 17, 30), "Pull workout"));
        create(new Session(4, 3, LocalDateTime.of(2025, 10, 23, 9, 0),  "Cardio HIIT"));
        create(new Session(5, 4, LocalDateTime.of(2025, 10, 24, 11, 0), "Full body"));
    }
}
