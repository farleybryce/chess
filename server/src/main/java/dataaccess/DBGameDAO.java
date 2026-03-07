package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;

import static dataaccess.DatabaseManager.configureDatabase;

public class DBGameDAO implements GameDAO{

    public DBGameDAO() throws DataAccessException{
        configureDatabase(createStatements);
    }

    public int createGame(String gameName) throws DataAccessException {
        return 0;
    }

    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    public void joinGame(int gameID, ChessGame.TeamColor playerColor, String username) throws DataAccessException {

    }

    public ArrayList<GameData> listGames() throws DataAccessException {
        return null;
    }

    public void clear() throws DataAccessException {

    }

    private final String[] createStatements = {
            """
        CREATE TABLE IF NOT EXISTS  pet (
          `id` int NOT NULL AUTO_INCREMENT,
          `whiteUsername` varchar(256),
          `blackUsername` varchar(256),
          `gameName` varchar(256) NOT NULL,
          `game` TEXT DEFAULT NULL,
          PRIMARY KEY (`id`),
          INDEX(id),
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
        """
    };
}
