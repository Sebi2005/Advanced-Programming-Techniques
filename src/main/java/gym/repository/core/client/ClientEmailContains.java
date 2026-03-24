package gym.repository.core.client;

import gym.domain.Client;
import gym.repository.core.AbstractFilter;
public class ClientEmailContains implements AbstractFilter<Client> {
    private final String needle;
    public ClientEmailContains(String needle) {
        this.needle = needle.toLowerCase();
    }
    @Override
    public boolean accept(Client client) {
        return client.getEmail() != null && client.getEmail().toLowerCase().contains(needle);
    }
}
