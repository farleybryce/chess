package client;

import chess.ChessGame;
import server.DataAccessException;
import org.junit.jupiter.api.*;
import server.*;

import java.util.ArrayList;

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

    @Test
    void successfulCreateGameTest() throws DataAccessException {
        var createResult = facade.createGame(new CreateRequest("MyGame"), registerLoginResult.authToken());
        assertEquals(1, createResult.gameID());
    }

    @Test
    void failedCreateGameTest() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> {
            facade.createGame(new CreateRequest("MyGame"), "ThisIsNotTheCorrectAuthToken");
        });
    }

    @Test
    void successfulJoinGameTest() throws DataAccessException {
        var createResult = facade.createGame(new CreateRequest("MyGame"), registerLoginResult.authToken());
        assertDoesNotThrow(() -> {
            facade.joinGame(new JoinRequest(ChessGame.TeamColor.WHITE, createResult.gameID()), registerLoginResult.authToken());
        });
    }

    @Test
    void failedJoinGameTest() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> {
            facade.joinGame(new JoinRequest(ChessGame.TeamColor.WHITE, 1), registerLoginResult.authToken());
        });
        var createResult = facade.createGame(new CreateRequest("MyGame"), registerLoginResult.authToken());
        facade.joinGame(new JoinRequest(ChessGame.TeamColor.WHITE, createResult.gameID()), registerLoginResult.authToken());
        assertThrows(DataAccessException.class, () -> {
            facade.joinGame(new JoinRequest(ChessGame.TeamColor.WHITE, createResult.gameID()), registerLoginResult.authToken());
        });
    }

    @Test
    void successfulListGamesTest() throws DataAccessException {
        var createResult1 = facade.createGame(new CreateRequest("MyGame1"), registerLoginResult.authToken());
        var createResult2 = facade.createGame(new CreateRequest("MyGame2"), registerLoginResult.authToken());
        var listResult = facade.listGames(registerLoginResult.authToken());
        ListEntry listEntry1 = new ListEntry(createResult1.gameID(), null, null, "MyGame1");
        ListEntry listEntry2 = new ListEntry(createResult2.gameID(), null, null, "MyGame2");
        ArrayList<ListEntry> listEntryArrayList = new ArrayList<>();
        listEntryArrayList.add(listEntry1);
        listEntryArrayList.add(listEntry2);
        ListResult listResultTest = new ListResult(listEntryArrayList);
        assertEquals(listResultTest, listResult);
    }

    @Test
    void failedListGamesTest() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> {
            facade.listGames("ThisIsNotTheCorrectAuthToken");
        });
    }

    @Test
    void successfulClearTest() throws DataAccessException {
        facade.clear();
        assertThrows(DataAccessException.class, () -> {
            facade.createGame(new CreateRequest("MyGame"), registerLoginResult.authToken());
        });
        assertThrows(DataAccessException.class, () -> {
            facade.login(new LoginRequest("player1", "password"));
        });
    }

}
