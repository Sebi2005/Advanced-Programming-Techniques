package gym.repository;
import gym.common.DuplicateIdException;
import gym.common.EntityNotFoundException;
import gym.domain.Client;
import gym.repository.core.InMemoryRepository;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryRepositoryTest {
    @Test
    void crudAndCount() {
        var repository = new InMemoryRepository<Integer, Client>();
        assertEquals(0, repository.count());
        repository.create(new Client(1, "Alex", "a@gmail.com", "000"));
        assertEquals(1, repository.count());
        assertEquals("Alex", repository.read(1).orElseThrow().getName());
        repository.update(new Client(1, "Ana", "ana@email.com", "001"));
        assertEquals("Ana", repository.read(1).orElseThrow().getName());
        repository.delete(1);
        assertEquals(0, repository.count());
    }

    @Test
    void duplicateAndNotFound() {
        var repository = new InMemoryRepository<Integer, Client>();
        repository.create(new Client(1,"Alex","a@mail","000"));
        assertThrows(DuplicateIdException.class,
                () ->repository.create(new Client(1,"ana","a","0")));
        assertThrows(EntityNotFoundException.class,
                () ->repository.read(2));
        assertThrows(EntityNotFoundException.class,
                ()->repository.update(new Client(2,"a","a","0")));
        assertThrows(EntityNotFoundException.class,
                ()->repository.delete(2));
    }


    }

