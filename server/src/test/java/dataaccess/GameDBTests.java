package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import facade.DataAccessException;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class GameDBTests {
    static DBGameDAO dbGameDAO;
    int gameID;

    @BeforeEach
    public void setUp() throws DataAccessException {
        try {
            dbGameDAO = new DBGameDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        dbGameDAO.clear();
        gameID = dbGameDAO.createGame("myGame");
    }

    @AfterAll
    public static void clearAfterDone() throws DataAccessException {
        dbGameDAO.clear();
    }

    @Test
    public void successfulCreateGameTest() {
        assertNotEquals(0, gameID);
    }

    @Test
    public void successfulGetGameTest() throws DataAccessException {
        GameData gameData = new GameData(1, null, null, "myGame", new ChessGame());
        assertEquals(gameData, dbGameDAO.getGame(1));
    }

    @Test
    public void failedGetGameTest() {
        assertThrows(DataAccessException.class, () -> {
            dbGameDAO.getGame(2);
        });
    }

    @Test
    public void successfulJoinGameTest() throws DataAccessException {
        dbGameDAO.joinGame(1, ChessGame.TeamColor.WHITE, "username");
        assertEquals(new GameData(1, "username", null, "myGame", new ChessGame()),
                dbGameDAO.getGame(1));
    }

    @Test
    public void failedJoinGameTest() throws DataAccessException {
        dbGameDAO.joinGame(1, ChessGame.TeamColor.WHITE, "username");
        assertThrows(DataAccessException.class, () -> {
            dbGameDAO.joinGame(1, ChessGame.TeamColor.WHITE, "username1");
        });
    }

    @Test
    public void successfulListGamesTest() throws DataAccessException {
        dbGameDAO.createGame("myGame1");
        ArrayList<GameData> gameDataArrayList = new ArrayList<>();
        gameDataArrayList.add(new GameData(1, null, null, "myGame", new ChessGame()));
        gameDataArrayList.add(new GameData(2, null, null, "myGame1", new ChessGame()));
        assertEquals(gameDataArrayList, dbGameDAO.listGames());
    }

    @Test
    public void emptyListGamesTest() throws DataAccessException {
        dbGameDAO.clear();
        ArrayList<GameData> gameDataArrayList = new ArrayList<>();
        assertEquals(gameDataArrayList, dbGameDAO.listGames());
    }

    @Test
    public void successfulGameClearTest() throws DataAccessException {
        dbGameDAO.clear();
        assertThrows(DataAccessException.class, () -> {
            dbGameDAO.getGame(1);
        });
    }

}
