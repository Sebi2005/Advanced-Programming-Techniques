package gym.repository.core.client;
import gym.domain.Client;
import gym.repository.file.BinaryFileRepository;
public class ClientBinaryRepository extends BinaryFileRepository<Integer, Client> {
    public ClientBinaryRepository(String filePath) {super(filePath);}

}
