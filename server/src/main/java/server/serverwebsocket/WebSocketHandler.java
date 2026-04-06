package server.serverwebsocket;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import facade.DataAccessException;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import sharedwebsocket.commands.UserGameCommand;
import sharedwebsocket.messages.ServerMessage;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public WebSocketHandler(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        try {
            UserGameCommand userGameCommand = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            switch (userGameCommand.getCommandType()) {
                case ENTER -> enter(action.visitorName(), ctx.session);
                case EXIT -> exit(action.visitorName(), ctx.session);
                case LEAVE -> leave();
                case RESIGN -> resign();
                case CONNECT -> connect(userGameCommand.getAuthToken(), userGameCommand.getGameID(),
                        userGameCommand.getColor(), ctx.session);
                case MAKE_MOVE -> move();
            }
        } catch (IOException | DataAccessException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void connect(String authToken, int gameID, ChessGame.TeamColor color, Session session) throws DataAccessException, IOException {
        connections.add(gameID, session);
        AuthData authData = authDAO.getAuth(authToken);
        String username = authData.username();
        var message = String.format("%s has connected to the game as %s", username, color);
        var connectServerMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        var loadServerMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, null);
        connections.broadcast(gameID, session, connectServerMessage);
        connections.returnToSender(gameID, session, loadServerMessage);
    }


    private void exit(String visitorName, Session session) throws IOException {
        var message = String.format("%s left the shop", visitorName);
        var notification = new Notification(Notification.Type.DEPARTURE, message);
        connections.broadcast(session, notification);
        connections.remove(session);
    }

    public void makeNoise(String petName, String sound) throws ResponseException {
        try {
            var message = String.format("%s says %s", petName, sound);
            var notification = new Notification(Notification.Type.NOISE, message);
            connections.broadcast(null, notification);
        } catch (Exception ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }
}
