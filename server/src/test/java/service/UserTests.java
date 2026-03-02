package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import server.LoginRequest;
import server.RegisterLoginResult;
import server.RegisterRequest;

public class UserTests {

    private MemoryUserDAO memoryUserDAO;
    private MemoryAuthDAO memoryAuthDAO;
    private UserService userService;

    private RegisterRequest testRegisterRequest;

    @BeforeEach
    public void setUp() {
        memoryUserDAO = new MemoryUserDAO();
        memoryAuthDAO = new MemoryAuthDAO();
        userService = new UserService(memoryUserDAO, memoryAuthDAO);

        testRegisterRequest =
                new RegisterRequest("bryce", "password", "bryce@example.com");
    }

    @Test
    public void successfulRegisterTest() throws DataAccessException {
        RegisterLoginResult registerResult = userService.register(testRegisterRequest);
        assertNotNull(registerResult);
        assertEquals(testRegisterRequest.username(), registerResult.username());
        assertNotNull(registerResult.authToken());
    }

    @Test
    public void failedRegisterTest() throws DataAccessException {
        userService.register(testRegisterRequest);
        assertThrows(DataAccessException.class, () -> {
            userService.register(testRegisterRequest);
        });
    }

    @Test
    public void successfulLoginTest() throws DataAccessException {
        userService.register(testRegisterRequest);
        LoginRequest loginRequest = new LoginRequest("bryce", "password");
        RegisterLoginResult loginResult = userService.login(loginRequest);
        assertNotNull(loginResult);
        assertEquals(loginRequest.username(), loginResult.username());
        assertNotNull(loginResult.authToken());
    }

    @Test
    public void failedLoginTest() throws DataAccessException {
        userService.register(testRegisterRequest);
        LoginRequest loginRequest = new LoginRequest("bryce", "notPassword");
        assertThrows(DataAccessException.class, () -> {
            userService.login(loginRequest);
        });
    }

    @Test
    public void successfulLogout() throws DataAccessException {
        RegisterLoginResult registerResult = userService.register(testRegisterRequest);
        userService.logout(registerResult.authToken());
        assertThrows(DataAccessException.class, () -> {
            memoryAuthDAO.getAuth(registerResult.authToken());
        });
    }

    @Test
    public void failedLogout() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> {
            userService.logout("thisIsAFakeAuthToken");
        });
    }
}
