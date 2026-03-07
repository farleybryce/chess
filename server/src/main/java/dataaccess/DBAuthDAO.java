package dataaccess;

import model.AuthData;

import java.sql.Connection;
import java.sql.SQLException;

import static dataaccess.DatabaseManager.configureDatabase;

public class DBAuthDAO implements AuthDAO {

    public DBAuthDAO() throws DataAccessException{
        configureDatabase(createStatements);
    }

    public AuthData createAuth(String username) throws DataAccessException {
        return null;
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    public void deleteAuth(String authToken) throws DataAccessException {

    }

    public void clear() throws DataAccessException {

    }

    private final String[] createStatements = {
        """
        CREATE TABLE IF NOT EXISTS  auth (
          `id` int NOT NULL AUTO_INCREMENT,
          `username` varchar(256) NOT NULL,
          `authToken` varchar(256) NOT NULL,
          `json` TEXT DEFAULT NULL,
          PRIMARY KEY (`id`),
          INDEX(authToken),
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
        """
    };


}
