package server.serverwebsocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
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
import server.Server;
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
    public void handleMessage(WsMessageContext ctx) throws DataAccessException {
        try {
            UserGameCommand userGameCommand = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            switch (userGameCommand.getCommandType()) {
                case LEAVE -> leave(userGameCommand.getAuthToken(), userGameCommand.getGameID(),
                        userGameCommand.getColor(), ctx.session);
                case RESIGN -> resign(userGameCommand.getAuthToken(), userGameCommand.getGameID(), ctx.session);
                case CONNECT -> connect(userGameCommand.getAuthToken(), userGameCommand.getGameID(),
                        userGameCommand.getColor(), ctx.session);
                case MAKE_MOVE -> move(userGameCommand.getAuthToken(), userGameCommand.getGameID(),
                        userGameCommand.getChessMove(), ctx.session);
            }
        } catch (IOException | DataAccessException ex) {
            throw new DataAccessException(500, ex.getMessage());
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private String getUserFromAuth(String authToken) throws DataAccessException {
        AuthData authData = authDAO.getAuth(authToken);
        return authData.username();
    }

    private ChessGame getGameFromID(int gameID) throws DataAccessException {
        GameData gameData = gameDAO.getGame(gameID);
        return gameData.game();
    }

    private String columnToNumber(int num) {
        switch (num) {
            case 1 -> {return "a";}
            case 2 -> {return "b";}
            case 3 -> {return "c";}
            case 4 -> {return "d";}
            case 5 -> {return "e";}
            case 6 -> {return "f";}
            case 7 -> {return "g";}
            case 8 -> {return "h";}
            default -> {return "!";}
        }
    }

    private String decodeMove(ChessMove chessMove) {
        int startRow = chessMove.getStartPosition().getRow();
        int startCol = chessMove.getStartPosition().getColumn();
        int endRow = chessMove.getStartPosition().getRow();
        int endCol = chessMove.getStartPosition().getColumn();
        String startColStr = columnToNumber(startCol);
        String endColStr = columnToNumber(endCol);
        return String.format("%s%d%s%d", startColStr, startRow, endColStr, endRow);
    }

    private void connect(String authToken, int gameID, ChessGame.TeamColor color, Session session) throws DataAccessException, IOException {
        connections.add(gameID, session);
        String username = getUserFromAuth(authToken);
        ChessGame game = getGameFromID(gameID);
        String message;
        if (color == null) {
            message = String.format("%s has connected to the game as an observer", username);
        } else {
            String teamColor = color.toString();
            message = String.format("%s has connected to the game as %s", username, teamColor);
        }
        var connectServerMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message, null);
        var loadServerMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, null, game);
        connections.broadcast(gameID, session, connectServerMessage);
        connections.returnToSender(gameID, session, loadServerMessage);
    }

    private void move(String authToken, int gameID, ChessMove chessMove, Session session) throws DataAccessException, IOException {
        String username = getUserFromAuth(authToken);
        ChessGame game = getGameFromID(gameID);
        if (game.getIsOver()) {
            var errorMessage = "Error: game is over; no new moves can be made";
            var errorServerMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR, errorMessage, null);
            connections.returnToSender(gameID, session, errorServerMessage);
        } else {
            try {
                game.makeMove(chessMove);
            } catch (InvalidMoveException e) {
                var errorMessage = "Error: move is invalid";
                var errorServerMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR, errorMessage, null);
                connections.returnToSender(gameID, session, errorServerMessage);
            }
            gameDAO.updateGame(gameID, game);
            String moveStr = decodeMove(chessMove);
            var message = String.format("%s moved %s", username, moveStr);
            var loadServerMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, null, game);
            var moveServerMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message, null);
            connections.broadcast(gameID, null, loadServerMessage);
            connections.broadcast(gameID, session, moveServerMessage);
        }
    }

    private void resign(String authToken, int gameID, Session session) throws DataAccessException, IOException {
        String username = getUserFromAuth(authToken);
        ChessGame game = getGameFromID(gameID);
        if (game.getIsOver()) {
            var errorMessage = "Error: game is already over";
            var errorServerMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR, errorMessage, null);
            connections.returnToSender(gameID, session, errorServerMessage);
        } else {
            game.setIsOver();
            gameDAO.updateGame(gameID, game);
            var message = String.format("%s has resigned; the game is over", username);
            var resignServerMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message, null);
            connections.broadcast(gameID, null, resignServerMessage);
        }
    }

    private void leave(String authToken, int gameID, ChessGame.TeamColor color, Session session) throws DataAccessException, IOException {
        String username = getUserFromAuth(authToken);
        if (color != null) {
            gameDAO.removePlayer(gameID, color);
        }
        var message = String.format("%s has left the game", username);
        var leaveServerMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message, null);
        connections.broadcast(gameID, session, leaveServerMessage);
        connections.remove(gameID, session);
    }

}
