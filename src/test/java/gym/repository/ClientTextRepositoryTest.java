package gym.repository;
import gym.common.DuplicateIdException;
import gym.common.EntityNotFoundException;
import gym.domain.Client;
import gym.repository.core.client.ClientTextRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;
class ClientTextRepositoryTest {
    @TempDir
    Path temporaryDir;

    @Test
    void loadFromExistingFile() throws Exception {
        Path file = temporaryDir.resolve("clients.txt");
        String content =
                "1;Ana;ana@mail.com;0711\n" +
                        "2;Bogdan;bogdan@mail.com;0722\n" +
                        "\n";
        Files.writeString(file,content,StandardCharsets.UTF_8);
        var repository = new ClientTextRepository(file.toString());

        assertEquals(2,repository.count());

        var client1 = repository.read(1).orElseThrow();
        assertEquals("Ana", client1.getName());
        assertEquals("ana@mail.com", client1.getEmail());
        assertEquals("0711", client1.getPhone());

    }
    @Test
    void createUpdateDeletePersists() {
        Path file = temporaryDir.resolve("clients_persist.txt");
        var repository = new ClientTextRepository(file.toString());
        assertEquals(0, repository.count());
        repository.create(new Client(10, "Tom", "t@x.com", "0700"));
        repository.create(new Client(11, "Eva", "e@x.com", "0701"));
        assertEquals(2, repository.count());
        var repositoryAfterSecondFileOpen = new ClientTextRepository(file.toString());
        assertEquals(2, repositoryAfterSecondFileOpen.count());
        assertEquals("Tom", repositoryAfterSecondFileOpen.read(10).orElseThrow().getName());
        repositoryAfterSecondFileOpen.update(new Client(10, "Tommy", "tom@x.com", "0709"));
        var repositoryAfterThirdFileOpen = new ClientTextRepository(file.toString());
        assertEquals(2, repositoryAfterThirdFileOpen.count());
        assertEquals("Tommy", repositoryAfterThirdFileOpen.read(10).orElseThrow().getName());
        repositoryAfterThirdFileOpen.delete(11);
        var repositoryAfterFourthFileOpen = new ClientTextRepository(file.toString());
        assertEquals(1, repositoryAfterFourthFileOpen.count());
        assertThrows(EntityNotFoundException.class, () -> repositoryAfterFourthFileOpen.read(11));

    }
    void duplicateAndMissingThrow() {
        Path file = temporaryDir.resolve("clients_err.txt");
        var repo = new ClientTextRepository(file.toString());
        repo.create(new Client(1, "Ana", "a@a", "0"));
        assertThrows(DuplicateIdException.class,
                () -> repo.create(new Client(1, "X", "x@x", "1")));
        assertThrows(EntityNotFoundException.class, () -> repo.read(999));
        assertThrows(EntityNotFoundException.class, () -> repo.update(new Client(2, "B", "b@b", "2")));
        assertThrows(EntityNotFoundException.class, () -> repo.delete(2));
    }

    }

