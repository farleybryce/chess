package client;

import dataaccess.DataAccessException;
import org.junit.jupiter.api.*;
import server.LoginRequest;
import server.RegisterLoginResult;
import server.RegisterRequest;
import server.Server;
import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    private RegisterLoginResult registerLoginResult;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(String.format("http://localhost:%d", port));
    }

    @BeforeEach
    public void clearDB() throws DataAccessException {
        facade.clear();
        registerLoginResult = facade.register(new RegisterRequest("player1", "password", "p1@email.com"));
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    public void successfulRegisterTest() throws DataAccessException {
        assertTrue(registerLoginResult.authToken().length() > 10);
    }

    @Test
    public void failedRegisterTest() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> {
            facade.register(new RegisterRequest("player1", "password1", "player1@email.com"));
        });
    }

    @Test
    void successfulLoginTest() throws DataAccessException {
        var loginResult = facade.login(new LoginRequest("player1", "password"));
        assertTrue(loginResult.authToken().length() > 10);
    }

    @Test
    void failedLoginTest() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> {
            facade.login(new LoginRequest("player1", "password1"));
        });
        assertThrows(DataAccessException.class, () -> {
            facade.login(new LoginRequest("player2", "password"));
        });
    }

    @Test
    void successfulLogoutTest() throws DataAccessException {
        facade.logout(registerLoginResult.authToken());
        assertThrows(DataAccessException.class, () -> {
            facade.logout(registerLoginResult.authToken());
        });
    }

    @Test
    void failedLogoutTest() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> {
            facade.logout("ThisIsNotTheCorrectAuthToken");
        });
    }

}
