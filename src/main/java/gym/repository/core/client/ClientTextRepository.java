package gym.repository.core.client;

import static gym.Constants.*;
import gym.domain.Client;
import gym.repository.file.TextFileRepository;
public class ClientTextRepository extends TextFileRepository<Integer, Client> {
    public ClientTextRepository(String filePath) {super(filePath);}

    @Override
    protected Client fromLine(String line) {
        String[] parsedInput = line.split(";", -1);
        return new Client(Integer.parseInt(parsedInput[CLIENT_ID]), parsedInput[CLIENT_NAME], parsedInput[CLIENT_EMAIL], parsedInput[CLIENT_PHONE]);
    }

    @Override
    protected String toLine(Client client) {
        return client.getId() + ";" +client.getName() + ";" + client.getEmail() + ";" +client.getPhone();

    }
}
