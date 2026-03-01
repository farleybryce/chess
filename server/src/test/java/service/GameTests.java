package service;

import chess.ChessGame;
import dataaccess.*;
import model.GameData;
import org.junit.jupiter.api.Test;
import server.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class GameTests {

    @Test
    public void successfulCreateGameTest() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("bryce", "password", "bryce@example.com");
        MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
        UserService userService = new UserService(new MemoryUserDAO(), memoryAuthDAO);
        RegisterLoginResult registerResult = userService.register(registerRequest);
        CreateRequest createRequest = new CreateRequest("myGame");
        GameService gameService = new GameService(new MemoryGameDAO(), memoryAuthDAO);
        CreateResult createResult =  gameService.createGame(registerResult.authToken(), createRequest);
        assertNotNull(createResult);
        assertEquals(1, createResult.gameID());
    }

    @Test
    public void failedCreateGameTest() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("bryce", "password", "bryce@example.com");
        MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
        UserService userService = new UserService(new MemoryUserDAO(), memoryAuthDAO);
        userService.register(registerRequest);
        CreateRequest createRequest = new CreateRequest("myGame");
        GameService gameService = new GameService(new MemoryGameDAO(), memoryAuthDAO);
        assertThrows(DataAccessException.class, () -> {
            gameService.createGame("thisIsNotAVaildAuthToken", createRequest);
        });
    }

    @Test
    public void successfulJoinGameTest() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("bryce", "password", "bryce@example.com");
        MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
        UserService userService = new UserService(new MemoryUserDAO(), memoryAuthDAO);
        RegisterLoginResult registerResult = userService.register(registerRequest);
        CreateRequest createRequest = new CreateRequest("myGame");
        MemoryGameDAO memoryGameDAO = new MemoryGameDAO();
        GameService gameService = new GameService(memoryGameDAO, memoryAuthDAO);
        CreateResult createResult =  gameService.createGame(registerResult.authToken(), createRequest);
        JoinRequest joinRequest = new JoinRequest(ChessGame.TeamColor.WHITE, createResult.gameID());
        gameService.joinGame(registerResult.authToken(), joinRequest);
        assertEquals(registerResult.username(), memoryGameDAO.getGame(createResult.gameID()).whiteUsername());
    }

    @Test
    public void failedJoinGameTest() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("bryce", "password", "bryce@example.com");
        MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
        UserService userService = new UserService(new MemoryUserDAO(), memoryAuthDAO);
        RegisterLoginResult registerResult = userService.register(registerRequest);
        CreateRequest createRequest = new CreateRequest("myGame");
        MemoryGameDAO memoryGameDAO = new MemoryGameDAO();
        GameService gameService = new GameService(memoryGameDAO, memoryAuthDAO);
        CreateResult createResult =  gameService.createGame(registerResult.authToken(), createRequest);
        JoinRequest joinRequest = new JoinRequest(ChessGame.TeamColor.WHITE, createResult.gameID());
        gameService.joinGame(registerResult.authToken(), joinRequest);
        assertThrows(DataAccessException.class, () -> {
            gameService.joinGame(registerResult.authToken(), joinRequest);
        });
    }

    @Test
    public void successfulListGamesTest() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("bryce", "password", "bryce@example.com");
        MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
        UserService userService = new UserService(new MemoryUserDAO(), memoryAuthDAO);
        RegisterLoginResult registerResult = userService.register(registerRequest);
        CreateRequest createRequest1 = new CreateRequest("myGame1");
        CreateRequest createRequest2 = new CreateRequest("myGame2");
        MemoryGameDAO memoryGameDAO = new MemoryGameDAO();
        GameService gameService = new GameService(memoryGameDAO, memoryAuthDAO);
        CreateResult createResult1 = gameService.createGame(registerResult.authToken(), createRequest1);
        CreateResult createResult2 = gameService.createGame(registerResult.authToken(), createRequest2);
        ListResult listResult = gameService.listGames(registerResult.authToken());
        ArrayList<ListEntry> gameDataCollection = new ArrayList<>();
        gameDataCollection.add(new ListEntry(createResult1.gameID(), null, null, "myGame1"));
        gameDataCollection.add(new ListEntry(createResult2.gameID(), null, null, "myGame2"));

        assertEquals(gameDataCollection, listResult.games());
    }

    @Test
    public void failedListGamesTest() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("bryce", "password", "bryce@example.com");
        MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
        UserService userService = new UserService(new MemoryUserDAO(), memoryAuthDAO);
        RegisterLoginResult registerResult = userService.register(registerRequest);
        CreateRequest createRequest1 = new CreateRequest("myGame1");
        CreateRequest createRequest2 = new CreateRequest("myGame2");
        MemoryGameDAO memoryGameDAO = new MemoryGameDAO();
        GameService gameService = new GameService(memoryGameDAO, memoryAuthDAO);
        gameService.createGame(registerResult.authToken(), createRequest1);
        gameService.createGame(registerResult.authToken(), createRequest2);
        assertThrows(DataAccessException.class, () -> {
            gameService.listGames("thisIsNotAVaildAuthToken");
        });

    }
}
