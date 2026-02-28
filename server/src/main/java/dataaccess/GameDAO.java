package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;

public interface GameDAO {
    int createGame(String gameName) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    void joinGame(int gameID, ChessGame.TeamColor playerColor, String username) throws DataAccessException;

    ArrayList<GameData> listGames() throws DataAccessException;
}
