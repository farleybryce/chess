package service;

import chess.ChessGame;
import dataaccess.*;
import model.GameData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import facade.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class GameTests {

    private MemoryAuthDAO memoryAuthDAO;
    private MemoryUserDAO memoryUserDAO;
    private MemoryGameDAO memoryGameDAO;

    private UserService userService;
    private GameService gameService;

    private RegisterLoginResult registerResult;
    private String validAuthToken;

    @BeforeEach
    public void setUp() throws DataAccessException {
        // Fresh DAOs for every test
        memoryAuthDAO = new MemoryAuthDAO();
        memoryUserDAO = new MemoryUserDAO();
        memoryGameDAO = new MemoryGameDAO();

        // Fresh services for every test
        userService = new UserService(memoryUserDAO, memoryAuthDAO);
        gameService = new GameService(memoryGameDAO, memoryAuthDAO);

        // Register a default user
        RegisterRequest registerRequest =
                new RegisterRequest("bryce", "password", "bryce@example.com");

        registerResult = userService.register(registerRequest);
        validAuthToken = registerResult.authToken();
    }


    @Test
    public void successfulCreateGameTest() throws DataAccessException {
        CreateRequest createRequest = new CreateRequest("myGame");
        CreateResult createResult =  gameService.createGame(validAuthToken, createRequest);
        assertNotNull(createResult);
        assertEquals(1, createResult.gameID());
    }

    @Test
    public void failedCreateGameTest() throws DataAccessException {
        CreateRequest createRequest = new CreateRequest("myGame");
        assertThrows(DataAccessException.class, () -> {
            gameService.createGame("thisIsNotAVaildAuthToken", createRequest);
        });
    }

    @Test
    public void successfulJoinGameTest() throws DataAccessException {
        CreateRequest createRequest = new CreateRequest("myGame");
        CreateResult createResult =  gameService.createGame(validAuthToken, createRequest);
        JoinRequest joinRequest = new JoinRequest(ChessGame.TeamColor.WHITE, createResult.gameID());
        gameService.joinGame(validAuthToken, joinRequest);
        assertEquals(registerResult.username(), memoryGameDAO.getGame(createResult.gameID()).whiteUsername());
    }

    @Test
    public void failedJoinGameTest() throws DataAccessException {
        CreateRequest createRequest = new CreateRequest("myGame");
        CreateResult createResult =  gameService.createGame(validAuthToken, createRequest);
        JoinRequest joinRequest = new JoinRequest(ChessGame.TeamColor.WHITE, createResult.gameID());
        gameService.joinGame(validAuthToken, joinRequest);
        assertThrows(DataAccessException.class, () -> {
            gameService.joinGame(validAuthToken, joinRequest);
        });
    }

    @Test
    public void successfulListGamesTest() throws DataAccessException {

        CreateRequest createRequest1 = new CreateRequest("myGame1");
        CreateRequest createRequest2 = new CreateRequest("myGame2");
        CreateResult createResult1 = gameService.createGame(validAuthToken, createRequest1);
        CreateResult createResult2 = gameService.createGame(validAuthToken, createRequest2);
        ListResult listResult = gameService.listGames(validAuthToken);
        ArrayList<ListEntry> gameDataCollection = new ArrayList<>();
        gameDataCollection.add(new ListEntry(createResult1.gameID(), null, null, "myGame1"));
        gameDataCollection.add(new ListEntry(createResult2.gameID(), null, null, "myGame2"));
        assertEquals(gameDataCollection, listResult.games());
    }

    @Test
    public void failedListGamesTest() throws DataAccessException {
        CreateRequest createRequest1 = new CreateRequest("myGame1");
        CreateRequest createRequest2 = new CreateRequest("myGame2");
        gameService.createGame(validAuthToken, createRequest1);
        gameService.createGame(validAuthToken, createRequest2);
        assertThrows(DataAccessException.class, () -> {
            gameService.listGames("thisIsNotAVaildAuthToken");
        });

    }
}
