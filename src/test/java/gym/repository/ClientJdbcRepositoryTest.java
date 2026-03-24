package gym.repository;

import gym.domain.Client;
import gym.repository.core.DbUtil;
import gym.repository.core.Repository;
import org.junit.jupiter.api.Test;
import gym.repository.core.client.ClientJDBCRepository;
import static org.junit.jupiter.api.Assertions.*;
import java.nio.file.Path;
import org.junit.jupiter.api.io.TempDir;

class ClientJdbcRepositoryTest {

    @TempDir Path tempDir;

    @Test
    void crudSqlite() {

        String url = DbUtil.sqliteUrl(tempDir.toString(), "test.db");
        Repository<Integer, Client> repo = new ClientJDBCRepository(url, "clients_test");

        repo.create(new Client(1, "Ana", "a@mail.com", "0"));
        assertEquals(1, repo.count());
        assertEquals("Ana", repo.read(1).orElseThrow().getName());
        var clients = repo.readAll();
        assertNotNull(clients);
        repo.update(new Client(1, "Ana2", "a@mail.com", "1"));
        assertEquals("Ana2", repo.read(1).orElseThrow().getName());

        repo.delete(1);
        assertEquals(0, repo.count());
    }
}
