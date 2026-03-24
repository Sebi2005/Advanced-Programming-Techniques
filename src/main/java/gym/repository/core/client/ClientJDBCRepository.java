package gym.repository.core.client;
import gym.common.EntityNotFoundException;
import gym.common.RepositoryException;
import gym.domain.Client;
import gym.repository.core.Repository;
import java.sql.*;
import static gym.Constants.*;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
public class ClientJDBCRepository implements Repository<Integer,Client> {
    private final String url;
    private final String table;
    public ClientJDBCRepository(String url, String table ){
        this.url = url;
        this.table = table == null || table.isBlank() ? "clients" : table;
        init();
    }
    private void init() {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS " + table + " ("+
                "id INTEGER PRIMARY KEY,"+
                "name TEXT NOT NULL,"+
                "email TEXT NOT NULL,"+
                "phone TEXT NOT NULL)";
        try (Connection connection =DriverManager.getConnection(url);
        Statement statement= connection.createStatement()) {
            statement.execute(createTableQuery);
        } catch (SQLException e) {
            throw new RepositoryException("DB init failed for " + table, e);
        }
    }

    @Override
    public void create(Client client) {
        String insertQuery = "INSERT INTO "+table+" (id, name, email, phone) VALUES (?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(url);
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)){
            preparedStatement.setInt(SQL_CLIENT_ID,client.getId());
            preparedStatement.setString(SQL_CLIENT_NAME, client.getName());
            preparedStatement.setString(SQL_CLIENT_EMAIL, client.getEmail());
            preparedStatement.setString(SQL_CLIENT_PHONE,client.getPhone());
            preparedStatement.executeUpdate();
        } catch(SQLException e) {
            throw new RepositoryException("Insert failed: "+client.getId(),e);
        }
    }
    @Override
    public Optional<Client> read(Integer id) {
        String readQuery = "SELECT id, name, email, phone FROM "+table+" WHERE id=?";
        try (Connection connection = DriverManager.getConnection(url);
            PreparedStatement preparedStatement= connection.prepareStatement(readQuery)) {
            preparedStatement.setInt(SQL_CLIENT_ID,id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (!resultSet.next()) return Optional.empty();
                Client client =  new Client(resultSet.getInt(SQL_CLIENT_ID), resultSet.getString(SQL_CLIENT_NAME), resultSet.getString(SQL_CLIENT_EMAIL), resultSet.getString(SQL_CLIENT_PHONE));
                return Optional.of(client);
            }
        }catch (SQLException ex) {
            throw new RepositoryException("Read failed: " + id, ex);
        }
    }

    @Override
    public Iterable<Client> readAll() {
        String readAllQuery = "SELECT id, name, email, phone FROM "+table;
        java.util.List<Client> clients = new java.util.ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url);
            PreparedStatement preparedStatement = connection.prepareStatement(readAllQuery);
            ResultSet resultSet= preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                clients.add(new Client(resultSet.getInt(SQL_CLIENT_ID), resultSet.getString(SQL_CLIENT_NAME), resultSet.getString(SQL_CLIENT_EMAIL), resultSet.getString(SQL_CLIENT_PHONE)));
            }
            return clients;
        } catch (SQLException ex) {
            throw new RepositoryException("ReadAll failed", ex);
    }
    }

    @Override
    public void update(Client client) {
        String updateQuery = "UPDATE " + table + " SET name=?, email=?, phone=? WHERE id=?";
        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement preparedStatement= connection.prepareStatement(updateQuery)) {
            preparedStatement.setString(SQL_CLIENT_UPDATED_NAME, client.getName());
            preparedStatement.setString(SQL_CLIENT_UPDATED_EMAIL, client.getEmail());
            preparedStatement.setString(SQL_CLIENT_UPDATED_PHONE, client.getPhone());
            preparedStatement.setInt(SQL_CLIENT_ID_TO_UPDATE,client.getId());
            int executionStatus = preparedStatement.executeUpdate();
            if (executionStatus == FAILED_EXECUTION) throw new EntityNotFoundException(client.getId());
        } catch (SQLException ex) {
            throw new RepositoryException("Update failed: " + client.getId(), ex);
        }
    }

    @Override
    public Optional<Client> delete(Integer id) {
        Optional<Client> clientToDelete = read(id);
        if (clientToDelete.isEmpty()) return Optional.empty();
        String deleteQuery = "DELETE FROM " + table + " WHERE id=?";
        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
            preparedStatement.setInt(SQL_CLIENT_ID, id);
            preparedStatement.executeUpdate();
            return clientToDelete;
        } catch (SQLException ex) {
            throw new RepositoryException("Delete failed: " + id, ex);
        }
    }
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
    public void clearAll() {
        String sql = "DELETE FROM " + table;
        try (Connection c = DriverManager.getConnection(url);
             Statement st = c.createStatement()) {
            st.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RepositoryException("ClearAll failed", e);
        }
    }

    public void createBatch(List<Client> clients, int batchSize) {
        String sql = "INSERT INTO " + table + " (id, name, email, phone) VALUES (?, ?, ?, ?)";
        try (Connection c = DriverManager.getConnection(url);
             PreparedStatement ps = c.prepareStatement(sql)) {

            c.setAutoCommit(false);

            int i = 0;
            for (Client cl : clients) {
                ps.setInt(1, cl.getId());
                ps.setString(2, cl.getName());
                ps.setString(3, cl.getEmail());
                ps.setString(4, cl.getPhone());
                ps.addBatch();

                i++;
                if (i % batchSize == 0) ps.executeBatch();
            }
            ps.executeBatch();
            c.commit();

        } catch (SQLException e) {
            throw new RepositoryException("createBatch failed", e);
        }
    }



}
