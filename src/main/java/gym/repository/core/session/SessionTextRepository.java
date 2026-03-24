package gym.repository.core.session;

import static gym.Constants.*;
import gym.domain.Session;
import gym.repository.file.TextFileRepository;

import java.time.LocalDateTime;
public class SessionTextRepository extends TextFileRepository<Integer, Session> {
    public SessionTextRepository(String filePath) {super(filePath);}

    @Override
    protected Session fromLine(String line) {
        String[] parsedInput = line.split(";", -1);
        return new Session(
                Integer.parseInt(parsedInput[SESSION_ID]),
                Integer.parseInt(parsedInput[SESSION_CLIENT_ID]),
                LocalDateTime.parse(parsedInput[SESSION_TIME]),
                parsedInput[SESSION_DESCRIPTION]
        );
    }

    @Override
    protected String toLine(Session session) {
        return session.getId() + ";" + session.getClientId() + ";" + session.getDateTime() + ";"+session.getDescription();
    }
}
