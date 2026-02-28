package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import server.RegisterLoginResult;
import server.RegisterRequest;

public class RegisterTests {
    @Test
    public void successfulRegisterTest() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("bryce", "password", "bryce@example.com");
        UserService userService = new UserService(new MemoryUserDAO(), new MemoryAuthDAO());
        RegisterLoginResult registerResult = userService.register(registerRequest);
        assertNotNull(registerResult);
        assertEquals(registerRequest.username(), registerResult.username());
    }

    @Test
    public void failedRegisterTest() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("bryce", "password", "bryce@example.com");
        UserService userService = new UserService(new MemoryUserDAO(), new MemoryAuthDAO());
        RegisterLoginResult registerResult1 = userService.register(registerRequest);
        assertThrows(DataAccessException.class, () -> {
            userService.register(registerRequest);
        });
    }
}
