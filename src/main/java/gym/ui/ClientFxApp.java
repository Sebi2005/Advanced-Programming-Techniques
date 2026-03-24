package gym.ui;
import gym.domain.Client;
import gym.domain.Session;
import gym.repository.core.FilteredRepoAdapter;
import gym.repository.core.FilteredRepository;
import gym.repository.core.client.ClientBinaryRepository;
import gym.repository.core.client.ClientJDBCRepository;
import gym.repository.core.client.ClientRepository;
import gym.repository.core.client.ClientTextRepository;
import gym.repository.core.session.SessionBinaryRepository;
import gym.repository.core.session.SessionJDBCRepository;
import gym.repository.core.session.SessionRepository;
import gym.repository.core.session.SessionTextRepository;
import gym.service.ClientService;
import gym.service.SessionService;
import gym.repository.core.DbUtil;
import gym.common.RepositoryException;
import gym.common.ValidationException;

import gym.undo.UndoRedoService;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Properties;

import static gym.Constants.*;

public class ClientFxApp extends Application {

    private ClientService clientService;
    private TableView<Client> tableView;
    private TextField idField;
    private TextField nameField;
    private TextField emailField;
    private TextField phoneField;

    @Override
    public void start(Stage primaryStage) {
        this.clientService = buildClientService();

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setTop(buildFormPane());
        root.setCenter(buildTable());
        root.setBottom(buildButtons());

        refreshTable();

        Scene scene = new Scene(root,750,500);
        primaryStage.setTitle("Client Management");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    private ClientService buildClientService() {
        Properties properties = new Properties();
        try(InputStream inputStream = ClientFxApp.class.getResourceAsStream("/settings.proper2ties")) {
            if (inputStream != null) {
                properties.load(inputStream);
            } else {
                System.out.println("Warning: couldn't load settings.properties from resources. Using memory repos.");
            }
        } catch (Exception e) {
            System.out.println("Warning: couldn't load settings.properties from resources. Using memory repos.");
        }
        String repoType = properties.getProperty("Repository", "memory").trim().toLowerCase();
        String location = properties.getProperty("Location", "data").trim();
        String dbFile   = properties.getProperty("Database", "gym.db").trim();
        String clientsTable = properties.getProperty("Clients", "clients").trim();
        String sessionsTable = properties.getProperty("Sessions", "sessions").trim();

        FilteredRepository<Integer, Client> clientRepository;
        FilteredRepository<Integer, Session> sessionRepository;

        switch (repoType) {
            case "text":
                clientRepository = new ClientTextRepository(properties.getProperty("Clients", "data/clients.txt"));
                sessionRepository = new SessionTextRepository(properties.getProperty("Sessions", "data/sessions.txt"));
                break;
            case "binary":
                clientRepository = new ClientBinaryRepository(properties.getProperty("Clients", "clients.bin"));
                sessionRepository = new SessionBinaryRepository(properties.getProperty("Sessions", "sessions.bin"));
                break;
            case "database":
                String url = DbUtil.sqliteUrl(location, dbFile);
                var clientJdbc = new ClientJDBCRepository(url, clientsTable);
                var sessionJdbc = new SessionJDBCRepository(url, sessionsTable);
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
        return new ClientService(clientRepository,sessionService,undoRedoService);
    }

    private Pane buildFormPane() {
        GridPane formPane = new GridPane();
        formPane.setHgap(10);
        formPane.setVgap(10);
        formPane.setPadding(new Insets(10));

        Label idLabel = new Label("ID:");
        Label nameLabel = new Label("Name:");
        Label emailLabel = new Label("Email:");
        Label phoneLabel = new Label("Phone:");

        idField = new TextField();
        nameField = new TextField();
        emailField = new TextField();
        phoneField = new TextField();

        formPane.add(idLabel, 0, 0);
        formPane.add(idField, 1, 0);

        formPane.add(nameLabel, 0 ,1);
        formPane.add(nameField, 1, 1);

        formPane.add(emailLabel, 0 ,2);
        formPane.add(emailField, 1, 2);

        formPane.add(phoneLabel, 0, 3);
        formPane.add(phoneField, 1, 3);

        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(25);
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setPercentWidth(75);
        formPane.getColumnConstraints().addAll(column1, column2);
        return formPane;


    }

    private TableView<Client> buildTable() {
        tableView = new TableView<>();

        TableColumn<Client, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setPrefWidth(80);

        TableColumn<Client,String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setPrefWidth(180);

        TableColumn<Client, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailColumn.setPrefWidth(220);

        TableColumn<Client, String> phoneColumn = new TableColumn<>("Phone");
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        phoneColumn.setPrefWidth(150);

        tableView.getColumns().addAll(idColumn,nameColumn,emailColumn,phoneColumn);

        tableView.getSelectionModel().selectedItemProperty().addListener((observableValue,oldSelect,newSelect)->{
            if (newSelect != null) {
                idField.setText(String.valueOf(newSelect.getId()));
                nameField.setText(newSelect.getName());
                emailField.setText(newSelect.getEmail());
                phoneField.setText(newSelect.getPhone());
            }
        });
        return tableView;


    }
    private Pane buildButtons(){
        Button addButton = new Button("Add");
        Button updateButton = new  Button("Update");
        Button deleteButton = new Button("Delete");
        Button clearButton = new Button("Clear");
        Button refreshButton = new Button("Refresh");

        addButton.setOnAction(event -> handleAdd());
        updateButton.setOnAction(event->handleUpdate());
        deleteButton.setOnAction(event->handleDelete());
        clearButton.setOnAction(event->clearForm());
        refreshButton.setOnAction(event->refreshTable());

        HBox box = new HBox(10, addButton, updateButton, deleteButton, clearButton, refreshButton);
        box.setAlignment(Pos.CENTER_RIGHT);
        box.setPadding(new Insets(10));

        return box;
    }
    private int parseId() {
        String text = idField.getText().trim();
        if (text.isEmpty()) {
            throw new NumberFormatException("Empty ID");
        }
        return Integer.parseInt(text);
    }
    private void handleAdd() {
        try {
            int id = parseId();
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();

            clientService.addClient(id, name, email, phone);
            refreshTable();
            clearForm();
        } catch (ValidationException | RepositoryException ex){
            showError("Error adding client",ex.getMessage());
        } catch (NumberFormatException nfe) {
            showError("Invalid ID", "ID must be an integer.");
        } catch (Exception ex) {
            showError("Unexpected error",ex.getMessage());
        }
    }

    private void handleUpdate() {
        try {
            int id = parseId();
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();

            clientService.updateClient(id, name, email, phone);
            refreshTable();

        } catch (ValidationException | RepositoryException ex) {
            showError("Error updating client",ex.getMessage());
        } catch (NumberFormatException nfe) {
            showError("Invalid ID", "ID must be an integer. ");
        } catch(Exception ex) {
            showError("Unexpected error",ex.getMessage());
        }
    }
    private void handleDelete() {
        try {
            int id  = parseId();
            clientService.removeClient(id);
            refreshTable();
            clearForm();
        }catch (RepositoryException ex) {
            showError("Error deleting client", ex.getMessage());
        } catch (NumberFormatException nfe) {
            showError("Invalid ID", "ID must be an integer.");
        } catch (Exception ex) {
            showError("Unexpected error", ex.getMessage());
        }
    }
    private void clearForm() {
        idField.clear();
        nameField.clear();
        emailField.clear();
        phoneField.clear();
        tableView.getSelectionModel().clearSelection();

    }
    private void refreshTable() {
        ObservableList<Client> items = FXCollections.observableArrayList();
        clientService.getAllClients().forEach(items::add);
        tableView.setItems(items);
    }
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
