package gym.service;

import gym.common.EntityNotFoundException;
import gym.common.ValidationException;
import gym.domain.Client;
import gym.repository.core.client.ClientRepository;
import gym.repository.core.client.ClientEmailContains;
import gym.repository.core.client.ClientNameContains;
import gym.repository.core.FilteredRepository;
import gym.repository.core.Repository;
import gym.Constants.*;

import static gym.Constants.MINIMUM_ID;
import gym.common.ValidationException;
import gym.undo.ActionAdd;

import java.util.Optional;
import java.util.stream.StreamSupport;
public class ClientService {
    private final FilteredRepository<Integer,Client> clientRepository;
    private final gym.service.SessionService sessionService;
    private final gym.undo.UndoRedoService undoRedoService;
    public ClientService(FilteredRepository<Integer,Client> clientRepository,gym.service.SessionService sessionService
    , gym.undo.UndoRedoService undoRedoService) {
        this.clientRepository = clientRepository;
        this.sessionService = sessionService;
        this.undoRedoService = undoRedoService;
    }
    private void clientValidation(int id, String name, String email, String phone) {
        if (id<MINIMUM_ID) throw new ValidationException("Id must be higher than 0");
        if (name == null || name.isBlank()) throw new ValidationException("Name cannot be empty");
        if (email == null || email.isBlank()) throw new ValidationException("Email cannot be empty");
        if (phone == null || phone.isBlank()) throw new ValidationException("Phone cannot be empty");
    }
    public void addClient(int id,String name, String email, String phone){
        clientValidation(id,name,email,phone);
        Client newClient = new Client(id, name, email, phone);
        clientRepository.create(newClient);
        undoRedoService.record(new gym.undo.ActionAdd<>(clientRepository,newClient));
    }
    public void updateClient(int id, String name, String email, String phone) {
        clientValidation(id,name,email,phone);
        Client oldClient = clientRepository.read(id)
                .orElseThrow(()->new EntityNotFoundException(id));

        Client newClient = new Client(id, name, email, phone);

        clientRepository.update(newClient);

        undoRedoService.record(new gym.undo.ActionUpdate<>(clientRepository,oldClient,newClient));
    }
    public Client getOneClient(int id) {

        return clientRepository.read(id).orElseThrow(()->new EntityNotFoundException(id));
    }
    public Iterable<Client> getAllClients() {
        return clientRepository.readAll();
    }
    public void removeClient(int id) {
        Client removedClient = clientRepository.read(id).orElseThrow(()->new EntityNotFoundException(id));

        var relatedSessions = new java.util.ArrayList<gym.domain.Session>();
        sessionService.filterByClientId(id).forEach(relatedSessions::add);

        sessionService.removeAllSessionsForClient(id);
        clientRepository.delete(id).orElseThrow(()->new EntityNotFoundException(id));

        java.util.List<gym.undo.IAction> actions = new java.util.ArrayList<>();

        for (gym.domain.Session session: relatedSessions) {
            actions.add(new gym.undo.ActionRemove<>(sessionService.getRepository(),session));
        }
        actions.add(new gym.undo.ActionRemove<>(clientRepository, removedClient));
        undoRedoService.record(new gym.undo.CompositeAction(actions));
    }

    public Iterable<Client> filterByNameContains(String text) {
        String textLowered = text.toLowerCase();
        return clientRepository.filter(clientFilteredByName->clientFilteredByName.getName().toLowerCase().contains(textLowered));
    }
    public Iterable<Client> filterByEmailContains(String text) {
        String textLowered = text.toLowerCase();
        return clientRepository.filter(clientFilteredByEmail->clientFilteredByEmail.getEmail().toLowerCase().contains(textLowered));
    }


}
