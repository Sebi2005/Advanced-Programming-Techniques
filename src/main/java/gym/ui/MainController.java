package gym.ui;

import gym.common.ValidationException;
import gym.domain.Client;
import gym.domain.Session;
import gym.service.ClientService;
import gym.service.SessionService;
import gym.undo.UndoRedoService;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

public class MainController {

    private ClientService clientService;
    private SessionService sessionService;
    private UndoRedoService undoRedoService;


    @FXML private TableView<Client> clientTableView;
    @FXML private TableColumn<Client, Integer> columnClientId;
    @FXML private TableColumn<Client, String> columnClientName;
    @FXML private TableColumn<Client, String> columnClientEmail;
    @FXML private TableColumn<Client, String> columnClientPhone;

    @FXML private TextField textFieldClientId, textFieldClientName, textFieldClientEmail, textFieldClientPhone;
    @FXML private TextField textFieldClientFilterName, textFieldClientFilterEmail;

    private final ObservableList<Client> clientsModel = FXCollections.observableArrayList();


    @FXML private TableView<Session> sessionTableView;
    @FXML private TableColumn<Session, Integer> columnSessionId;
    @FXML private TableColumn<Session, Integer> columnSessionClientId;
    @FXML private TableColumn<Session, LocalDateTime> columnSessionDateTime;
    @FXML private TableColumn<Session, String> columnSessionDescription;

    @FXML private TextField textFieldSessionId, textFieldSessionClientId, textFieldSessionTime;
    @FXML private DatePicker datePickerSessionDate;

    @FXML private TextArea textAreaSessionDesc;

    @FXML private TextField textFieldSessionFilterClientId;
    @FXML private DatePicker datePickerSessionFilterDate;

    private final ObservableList<Session> sessionsModel = FXCollections.observableArrayList();


    @FXML private TextField textFieldReportClientId, textFieldReportClientId2, textFieldReportClientId3, textFieldReportKeyword;
    @FXML private DatePicker datePickerReportStart, datePickerReportEnd;
    @FXML private TextArea textAreaReportOutput;

    @FXML private Button buttonUndo;
    @FXML private Button buttonRedo;
    @FXML private Label labelStatus;


    public void setServices(ClientService clientService, SessionService sessionService,UndoRedoService undoRedoService) {
        this.clientService = clientService;
        this.sessionService = sessionService;
        this.undoRedoService = undoRedoService;
        refreshClients();
        refreshSessions();
        updateUndoRedoButtons();
    }
    private void updateUndoRedoButtons() {
        if (undoRedoService == null) return;
        buttonUndo.setDisable(!undoRedoService.canUndo());
        buttonRedo.setDisable(!undoRedoService.canRedo());
    }
    private final gym.service.multithreading.ClientBulkInMemoryService bulk = new gym.service.multithreading.ClientBulkInMemoryService();

    @FXML
    private void initialize() {

        columnClientId.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("id"));
        columnClientName.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("name"));
        columnClientEmail.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("email"));
        columnClientPhone.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("phone"));
        clientTableView.setItems(clientsModel);


        columnSessionId.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("id"));
        columnSessionClientId.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("clientId"));
        columnSessionDateTime.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("dateTime"));
        columnSessionDescription.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("description"));
        sessionTableView.setItems(sessionsModel);


        clientTableView.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, oldValue, newValue) -> { if (newValue != null) fillClientForm(newValue); }
        );
        sessionTableView.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, oldValue, newValue) -> { if (newValue != null) fillSessionForm(newValue); }
        );


        ChangeListener<String> clientFilterListener = (observableValue, oldValue, newValue) -> applyClientFilters();
        textFieldClientFilterName.textProperty().addListener(clientFilterListener);
        textFieldClientFilterEmail.textProperty().addListener(clientFilterListener);
    }



    @FXML private void onRefreshClients() { refreshClients(); }

    private void refreshClients() {
        if (clientService == null) return;
        clientsModel.setAll(iterableToList(clientService.getAllClients()));
        applyClientFilters();
    }

    private void applyClientFilters() {
        if (clientService == null) return;

        String name = textFieldClientFilterName.getText() == null ? "" : textFieldClientFilterName.getText().trim();
        String email = textFieldClientFilterEmail.getText() == null ? "" : textFieldClientFilterEmail.getText().trim();

        Iterable<Client> base = clientService.getAllClients();


        if (!name.isEmpty()) {
            base = clientService.filterByNameContains(name);
        }


        if (!email.isEmpty()) {
            Iterable<Client> byEmail = clientService.filterByEmailContains(email);
            if (!name.isEmpty()) {

                var nameList = iterableToList(base);
                var emailList = iterableToList(byEmail);
                nameList.retainAll(emailList);
                clientsModel.setAll(nameList);
                return;
            }
            clientsModel.setAll(iterableToList(byEmail));
            return;
        }


        clientsModel.setAll(iterableToList(base));
    }

    @FXML private void onClearClientFilters() {
        textFieldClientFilterName.clear();
        textFieldClientFilterEmail.clear();
        refreshClients();
    }

    @FXML private void onAddClient() {
        try {
            int id = parseInt(textFieldClientId, "Client ID");
            clientService.addClient(id, textFieldClientName.getText(), textFieldClientEmail.getText(), textFieldClientPhone.getText());
            refreshClients();
            onClearClientForm();
            updateUndoRedoButtons();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    @FXML private void onUpdateClient() {
        try {
            int id = parseInt(textFieldClientId, "Client ID");
            clientService.updateClient(id, textFieldClientName.getText(), textFieldClientEmail.getText(), textFieldClientPhone.getText());
            refreshClients();
            updateUndoRedoButtons();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    @FXML private void onDeleteClient() {
        try {
            int id = parseInt(textFieldClientId, "Client ID");
            clientService.removeClient(id);
            refreshClients();
            refreshSessions();
            onClearClientForm();
            updateUndoRedoButtons();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    @FXML private void onClearClientForm() {
        textFieldClientId.clear(); textFieldClientName.clear(); textFieldClientEmail.clear(); textFieldClientPhone.clear();
        clientTableView.getSelectionModel().clearSelection();
    }

    private void fillClientForm(Client c) {
        textFieldClientId.setText(String.valueOf(c.getId()));
        textFieldClientName.setText(c.getName());
        textFieldClientEmail.setText(c.getEmail());
        textFieldClientPhone.setText(c.getPhone());
    }



    @FXML private void onRefreshSessions() { refreshSessions(); }

    private void refreshSessions() {
        if (sessionService == null) return;
        sessionsModel.setAll(iterableToList(sessionService.getAllSessions()));
    }

    @FXML private void onApplySessionFilters() {
        try {
            boolean hasClientId = textFieldSessionFilterClientId.getText() != null && !textFieldSessionFilterClientId.getText().isBlank();
            boolean hasDate = datePickerSessionFilterDate.getValue() != null;

            if (!hasClientId && !hasDate) {
                refreshSessions();
                return;
            }

            if (hasClientId && hasDate) {

                int clientId = Integer.parseInt(textFieldSessionFilterClientId.getText().trim());
                LocalDate date = datePickerSessionFilterDate.getValue();
                var byClient = iterableToList(sessionService.filterByClientId(clientId));
                var byDate = iterableToList(sessionService.filterOnDate(date));
                byClient.retainAll(byDate);
                sessionsModel.setAll(byClient);
                return;
            }

            if (hasClientId) {
                int clientId = Integer.parseInt(textFieldSessionFilterClientId.getText().trim());
                sessionsModel.setAll(iterableToList(sessionService.filterByClientId(clientId)));
                return;
            }

            LocalDate date = datePickerSessionFilterDate.getValue();
            sessionsModel.setAll(iterableToList(sessionService.filterOnDate(date)));

        } catch (Exception ex) {
            showError(ex);
        }
    }

    @FXML private void onClearSessionFilters() {
        textFieldSessionFilterClientId.clear();
        datePickerSessionFilterDate.setValue(null);
        refreshSessions();
    }

    @FXML private void onAddSession() {
        try {
            int id = parseInt(textFieldSessionId, "Session ID");
            int clientId = parseInt(textFieldSessionClientId, "Client ID");
            LocalDateTime dt = parseDateTime(datePickerSessionDate, textFieldSessionTime);
            sessionService.addSession(id, clientId, dt, textAreaSessionDesc.getText());
            refreshSessions();
            onClearSessionForm();
            updateUndoRedoButtons();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    @FXML private void onUpdateSession() {
        try {
            int id = parseInt(textFieldSessionId, "Session ID");
            int clientId = parseInt(textFieldSessionClientId, "Client ID");
            LocalDateTime dt = parseDateTime(datePickerSessionDate, textFieldSessionTime);
            sessionService.updateSession(id, clientId, dt, textAreaSessionDesc.getText());
            refreshSessions();
            updateUndoRedoButtons();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    @FXML private void onDeleteSession() {
        try {
            int id = parseInt(textFieldSessionId, "Session ID");
            sessionService.removeSession(id);
            refreshSessions();
            onClearSessionForm();
            updateUndoRedoButtons();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    @FXML private void onClearSessionForm() {
        textFieldSessionId.clear();
        textFieldSessionClientId.clear();
        datePickerSessionDate.setValue(null);
        textFieldSessionTime.clear();
        textAreaSessionDesc.clear();
        sessionTableView.getSelectionModel().clearSelection();
    }

    private void fillSessionForm(Session session) {
        textFieldSessionId.setText(String.valueOf(session.getId()));
        textFieldSessionClientId.setText(String.valueOf(session.getClientId()));
        if (session.getDateTime() != null) {
            datePickerSessionDate.setValue(session.getDateTime().toLocalDate());
            textFieldSessionTime.setText(session.getDateTime().toLocalTime().toString());
        } else {
            datePickerSessionDate.setValue(null);
            textFieldSessionTime.clear();
        }
        textAreaSessionDesc.setText(session.getDescription());
    }



    @FXML private void onClearReportOutput() { textAreaReportOutput.clear(); }

    @FXML private void onReportBetweenDates() {
        try {
            int clientId = parseInt(textFieldReportClientId, "Client ID");
            LocalDate start = datePickerReportStart.getValue();
            LocalDate end = datePickerReportEnd.getValue();
            if (start == null || end == null) throw new ValidationException("Start and End date must be set.");

            List<Session> result = sessionService.sessionsOfClientBetweenDates(clientId, start, end);
            textAreaReportOutput.setText(formatSessions(result));
        } catch (Exception ex) {
            showError(ex);
        }
    }

    @FXML private void onReportWithKeyword() {
        try {
            int clientId = parseInt(textFieldReportClientId2, "Client ID");
            String keyword = textFieldReportKeyword.getText();
            List<Session> result = sessionService.sessionsOfClientWithDescriptionKeyword(clientId, keyword);
            textAreaReportOutput.setText(formatSessions(result));
        } catch (Exception ex) {
            showError(ex);
        }
    }

    @FXML private void onReportCountPerClient() {
        try {
            Map<Integer, Long> map = sessionService.sessionCountPerClient();
            textAreaReportOutput.setText(formatMap(map));
        } catch (Exception ex) {
            showError(ex);
        }
    }

    @FXML private void onReportNextUpcoming() {
        try {
            Map<Integer, Session> map = sessionService.nextUpcomingSessionPerClient();
            textAreaReportOutput.setText(formatMapSessions(map));
        } catch (Exception ex) {
            showError(ex);
        }
    }

    @FXML private void onReportSessionsByDay() {
        try {
            int clientId = parseInt(textFieldReportClientId3, "Client ID");
            Map<LocalDate, List<Session>> map = sessionService.sessionsByDayForClient(clientId);
            StringBuilder stringBuilder = new StringBuilder();
            map.forEach((day, sessions) -> {
                stringBuilder.append(day).append("\n");
                sessions.forEach(s -> stringBuilder.append("  - ").append(s).append("\n"));
                stringBuilder.append("\n");
            });
            textAreaReportOutput.setText(stringBuilder.toString());
        } catch (Exception ex) {
            showError(ex);
        }
    }
    @FXML private void onUndo() {
        try {
            undoRedoService.undo();
            refreshClients();
            refreshSessions();
            labelStatus.setText("Undo performed.");
            updateUndoRedoButtons();
        } catch(Exception ex) {
            showError(ex);
        }
    }
    @FXML private void onRedo() {
        try {
            undoRedoService.redo();
            refreshClients();
            refreshSessions();
            labelStatus.setText("Redo performed.");
            updateUndoRedoButtons();
        } catch(Exception ex) {
            showError(ex);
        }
    }
    @FXML
    private void onRunBulkInMemoryTest() {
        try {
            int n = 100_000;
            int threads = 10;

            var data1 = bulk.generateClients(n);
            long tThreads = bulk.runWithThreads(data1, threads);

            var data2 = bulk.generateClients(n);
            long tExec = bulk.runWithExecutor(data2, threads);

            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Bulk Test");
            a.setHeaderText("In-memory bulk update done");
            a.setContentText("n=" + n + ", threads=" + threads +
                    "\nThreads time: " + tThreads + " ms" +
                    "\nExecutor time: " + tExec + " ms" +
                    "\nExample client: " + data2.get(2));
            a.showAndWait();
        } catch (Exception ex) {
            showError(ex);
        }
    }



    private int parseInt(TextField textField, String fieldName) {
        try {
            return Integer.parseInt(textField.getText().trim());
        } catch (Exception e) {
            throw new ValidationException(fieldName + " must be an integer.");
        }
    }

    private LocalDateTime parseDateTime(DatePicker datePickerDate, TextField textFieldTime) {
        LocalDate date = datePickerDate.getValue();
        if (date == null) throw new ValidationException("Date must be set.");

        String timeStr = textFieldTime.getText() == null ? "" : textFieldTime.getText().trim();
        if (timeStr.isEmpty()) throw new ValidationException("Time must be set (HH:mm).");

        try {
            LocalTime time = LocalTime.parse(timeStr);
            return LocalDateTime.of(date, time);
        } catch (DateTimeParseException ex) {
            throw new ValidationException("Time must be in format HH:mm (e.g., 14:30).");
        }
    }

    private <T> java.util.List<T> iterableToList(Iterable<T> iterable) {
        var list = new java.util.ArrayList<T>();
        iterable.forEach(list::add);
        return list;
    }

    private String formatSessions(List<Session> sessions) {
        if (sessions.isEmpty()) return "(no results)";
        StringBuilder sb = new StringBuilder();
        for (Session session : sessions) sb.append(session).append("\n");
        return sb.toString();
    }

    private <KeyType, ValueType> String formatMap(Map<KeyType, ValueType> map) {
        if (map.isEmpty()) return "(no results)";
        StringBuilder stringBuilder = new StringBuilder();
        map.forEach((keyType, valueType) -> stringBuilder.append(keyType).append(" -> ").append(valueType).append("\n"));
        return stringBuilder.toString();
    }

    private String formatMapSessions(Map<Integer, Session> map) {
        if (map.isEmpty()) return "(no results)";
        StringBuilder stringBuilder = new StringBuilder();
        map.forEach((clientId, session) -> stringBuilder.append("Client ").append(clientId).append(" -> ").append(session == null ? "(none)" : session).append("\n"));
        return stringBuilder.toString();
    }

    private void showError(Exception ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(ex.getClass().getSimpleName());
        alert.setContentText(ex.getMessage());
        alert.showAndWait();
    }
}

