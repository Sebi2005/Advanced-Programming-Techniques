package gym.service;
import gym.common.EntityNotFoundException;
import gym.domain.Session;
import gym.repository.core.FilteredRepository;
import gym.repository.core.session.SessionRepository;
import gym.repository.core.Repository;
import gym.repository.core.session.SessionByClientID;
import gym.repository.core.session.SessionOnDate;
import java.time.LocalDateTime;
import java.time.LocalDate;
import gym.Constants.*;
import static gym.Constants.MINIMUM_ID;
import gym.common.ValidationException;

import java.util.Comparator;
import java.util.stream.StreamSupport;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
public class SessionService {
    private final FilteredRepository<Integer, Session> sessionRepository;
    private final gym.undo.UndoRedoService undoRedoService;
    public SessionService(FilteredRepository<Integer, Session> sessionRepository,
                          gym.undo.UndoRedoService undoRedoService) {
        this.sessionRepository = sessionRepository;
        this.undoRedoService = undoRedoService;
    }
    private java.util.stream.Stream<Session> sessionStream() {
        return StreamSupport.stream(sessionRepository.readAll().spliterator(), false);
    }
    //the sessions of a client between two dates
    public List<Session> sessionsOfClientBetweenDates(int clientID, LocalDate startDate, LocalDate endDate) {
        return sessionStream().filter(sessionFilteredByClientID->sessionFilteredByClientID.getClientId() == clientID)
                .filter(sessionFilteredBetweenTwoDates->{
                    LocalDate date = sessionFilteredBetweenTwoDates.getDateTime().toLocalDate();
                    return !date.isBefore(startDate) && !date.isAfter(endDate);
                })
                .sorted(Comparator.comparing(Session::getDateTime))
                .toList();
    }

    public List<Session> sessionsOfClientWithDescriptionKeyword(int clientID, String keyword) {
        String loweredKeyword = keyword.toLowerCase();
        return sessionStream().filter(sessionFilteredByClientID->sessionFilteredByClientID.getClientId() == clientID)
                .filter(sessionFilteredByDescription->sessionFilteredByDescription.getDescription().toLowerCase().contains(loweredKeyword))
                .sorted(Comparator.comparing(Session::getDateTime))
                .toList();
    }
    public Map<Integer,Long> sessionCountPerClient() {

        return sessionStream()
                .collect(Collectors.groupingBy(
                        Session::getClientId,
                        Collectors.counting()
                ));
    }
    public Map<Integer, Session> nextUpcomingSessionPerClient() {
        LocalDate now = LocalDate.now();
        return sessionStream()
                .filter(UpcomingSessions->!UpcomingSessions.getDateTime().toLocalDate().isBefore(now))
                .collect(Collectors.groupingBy(
                        Session::getClientId,
                        Collectors.collectingAndThen(
                                Collectors.minBy(Comparator.comparing(Session::getDateTime)),
                                optional->optional.orElse(null)
                        )
                ));
    }

    public Map<LocalDate,List<Session>> sessionsByDayForClient(int clientID) {
        return sessionStream().filter(sessionFilteredByClientID->sessionFilteredByClientID.getClientId() == clientID)
                .collect(Collectors.groupingBy(
                        sessionGroupedByDay->sessionGroupedByDay.getDateTime().toLocalDate(),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> list.stream()
                                        .sorted(Comparator.comparing(Session::getDateTime))
                                        .toList()
                        )
                ));

    }

    private void sessionValidation(int id, int clientID, LocalDateTime date, String description ) {
        if (id < MINIMUM_ID) throw new ValidationException("ID must be > 0.");
        if (clientID < MINIMUM_ID) throw new ValidationException("clientId must be > 0.");
        if (date == null) throw new ValidationException("date must not be null.");
        if (description == null) throw new ValidationException("description must not be null.");
    }
    public void addSession(int id, int clientId, LocalDateTime date, String description){
        sessionValidation(id,clientId,date,description);
        Session newSession = new Session(id,clientId,date,description);
        sessionRepository.create(newSession);
        undoRedoService.record(new gym.undo.ActionAdd<>(sessionRepository,newSession));
    }
    public void updateSession(int id, int clientId, LocalDateTime date, String description){
        sessionValidation(id,clientId,date,description);
        Session oldSession = sessionRepository.read(id)
                        .orElseThrow(()->new EntityNotFoundException(id));
        Session newSession = new Session(id,clientId,date,description);

        sessionRepository.update(newSession);

        undoRedoService.record(new gym.undo.ActionUpdate<>(sessionRepository,oldSession,newSession));
    }
    public Session getOneSession(int id) {
        return sessionRepository.read(id).orElseThrow(()->new EntityNotFoundException(id));}
    public Iterable<Session> getAllSessions() {return sessionRepository.readAll();}
    public void removeSession(int id) {
        Session removedSession = sessionRepository.read(id)
                        .orElseThrow(()->new EntityNotFoundException(id));

        sessionRepository.delete(id).orElseThrow(()->new EntityNotFoundException(id));
        undoRedoService.record(new gym.undo.ActionRemove<>(sessionRepository, removedSession));}
    public Iterable<Session> filterByClientId(int clientId) { return sessionRepository.filter(new SessionByClientID(clientId)); }
    public Iterable<Session> filterOnDate(LocalDate date) { return sessionRepository.filter(new SessionOnDate(date)); }
    public void removeAllSessionsForClient(int clientId) {
        var sessionsToDelete = sessionStream()
                .filter(sessionFilteredByClientID->sessionFilteredByClientID.getClientId() == clientId)
                .map(Session::getId).toList();
        sessionsToDelete.forEach(id->sessionRepository.delete(id));
    }

    public gym.repository.core.Repository<Integer,Session> getRepository() {
        return sessionRepository;
    }
}
