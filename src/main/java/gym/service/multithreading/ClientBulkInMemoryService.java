package gym.service.multithreading;

import gym.domain.Client;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ClientBulkInMemoryService {

    public List<Client> generateClients(int count) {
        List<Client> clients = new ArrayList<>(count);
        for (int i = 1; i <= count; i++) {
            clients.add(new Client(
                    i,
                    "Client " + i,
                    "client" + i + "@mail.com",
                    "0700000000"
            ));
        }
        return clients;
    }

    public long runWithThreads(List<Client> clients, int noThreads) throws InterruptedException {
        int n = clients.size();
        int interval = n / noThreads;
        int remainder = n % noThreads;

        ClientWorker[] threads = new ClientWorker[noThreads];
        int start = 0;

        long t0 = System.currentTimeMillis();

        for (int i = 0; i < noThreads; i++) {
            int end = start + interval;
            if (i == noThreads - 1) end += remainder;

            threads[i] = new ClientWorker(start, end, clients);
            threads[i].start();

            start = end;
        }

        for (ClientWorker t : threads) t.join();

        long t1 = System.currentTimeMillis();
        return t1 - t0;
    }

    public long runWithExecutor(List<Client> clients, int noThreads) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(noThreads);

        int n = clients.size();
        int interval = n / noThreads;
        int remainder = n % noThreads;

        long t0 = System.currentTimeMillis();

        int start = 0;
        for (int i = 0; i < noThreads; i++) {
            int end = start + interval;
            if (i == noThreads - 1) end += remainder;

            final int startIdx = start;
            final int endIdx = end;

            executor.submit(() -> {
                for (int k = startIdx; k < endIdx; k++) {
                    Client c = clients.get(k);
                    if (c.getId() % 3 == 0) {
                        c.setPhone("07" + String.format("%08d", c.getId()));
                    }
                }
            });

            start = end;
        }

        executor.shutdown();
        executor.awaitTermination(60, TimeUnit.SECONDS);

        long t1 = System.currentTimeMillis();
        return t1 - t0;
    }
}

