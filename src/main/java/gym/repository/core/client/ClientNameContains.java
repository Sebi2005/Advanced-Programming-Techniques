package gym.repository.core.client;

import gym.domain.Client;
import gym.repository.core.AbstractFilter;
public class ClientNameContains implements AbstractFilter<Client> {
    private final String needle;
    public ClientNameContains(String needle) {
        this.needle = needle.toLowerCase();
    }
    @Override
    public boolean accept(Client client) {
        return client.getName().toLowerCase().contains(needle) && client.getName()!=null;
    }

}
