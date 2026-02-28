package service;

import dataaccess.*;
import org.junit.jupiter.api.Test;
import server.CreateRequest;
import server.CreateResult;
import server.RegisterLoginResult;
import server.RegisterRequest;

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
}
