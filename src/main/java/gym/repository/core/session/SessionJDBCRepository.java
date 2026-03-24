package gym.repository.core.session;
import gym.common.EntityNotFoundException;
import gym.common.RepositoryException;
import gym.domain.Session;
import gym.repository.core.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import static gym.Constants.*;
import java.util.Optional;
public class SessionJDBCRepository implements Repository<Integer,Session>{
    private final String url;
    private final String table;
    public SessionJDBCRepository(String url, String table) {
        this.url = url;
        this.table = table == null || table.isBlank() ? "sessions" : table;
        init();
    }
    void init() {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS " + table + " (" +
                "id INTEGER PRIMARY KEY," +
                "client_id INTEGER NOT NULL," +
                "date_time TEXT NOT NULL," +
                "description TEXT NOT NULL)";
        try (Connection connection = DriverManager.getConnection(url);
             Statement statement = connection.createStatement()) {
            statement.execute(createTableQuery);
        } catch (SQLException e) {
            throw new RepositoryException("DB init failed for " + table, e);
        }
    }

    @Override
    public void create(Session session) {
        String insertQuery = "INSERT INTO " + table + " (id,client_id,date_time,description) VALUES (?,?,?,?)";
        try (Connection connection = DriverManager.getConnection(url);
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setInt(SQL_SESSION_ID,session.getId());
            preparedStatement.setInt(SQL_SESSION_CLIENT_ID,session.getClientId());
            preparedStatement.setString(SQL_SESSION_DATE, session.getDateTime().toString());
            preparedStatement.setString(SQL_SESSION_DESCRIPTION, session.getDescription());
            preparedStatement.executeUpdate();
        } catch(SQLException e) {
            throw new RepositoryException("Failed inserting session: "+session.getId(), e);
        }
    }

    @Override
    public Optional<Session> read(Integer id) {
        String readQuery = "SELECT id,client_id,date_time,description FROM " + table + " WHERE id=?";
        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement preparedStatement = connection.prepareStatement(readQuery)) {
            preparedStatement.setInt(SQL_SESSION_ID, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (!resultSet.next()) return Optional.empty();
                Session session = new Session(
                        resultSet.getInt(SQL_SESSION_ID),
                        resultSet.getInt(SQL_SESSION_CLIENT_ID),
                        LocalDateTime.parse(resultSet.getString(SQL_SESSION_DATE)),
                        resultSet.getString(SQL_SESSION_DESCRIPTION));
                return Optional.of(session);
            }
        } catch (SQLException ex) {
            throw new RepositoryException("Read failed: " + id, ex);
        }
    }

    @Override
    public Iterable<Session> readAll() {
        String readAllQuery = "SELECT id,client_id,date_time,description FROM " + table;
        java.util.List<Session> sessions = new java.util.ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url);
            PreparedStatement preparedStatement = connection.prepareStatement(readAllQuery);
            ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                sessions.add(new Session(
                        resultSet.getInt(SQL_SESSION_ID),
                        resultSet.getInt(SQL_SESSION_CLIENT_ID),
                        LocalDateTime.parse(resultSet.getString(SQL_SESSION_DATE)),
                        resultSet.getString(SQL_SESSION_DESCRIPTION)));
            }
            return sessions;
        }catch (SQLException ex) {
            throw new RepositoryException("ReadAll failed", ex);
        }
    }

    @Override
    public void update(Session session){
        String updateQuery = "UPDATE " + table + " SET client_id=?, date_time=?, description=? WHERE id=?";
        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setInt(SQL_SESSION_UPDATED_CLIENT_ID, session.getClientId());
            preparedStatement.setString(SQL_SESSION_UPDATED_DATE, session.getDateTime().toString());
            preparedStatement.setString(SQL_SESSION_UPDATED_DESCRIPTION, session.getDescription());
            preparedStatement.setInt(SQL_SESSION_ID_TO_UPDATE, session.getId());
            int executionStatus = preparedStatement.executeUpdate();
            if (executionStatus == FAILED_EXECUTION) throw new EntityNotFoundException(session.getId());
        } catch (SQLException ex) {
            throw new RepositoryException("Update failed: " + session.getId(), ex);
        }
    }
    public Optional<Session> delete(Integer id){
        Optional<Session> sessionToDelete = read(id);
        if (sessionToDelete.isEmpty()) return Optional.empty();
        String deleteQuery = "DELETE FROM " + table + " WHERE id=?";
        try(Connection connection = DriverManager.getConnection(url);
           PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)){
            preparedStatement.setInt(SQL_SESSION_ID,id);
            preparedStatement.executeUpdate();
            return sessionToDelete;
        } catch (SQLException ex) {
            throw new RepositoryException("Delete failed: "+id,ex);
        }
    }

    @Override
    public int count() {
        String countQuery = "SELECT COUNT(*) FROM " + table;
        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement preparedStatement = connection.prepareStatement(countQuery);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            return resultSet.next() ? resultSet.getInt(COUNT_VALUE) : NO_VALUES;
        } catch (SQLException ex) {
            throw new RepositoryException("Count failed", ex);
        }
    }
}
