package dataaccess;

import com.google.gson.Gson;
import model.AuthData;
import model.UserData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import static dataaccess.DatabaseManager.configureDatabase;
import static dataaccess.DatabaseManager.executeUpdate;

public class DBAuthDAO implements AuthDAO {

    public DBAuthDAO() throws DataAccessException{
        String[] createStatements = {
                """
        CREATE TABLE IF NOT EXISTS  auth (
          `id` int NOT NULL AUTO_INCREMENT,
          `authToken` varchar(256) NOT NULL,
          `username` varchar(256) NOT NULL,
          `json` TEXT DEFAULT NULL,
          PRIMARY KEY (`id`),
          INDEX(authToken)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
        """
        };
        configureDatabase(createStatements);
    }

    private static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public AuthData createAuth(String username) throws DataAccessException {
        String authToken = generateToken();
        AuthData authData = new AuthData(authToken, username);
        var statement = "INSERT INTO auth (authToken, username, json) VALUES (?, ?, ?)";
        String json = new Gson().toJson(authData);
        executeUpdate(statement, authData.authToken(), authData.username(), json);
        return authData;
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT json FROM auth WHERE authToken=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readAuth(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(500, String.format("Error: Unable to read data: %s", e.getMessage()));
        }
        throw new DataAccessException(401, "Error: unauthorized");
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        var statement = "DELETE FROM auth WHERE authToken=?";
        executeUpdate(statement, authToken);
    }

    public void clear() throws DataAccessException {
        var statement = "TRUNCATE auth";
        executeUpdate(statement);
    }

    private AuthData readAuth(ResultSet rs) throws SQLException {
        var json = rs.getString("json");
        return new Gson().fromJson(json, AuthData.class);
    }


}
