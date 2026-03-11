package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static dataaccess.DatabaseManager.configureDatabase;
import static dataaccess.DatabaseManager.executeUpdate;

public class DBGameDAO implements GameDAO{

    public DBGameDAO() throws DataAccessException{
        configureDatabase(createStatements);
    }

    public int createGame(String gameName) throws DataAccessException {
        var statement = "INSERT INTO game (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";
        String game = new Gson().toJson(new ChessGame());
        return executeUpdate(statement, null, null, gameName, game);
    }

    public GameData getGame(int gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM game WHERE id=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(500, String.format("Error: Unable to read data: %s", e.getMessage()));
        }
        throw new DataAccessException(400, "Error: bad request");
    }

    public void joinGame(int gameID, ChessGame.TeamColor playerColor, String username) throws DataAccessException {
        GameData gameData = this.getGame(gameID);
        String colorString = null;
        if (playerColor == ChessGame.TeamColor.WHITE) {
            if (gameData.whiteUsername() != null) {
                throw new DataAccessException(403, "Error: already taken");
            }
            colorString = "whiteUsername";
        } else {
            if (gameData.blackUsername() != null) {
                throw new DataAccessException(403, "Error: already taken");
            }
            colorString = "blackUsername";
        }

        String statement = String.format("UPDATE game SET %s=? WHERE id=?", colorString);
        executeUpdate(statement, username, gameID);
    }

    public ArrayList<GameData> listGames() throws DataAccessException {
        ArrayList<GameData> gameList = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM game";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        gameList.add(readGame(rs));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(500, String.format("Error: Unable to read data: %s", e.getMessage()));
        }
        return gameList;
    }

    public void clear() throws DataAccessException {
        var statement = "TRUNCATE game";
        executeUpdate(statement);
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        var id = rs.getInt("id");
        var whiteUsername = rs.getString("whiteUsername");
        var blackUsername = rs.getString("blackUsername");
        var gameName = rs.getString("gameName");
        var gameJson = rs.getString("game");
        ChessGame game = new Gson().fromJson(gameJson, ChessGame.class);
        return new GameData(id, whiteUsername, blackUsername, gameName, game);
    }

    private final String[] createStatements = {
            """
        CREATE TABLE IF NOT EXISTS  game (
          `id` int NOT NULL AUTO_INCREMENT,
          `whiteUsername` varchar(256),
          `blackUsername` varchar(256),
          `gameName` varchar(256) NOT NULL,
          `game` TEXT DEFAULT NULL,
          PRIMARY KEY (`id`),
          INDEX(id)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
        """
    };
}
