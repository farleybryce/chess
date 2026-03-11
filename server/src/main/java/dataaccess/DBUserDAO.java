package dataaccess;

import com.google.gson.Gson;
import model.UserData;

import java.sql.*;

import static dataaccess.DatabaseManager.configureDatabase;
import static dataaccess.DatabaseManager.executeUpdate;

public class DBUserDAO implements UserDAO {

    public DBUserDAO() throws DataAccessException{
        String[] createStatements = {
                """
        CREATE TABLE IF NOT EXISTS  users (
          `id` int NOT NULL AUTO_INCREMENT,
          `username` varchar(256) NOT NULL,
          `password` varchar(256) NOT NULL,
          `email` varchar(256) NOT NULL,
          `json` TEXT DEFAULT NULL,
          PRIMARY KEY (`id`),
          INDEX(username)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
        """
        };
        configureDatabase(createStatements);
    }

    public UserData getUser(String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT json FROM users WHERE username=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(500, String.format("Error: Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    public void createUser(UserData userData) throws DataAccessException {
        var statement = "INSERT INTO users (username, password, email, json) VALUES (?, ?, ?, ?)";
        String json = new Gson().toJson(userData);
        executeUpdate(statement, userData.username(), userData.password(), userData.email(), json);
    }

    public void clear() throws DataAccessException {
        var statement = "TRUNCATE users";
        executeUpdate(statement);
    }

    private UserData readUser(ResultSet rs) throws SQLException {
        var json = rs.getString("json");
        return new Gson().fromJson(json, UserData.class);
    }

}
