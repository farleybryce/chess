package server;

import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.*;
import io.javalin.http.Context;
import service.ClearService;
import service.GameService;
import service.UserService;

import java.util.Map;


public class Server {
    private final UserService userService;
    private final GameService gameService;
//    private final ClearService clearService;
    private final Javalin javalin;

    public Server() {
        MemoryUserDAO memoryUserDAO = new MemoryUserDAO();
        MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
        MemoryGameDAO memoryGameDAO = new MemoryGameDAO();

        this.userService = new UserService(memoryUserDAO, memoryAuthDAO);
        this.gameService = new GameService(memoryGameDAO, memoryAuthDAO);

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
        // Register your endpoints and exception handlers here.
        .post("/user", this::register)
        .post("/session", this::login)
        .delete("/session", this::logout)
        .post("/game", this::createGame)
        .exception(DataAccessException.class, this::exceptionHandler);
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
        // this was working better before the map
        ctx.json(Map.of("message", ex.getMessage()));
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
}
