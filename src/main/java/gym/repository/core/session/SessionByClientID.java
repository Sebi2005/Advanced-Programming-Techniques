package gym.repository.core.session;
import gym.domain.Session;
import gym.repository.core.AbstractFilter;
public class SessionByClientID implements AbstractFilter<Session> {
    private final Integer clientID;
    public SessionByClientID(Integer clientID) {
        this.clientID = clientID;
    }
    @Override
    public boolean accept(Session session) {
        return session.getClientId().equals(clientID);

    }
}
