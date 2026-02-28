package dataaccess;

import chess.ChessGame;
import model.GameData;

public interface GameDAO {
    int createGame(String gameName) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    void joinGame(int gameID, ChessGame.TeamColor playerColor, String username) throws DataAccessException;
}
