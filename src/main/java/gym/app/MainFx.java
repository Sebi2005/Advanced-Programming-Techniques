package gym.app;

import gym.domain.Client;
import gym.domain.Session;
import gym.repository.core.DbUtil;
import gym.repository.core.FilteredRepoAdapter;
import gym.repository.core.FilteredRepository;
import gym.repository.core.client.*;
import gym.repository.core.session.*;
import gym.service.ClientService;
import gym.service.SessionService;
import gym.ui.MainController;
import gym.undo.UndoRedoService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Alert;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Properties;

public class MainFx extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        Properties properties = new Properties();
        try (InputStream inputStream = Main.class.getResourceAsStream("/settings.properties")) {
            if (inputStream != null) properties.load(inputStream);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Configuration warning");
            alert.setHeaderText("settings.properties not found");
            alert.setContentText("""
                    The file settings.properties could not be loaded.
                    The application will use in-memory repositories.""");
            alert.showAndWait();
        }

        String repoType = properties.getProperty("Repository", "memory").trim().toLowerCase();
        String location = properties.getProperty("Location", "data").trim();
        String dbFile = properties.getProperty("Database", "gym.db").trim();
        String clientsTable = properties.getProperty("Clients", "clients").trim();
        String sessionsTable = properties.getProperty("Sessions", "sessions").trim();

        FilteredRepository<Integer, Client> clientRepository;
        FilteredRepository<Integer, Session> sessionRepository;

        switch (repoType) {
            case "text" -> {
                clientRepository = new ClientTextRepository(properties.getProperty("Clients", "data/clients.txt"));
                sessionRepository = new SessionTextRepository(properties.getProperty("Sessions", "data/sessions.txt"));
            }
            case "binary" -> {
                clientRepository = new ClientBinaryRepository(properties.getProperty("Clients", "clients.bin"));
                sessionRepository = new SessionBinaryRepository(properties.getProperty("Sessions", "sessions.bin"));
            }
            case "database" -> {
                String url = DbUtil.sqliteUrl(location, dbFile);
                var clientJdbc = new ClientJDBCRepository(url, clientsTable);
                var sessionJdbc = new SessionJDBCRepository(url, sessionsTable);
                clientRepository = new FilteredRepoAdapter<>(clientJdbc);
                sessionRepository = new FilteredRepoAdapter<>(sessionJdbc);
            }
            default -> {
                clientRepository = new ClientRepository();
                sessionRepository = new SessionRepository();
            }
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
        ClientService clientService = new ClientService(clientRepository, sessionService,undoRedoService);


        FXMLLoader loader = new FXMLLoader(MainFx.class.getResource("/gym/ui/main-view.fxml"));
        Scene scene = new Scene(loader.load());

        MainController controller = loader.getController();
        controller.setServices(clientService, sessionService,undoRedoService);

        stage.setTitle("Gym Manager");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
