package gym.repository;


import gym.common.DuplicateIdException;
import gym.common.EntityNotFoundException;
import gym.domain.Client;
import gym.repository.core.client.ClientBinaryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ClientBinaryRepositoryTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void roundTripPersistence() {
        Path file = temporaryDirectory.resolve("clients.bin");
        var repository = new ClientBinaryRepository(file.toString());


        assertEquals(0, repository.count());


        repository.create(new Client(1, "Ana", "a@mail.com", "0700"));
        repository.create(new Client(2, "Bogdan", "b@mail.com", "0701"));
        assertEquals(2, repository.count());


        var repositoryAfterSecondFileOpen = new ClientBinaryRepository(file.toString());
        assertEquals(2, repositoryAfterSecondFileOpen.count());
        assertEquals("Ana", repositoryAfterSecondFileOpen.read(1).orElseThrow().getName());
        assertEquals("Bogdan", repositoryAfterSecondFileOpen.read(2).orElseThrow().getName());


        repositoryAfterSecondFileOpen.update(new Client(1, "Ana-M", "am@mail.com", "0799"));
        var repositoryAfterThirdFileOpen = new ClientBinaryRepository(file.toString());
        assertEquals("Ana-M", repositoryAfterThirdFileOpen.read(1).orElseThrow().getName());


        repositoryAfterThirdFileOpen.delete(2);
        var repositoryAfterFourthFileOpen = new ClientBinaryRepository(file.toString());
        assertEquals(1, repositoryAfterFourthFileOpen.count());
        assertThrows(EntityNotFoundException.class, () -> repositoryAfterFourthFileOpen.read(2));
    }

    @Test
    void duplicateAndMissingThrow() {
        Path file = temporaryDirectory.resolve("dup.bin");
        var repository = new ClientBinaryRepository(file.toString());

        repository.create(new Client(5, "X", "x@x", "000"));
        assertThrows(DuplicateIdException.class,
                () -> repository.create(new Client(5, "Y", "y@y", "111")));
        assertThrows(EntityNotFoundException.class, () -> repository.read(999));
        assertThrows(EntityNotFoundException.class, () -> repository.update(new Client(6, "Z", "z@z", "222")));
        assertThrows(EntityNotFoundException.class, () -> repository.delete(6));
    }
}
