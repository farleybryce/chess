package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import org.junit.jupiter.api.Test;
import server.*;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ClearTest {

    @Test
    public void clearTest() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("bryce", "password", "bryce@example.com");
        MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
        MemoryUserDAO memoryUserDAO = new MemoryUserDAO();
        MemoryGameDAO memoryGameDAO = new MemoryGameDAO();
        UserService userService = new UserService(memoryUserDAO, memoryAuthDAO);
        RegisterLoginResult registerResult = userService.register(registerRequest);
        CreateRequest createRequest = new CreateRequest("myGame");
        GameService gameService = new GameService(memoryGameDAO, memoryAuthDAO);
        CreateResult createResult =  gameService.createGame(registerResult.authToken(), createRequest);
        ClearService clearService = new ClearService(memoryUserDAO, memoryGameDAO, memoryAuthDAO);
        clearService.clear();
        assertThrows(DataAccessException.class, () -> {
            userService.login(new LoginRequest(registerRequest.username(), registerRequest.password()));
        });
        assertThrows(DataAccessException.class, () -> {
            memoryGameDAO.getGame(createResult.gameID());
        });
        assertThrows(DataAccessException.class, () -> {
            memoryAuthDAO.getAuth(registerResult.authToken());
        });

    }
}
