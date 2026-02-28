package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {

    private int nextID = 0;
    final private HashMap<Integer, GameData> gameDataHashMap = new HashMap<>();

    public int createGame(String gameName) throws DataAccessException {
        int gameID = ++nextID;
        gameDataHashMap.put(gameID, new GameData(gameID, "","", gameName, new ChessGame()));
        return gameID;
    }
}
