package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import server.LoginRequest;
import server.RegisterLoginResult;
import server.RegisterRequest;

public class UserTests {
    @Test
    public void successfulRegisterTest() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("bryce", "password", "bryce@example.com");
        UserService userService = new UserService(new MemoryUserDAO(), new MemoryAuthDAO());
        RegisterLoginResult registerResult = userService.register(registerRequest);
        assertNotNull(registerResult);
        assertEquals(registerRequest.username(), registerResult.username());
        assertNotNull(registerResult.authToken());
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

    @Test
    public void successfulLoginTest() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("bryce", "password", "bryce@example.com");
        UserService userService = new UserService(new MemoryUserDAO(), new MemoryAuthDAO());
        userService.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("bryce", "password");
        RegisterLoginResult loginResult = userService.login(loginRequest);
        assertNotNull(loginResult);
        assertEquals(loginRequest.username(), loginResult.username());
        assertNotNull(loginResult.authToken());
    }

    @Test
    public void failedLoginTest() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("bryce", "password", "bryce@example.com");
        UserService userService = new UserService(new MemoryUserDAO(), new MemoryAuthDAO());
        userService.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("bryce", "notPassword");
        assertThrows(DataAccessException.class, () -> {
            userService.login(loginRequest);
        });
    }

    @Test
    public void successfulLogout() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("bryce", "password", "bryce@example.com");
        MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
        UserService userService = new UserService(new MemoryUserDAO(), memoryAuthDAO);
        RegisterLoginResult registerResult = userService.register(registerRequest);
        userService.logout(registerResult.authToken());
        assertThrows(DataAccessException.class, () -> {
            memoryAuthDAO.getAuth(registerResult.authToken());
        });
    }

    @Test
    public void failedLogout() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("bryce", "password", "bryce@example.com");
        MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
        UserService userService = new UserService(new MemoryUserDAO(), memoryAuthDAO);
        userService.register(registerRequest);
        assertThrows(DataAccessException.class, () -> {
            userService.logout("thisIsAFakeAuthToken");
        });
    }
}
