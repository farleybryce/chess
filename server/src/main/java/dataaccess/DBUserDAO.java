package dataaccess;

import model.UserData;

import java.sql.*;

import static dataaccess.DatabaseManager.configureDatabase;

public class DBUserDAO implements UserDAO {

    public DBUserDAO() throws DataAccessException{
        configureDatabase(createStatements);
    }

    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    public void createUser(UserData userData) throws DataAccessException {

    }

    public void clear() throws DataAccessException {

    }

    private final String[] createStatements = {
        """
        CREATE TABLE IF NOT EXISTS  pet (
          `id` int NOT NULL AUTO_INCREMENT,
          `username` varchar(256) NOT NULL,
          `password` varchar(256) NOT NULL,
          `email` varchar(256) NOT NULL,
          `json` TEXT DEFAULT NULL,
          PRIMARY KEY (`id`),
          INDEX(username),
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
        """
    };
}
