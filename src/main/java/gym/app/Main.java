package gym.app;


import gym.repository.core.client.ClientBinaryRepository;
import gym.repository.core.client.ClientRepository;
import gym.repository.core.FilteredRepository;
import gym.domain.Client;
import gym.domain.Session;
import gym.repository.core.client.ClientTextRepository;
import gym.repository.core.session.SessionBinaryRepository;
import gym.repository.core.session.SessionTextRepository;
import gym.service.ClientService;
import gym.repository.core.DbUtil;
import gym.ui.ConsoleUI;
import gym.ui.SessionUI;
import java.util.Scanner;
import gym.repository.core.session.SessionRepository;
import gym.service.SessionService;
import gym.service.ClientService;
import gym.repository.core.client.ClientJDBCRepository;
import gym.repository.core.session.SessionJDBCRepository;
import gym.repository.core.FilteredRepoAdapter;
import gym.undo.UndoRedoService;

import static gym.Constants.*;
import java.time.LocalDateTime;


import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
public class Main {
    public static void main(String[] args) {
        Properties properties = new Properties();
        try (InputStream inputStream = Main.class.getResourceAsStream("/settings.properties")){
            if (inputStream != null) {properties.load(inputStream);}

        } catch(Exception e) {
            System.out.println("Warning: couldn't load settings.properties from resources. Using memory repos.");
        }

        String repoType = properties.getProperty("Repository","memory").trim().toLowerCase();
        String location = properties.getProperty("Location","data").trim();
        String dbFile   = properties.getProperty("Database", "gym.db").trim();
        String clientsTable = properties.getProperty("Clients", "clients").trim();
        String sessionsTable = properties.getProperty("Sessions", "sessions").trim();
        //String clientsPath = properties.getProperty("Clients","clients.txt").trim().toLowerCase();
        //String sessionsPath = properties.getProperty("Sessions","sessions.txt").trim().toLowerCase();
        FilteredRepository<Integer,Client> clientRepository;
        FilteredRepository<Integer,Session> sessionRepository;
        switch (repoType) {
            case "text":
                clientRepository = new ClientTextRepository(properties.getProperty("Clients","data/clients.txt"));
                sessionRepository = new SessionTextRepository(properties.getProperty("Sessions","data/sessions.txt"));
                break;
            case "binary":
                clientRepository = new ClientBinaryRepository(properties.getProperty("Clients","clients.bin"));
                sessionRepository = new SessionBinaryRepository(properties.getProperty("Sessions","sessions.bin"));
                break;
            case "database":
                String url = DbUtil.sqliteUrl(location,dbFile);
                var clientJdbc = new ClientJDBCRepository(url,clientsTable);
                var sessionJdbc = new SessionJDBCRepository(url,sessionsTable);
                clientRepository = new FilteredRepoAdapter<>(clientJdbc);
                sessionRepository = new FilteredRepoAdapter<>(sessionJdbc);
                break;
            default:
                clientRepository = new ClientRepository();
                sessionRepository = new SessionRepository();
        }
        if ((repoType.equals("text") || repoType.equals("binary")) && clientRepository.count() == 0) {
            clientRepository.create(new Client(1, "Ana Pop", "ana@mail.com", "0721000001"));
            clientRepository.create(new Client(2, "Bogdan Ilie", "bogdan@mail.com", "0722000002"));
        }
        if ((repoType.equals("text") || repoType.equals("binary")) && sessionRepository.count() == 0) {
            sessionRepository.create(new Session(1, 1, LocalDateTime.now().plusDays(1), "Intro workout"));
        }

        UndoRedoService undoRedoService = new UndoRedoService();
        SessionService sessionService = new SessionService(sessionRepository,undoRedoService);
        ClientService clientService = new ClientService(clientRepository,sessionService,undoRedoService);
        var ClientUI = new ConsoleUI(clientService);
        var SessionUI = new SessionUI(sessionService);
        Scanner input = new Scanner(System.in);
        while (true) {
            System.out.println("=== MAIN MENU ===");
            System.out.println(CLIENT_MENU + ". Manage Clients");
            System.out.println(SESSION_MENU + ". Manage Sessions");
            System.out.println(EXIT_MENU + ". Exit");
            System.out.print("Choose: ");
            String choice = input.nextLine().trim();
            switch (choice) {
                case CLIENT_MENU: ClientUI.run(); break;
                case SESSION_MENU: SessionUI.run(); break;
                case EXIT_MENU: System.out.println("Bye!"); return;
                default: System.out.println("Invalid choice!");

            }

        }
    }
}
