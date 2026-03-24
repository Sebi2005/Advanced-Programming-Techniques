package gym.service.multithreading;

import gym.domain.Client;

import java.util.List;

public class ClientWorker extends Thread {
    private final int startIdx, endIdx;
    private final List<Client> clients;

    public ClientWorker(int startIdx, int endIdx, List<Client> clients) {
        this.startIdx = startIdx;
        this.endIdx = endIdx;
        this.clients = clients;
    }

    @Override
    public void run() {
        for (int i = startIdx; i < endIdx; i++) {
            Client c = clients.get(i);


            if (c.getId() % 3 == 0) {
                c.setPhone("07" + String.format("%08d", c.getId()));
            }
        }
    }
}

