package server;

import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.*;
import io.javalin.http.Context;
import server.serverwebsocket.WebSocketHandler;
import service.ClearService;
import service.GameService;
import service.UserService;
import facade.*;
import java.util.Map;


public class Server {
    private final UserService userService;
    private final GameService gameService;
    private final ClearService clearService;
    private final Javalin javalin;
    private final WebSocketHandler webSocketHandler;

    public Server() {
        DBUserDAO dbUserDAO = null;
        try {
            dbUserDAO = new DBUserDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        DBAuthDAO dbAuthDAO = null;
        try {
            dbAuthDAO = new DBAuthDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        DBGameDAO dbGameDAO = null;
        try {
            dbGameDAO = new DBGameDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        this.userService = new UserService(dbUserDAO, dbAuthDAO);
        this.gameService = new GameService(dbGameDAO, dbAuthDAO);
        this.clearService = new ClearService(dbUserDAO, dbGameDAO, dbAuthDAO);

        this.webSocketHandler = new WebSocketHandler(dbAuthDAO, dbGameDAO);

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
        // Register your endpoints and exception handlers here.
        .post("/user", this::register)
        .post("/session", this::login)
        .delete("/session", this::logout)
        .post("/game", this::createGame)
        .put("/game", this::joinGame)
        .get("/game", this::listGames)
        .delete("/db", this::clear)
        .exception(DataAccessException.class, this::exceptionHandler)
        .ws("/ws", ws -> {
            ws.onConnect(webSocketHandler);
            ws.onMessage(webSocketHandler);
            ws.onClose(webSocketHandler);
        });
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void exceptionHandler(DataAccessException ex, Context ctx) {
        ctx.status(ex.getStatusCode());
        ctx.result(new Gson().toJson(Map.of("message", ex.getMessage())));
    }

    public void register(Context ctx) throws DataAccessException {
        RegisterRequest registerRequest = new Gson().fromJson(ctx.body(), RegisterRequest.class);
        RegisterLoginResult registerResult = userService.register(registerRequest);
        ctx.result(new Gson().toJson(registerResult));
    }

    public void login(Context ctx) throws DataAccessException {
        LoginRequest loginRequest = new Gson().fromJson(ctx.body(), LoginRequest.class);
        RegisterLoginResult loginResult = userService.login(loginRequest);
        ctx.result(new Gson().toJson(loginResult));
    }

    public void logout(Context ctx) throws DataAccessException {
        userService.logout(ctx.header("authorization"));
        ctx.result("{}");
    }

    public void createGame(Context ctx) throws DataAccessException {
        CreateRequest createRequest = new Gson().fromJson(ctx.body(), CreateRequest.class);
        CreateResult createResult = gameService.createGame(ctx.header("authorization"), createRequest);
        ctx.result(new Gson().toJson(createResult));
    }

    public void joinGame(Context ctx) throws DataAccessException {
        JoinRequest joinRequest = new Gson().fromJson(ctx.body(), JoinRequest.class);
        gameService.joinGame(ctx.header("authorization"), joinRequest);
        ctx.result("{}");
    }

    public void listGames(Context ctx) throws DataAccessException {
        ListResult listResult = gameService.listGames(ctx.header("authorization"));
        ctx.result(new Gson().toJson(listResult));
    }

    public void clear(Context ctx) throws DataAccessException {
        clearService.clear();
        ctx.result("{}");
    }
}
