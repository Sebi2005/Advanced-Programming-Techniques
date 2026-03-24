package gym.repository.core.session;

import gym.domain.Session;
import gym.repository.file.BinaryFileRepository;
public class SessionBinaryRepository extends BinaryFileRepository<Integer, Session> {
    public SessionBinaryRepository(String filePath) {super(filePath);}
}
