package gym.ui;

import gym.domain.Client;
import gym.domain.Session;
import gym.service.SessionService;
import static gym.Constants.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Scanner;
import gym.common.ValidationException;
import gym.common.RepositoryException;
import java.util.List;
import java.util.Map;
public class SessionUI {
    private final SessionService sessionService;
    private final Scanner scanner = new Scanner(System.in);
    public SessionUI(SessionService sessionService) {
        this.sessionService = sessionService;
    }
    public void run() {
        while (true) {
            menu();
            String command = scanner.nextLine().trim();
            try {
                switch (command) {
                    case LIST_SESSIONS:listSessions();break;
                    case ADD_SESSION:addSession();break;
                    case VIEW:viewSessionById();break;
                    case UPDATE:updateSession();break;
                    case DELETE:deleteSession();break;
                    case FILTER_BY_CLIENT_ID: filterByClientID(); break;
                    case FILTER_ON_DATE: filterOnDate(); break;
                    case REPORT_CLIENT_BETWEEN:reportSessionsOfClientBetweenDates(); break;
                    case REPORT_CLIENT_KEYWORD: reportSessionsOfClientWithKeyword(); break;
                    case REPORT_COUNT_PER_CLIENT:reportSessionCountPerClient(); break;
                    case REPORT_NEXT_UPCOMING:reportNextUpcomingSessionPerClient(); break;
                    case REPORT_BY_DAY_FOR_CLIENT: reportSessionsByDayForClient(); break;
                    case EXIT_SESSION:  return;
                    default: System.out.println("Invalid choice!");
                }
            }catch (RepositoryException | ValidationException e)  {
                System.out.println("Error: " + e.getMessage());
            } catch (NumberFormatException nfe) {
                System.out.println("Error: invalid number format.");
            }
            System.out.println();

        }
    }
    private void menu() {
        System.out.println("SESSIONS MENU");
        System.out.println(LIST_SESSIONS+". List Sessions");
        System.out.println(ADD_SESSION+". Add Session");
        System.out.println(VIEW+". View Session by ID");
        System.out.println(UPDATE+". Update Session");
        System.out.println(DELETE+". Delete Session");
        System.out.println(FILTER_BY_CLIENT_ID+". Filter: by clientId");
        System.out.println(FILTER_ON_DATE+". Filter: on date");
        System.out.println(REPORT_CLIENT_BETWEEN+". Report: sessions for client between 2 dates");
        System.out.println(REPORT_CLIENT_KEYWORD+ ". Report: sessions for client with keyword");
        System.out.println(REPORT_COUNT_PER_CLIENT+ ". Report: session count per client");
        System.out.println(REPORT_NEXT_UPCOMING+ ". Report: next upcoming session per client");
        System.out.println(REPORT_BY_DAY_FOR_CLIENT+ ". Report: sessions by day for a client");
        System.out.println(EXIT_SESSION+". Back");
        System.out.print("Select an option: ");
    }
    private int askInteger(String label) {
        System.out.print(label + ": ");
        return Integer.parseInt(scanner.nextLine().trim());
    }
    private LocalDateTime askDateTime() {
        System.out.print("Date (yyyy-MM-dd): ");
        LocalDate date = LocalDate.parse(scanner.nextLine().trim());
        System.out.print("Time (HH:mm): ");
        LocalTime time = LocalTime.parse(scanner.nextLine().trim());
        return LocalDateTime.of(date, time);
    }
    private void listSessions() {
        sessionService.getAllSessions().forEach(System.out::println);
    }
    private void addSession() {
        int id = askInteger("ID");
        int clientID = askInteger("Client ID");
        LocalDateTime date = askDateTime();
        System.out.print("Description: ");
        String description = scanner.nextLine().trim();
        sessionService.addSession(id, clientID, date, description);
    }
    private void viewSessionById() {
        int id = askInteger("ID");
        System.out.println(sessionService.getOneSession(id));

    }
    private void updateSession() {
        int id = askInteger("ID");
        int clientId = askInteger("Client ID");
        LocalDateTime dt = askDateTime();
        System.out.print("New description: ");
        String description = scanner.nextLine().trim();
        sessionService.updateSession(id, clientId, dt, description);
    }
    private void deleteSession() {
        int id = askInteger("ID");
        sessionService.removeSession(id);
    }
    private void filterByClientID() {
        int id = askInteger("Client ID");
        sessionService.filterByClientId(id).forEach(System.out::println);
    }
    private void filterOnDate() {
        System.out.print("Date (yyyy-MM-dd): ");
        LocalDate date = java.time.LocalDate.parse(scanner.nextLine().trim());
        sessionService.filterOnDate(date).forEach(System.out::println);

    }
    private void reportSessionsOfClientBetweenDates() {
        int clientID = askInteger("Client ID");
        System.out.print("Start date (yyyy-MM-dd): ");
        LocalDate start = LocalDate.parse(scanner.nextLine().trim());
        System.out.print("End date (yyyy-MM-dd): ");
        LocalDate end = LocalDate.parse(scanner.nextLine().trim());

        List<Session> sessions =
                sessionService.sessionsOfClientBetweenDates(clientID, start, end);

        if (sessions.isEmpty()) {
            System.out.println("No sessions for this client in the given interval.");
        } else {
            sessions.forEach(System.out::println);
        }
    }
    private void reportSessionsOfClientWithKeyword() {
        int clientID = askInteger("Client ID");
        System.out.print("Keyword in description: ");
        String keyword = scanner.nextLine().trim();

        List<Session> sessions =
                sessionService.sessionsOfClientWithDescriptionKeyword(clientID, keyword);

        if (sessions.isEmpty()) {
            System.out.println("No sessions found with that keyword for this client.");
        } else {
            sessions.forEach(System.out::println);
        }
    }
    private void reportSessionCountPerClient() {
        Map<Integer, Long> map = sessionService.sessionCountPerClient();
        if (map.isEmpty()) {
            System.out.println("No sessions found.");
            return;
        }
        map.forEach((clientId, count) ->
                System.out.println("Client " + clientId + " has " + count + " sessions.")
        );
    }
    private void reportNextUpcomingSessionPerClient() {
        Map<Integer, Session> map = sessionService.nextUpcomingSessionPerClient();
        if (map.isEmpty()) {
            System.out.println("No upcoming sessions.");
            return;
        }
        map.forEach((clientId, session) -> {
            if (session != null) {
                System.out.println("Client " + clientId + " next session: " + session);
            } else {
                System.out.println("Client " + clientId + " has no upcoming sessions.");
            }
        });
    }
    private void reportSessionsByDayForClient() {
        int clientID = askInteger("Client ID");
        Map<LocalDate, List<Session>> map = sessionService.sessionsByDayForClient(clientID);
        if (map.isEmpty()) {
            System.out.println("No sessions for this client.");
            return;
        }

        map.forEach((date, sessions) -> {
            System.out.println("=== " + date + " ===");
            sessions.forEach(System.out::println);
        });
    }

}
