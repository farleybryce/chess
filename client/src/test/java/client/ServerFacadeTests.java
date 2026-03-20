package client;

import dataaccess.DataAccessException;
import org.junit.jupiter.api.*;
import server.RegisterRequest;
import server.Server;
import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

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
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    public void successfulRegisterTest() throws DataAccessException {
        var registerResult = facade.register(new RegisterRequest("player1", "password", "p1@email.com"));
        assertTrue(registerResult.authToken().length() > 10);
    }

}
