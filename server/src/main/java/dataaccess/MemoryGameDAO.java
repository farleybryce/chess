package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

public class MemoryGameDAO implements GameDAO {

    private int nextID = 0;
    final private HashMap<Integer, GameData> gameDataHashMap = new HashMap<>();

    public int createGame(String gameName) {
        int gameID = ++nextID;
        gameDataHashMap.put(gameID, new GameData(gameID, null,null, gameName, new ChessGame()));
        return gameID;
    }

    public GameData getGame(int gameID) throws DataAccessException {
        if (!gameDataHashMap.containsKey(gameID)) {
            throw new DataAccessException(400, "Error: bad request");
        }
        return gameDataHashMap.get(gameID);
    }

    public void joinGame(int gameID, ChessGame.TeamColor playerColor, String username) throws DataAccessException {
        GameData gameData = gameDataHashMap.get(gameID);
        GameData updatedGameData;
        if (playerColor == ChessGame.TeamColor.WHITE) {
            if (gameData.whiteUsername() != null) {
                throw new DataAccessException(403, "Error: already taken");
            }
            updatedGameData = new GameData(gameID, username, gameData.blackUsername(), gameData.gameName(), gameData.game());
        } else {
            if (gameData.blackUsername() != null) {
                throw new DataAccessException(403, "Error: already taken");
            }
            updatedGameData = new GameData(gameID, gameData.whiteUsername(), username, gameData.gameName(), gameData.game());

        }
        gameDataHashMap.put(gameID, updatedGameData);
    }

    public ArrayList<GameData> listGames() {
        return new ArrayList<>(gameDataHashMap.values());
    }

    public void clear() {
        gameDataHashMap.clear();
    }
}
