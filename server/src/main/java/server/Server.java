package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import io.javalin.*;
import io.javalin.http.Context;
import service.ClearService;
import service.UserService;


public class Server {
    private final UserService userService;
//    private final GameService gameService;
//    private final ClearService clearService;
    private final Javalin javalin;

    public Server() {
        this(new UserService(new MemoryUserDAO(), new MemoryAuthDAO()));
    }

    public Server(UserService userService) {
        this.userService = userService;

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
        // Register your endpoints and exception handlers here.
        .post("/user", this::register)
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
        ctx.result(ex.getMessage());
    }

    public void register(Context ctx) throws DataAccessException {
        RegisterRequest registerRequest = new Gson().fromJson(ctx.body(), RegisterRequest.class);
        RegisterLoginResult registerLoginResult = userService.register(registerRequest);
        ctx.result(new Gson().toJson(registerLoginResult));
    }
}
