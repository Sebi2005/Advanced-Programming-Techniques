package gym.service;
import gym.common.EntityNotFoundException;
import gym.common.ValidationException;
import gym.domain.Client;
import gym.repository.core.client.ClientRepository;
import gym.repository.core.FilteredRepository;
import gym.undo.UndoRedoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ClientServiceTest {
    private ClientService clientService;
    private SessionService sessionService;
    private UndoRedoService undoRedoService;

    @BeforeEach
    void setUp() {
        FilteredRepository<Integer, Client> repository = new ClientRepository();
        clientService = new ClientService(repository,sessionService,undoRedoService);
    }
    @Test
    void getAll() {
        var allClients =  clientService.getAllClients();
        assertNotNull(allClients);
    }

    @Test
    void addGetUpdateRemove() {
        clientService.addClient(10,"T","t@gmail.com","00");
        var client = clientService.getOneClient(10);
        assertEquals("T",client.getName());
        clientService.updateClient(10,"N","n@mail","01");
        client = clientService.getOneClient(10);
        assertEquals("N",client.getName());
        clientService.removeClient(10);
        assertThrows(EntityNotFoundException.class,
                ()->clientService.getOneClient(10));
    }

    @Test
    void validation() {
        assertThrows(ValidationException.class,()->clientService.addClient(0,"r","r","0"));
        assertThrows(ValidationException.class,()->clientService.addClient(1,"","r","0"));
        assertThrows(ValidationException.class,()->clientService.addClient(1,"r","","0"));
        assertThrows(ValidationException.class,()->clientService.addClient(1,"r","r",""));

    }

    @Test
    void filters() {
        var entityByName = clientService.filterByNameContains("a");
        assertTrue(entityByName.iterator().hasNext());

        var entityByEmail = clientService.filterByEmailContains("gmail");
        assertTrue(entityByEmail.iterator().hasNext());
    }
}

