package client;

import chess.*;
import clientwebsocket.MessageHandler;
import clientwebsocket.WebSocketFacade;
import com.google.gson.*;
import facade.*;
import sharedwebsocket.messages.ServerMessage;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

import static client.DrawBoard.drawBoard;
import static ui.EscapeSequences.*;

public class ChessClient implements MessageHandler {

    private String username = null;
    private String authToken = null;
    private State state = State.LOGGEDOUT;
    private final ServerFacade server;
    private final WebSocketFacade ws;
    private ChessGame.TeamColor teamColor = null;
    private ChessGame game = null;
    private int gameID;

    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        try {
            ws = new WebSocketFacade(serverUrl, this);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void run() {
        System.out.println(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + "♔ Welcome to Chess. Please Sign in. ♕" + RESET_BG_COLOR + RESET_TEXT_COLOR);

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            System.out.print(menu());
            if (state != State.PLAYING  && state != State.OBSERVING) {
                System.out.print(SET_TEXT_COLOR_YELLOW + SET_TEXT_FAINT + SET_TEXT_ITALIC
                        + "Chess >>> "
                        + RESET_TEXT_COLOR + RESET_TEXT_ITALIC + RESET_TEXT_BOLD_FAINT);
            }
            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.println(result + RESET_TEXT_COLOR);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    public void notify(ServerMessage serverMessage) {
        var type = serverMessage.getServerMessageType();
        if (type == ServerMessage.ServerMessageType.LOAD_GAME) {
            game = serverMessage.getGame();
            System.out.print("\n" + redraw());
            System.out.print(SET_TEXT_COLOR_YELLOW + SET_TEXT_FAINT + SET_TEXT_ITALIC
                    + "Chess >>> "
                    + RESET_TEXT_COLOR + RESET_TEXT_ITALIC + RESET_TEXT_BOLD_FAINT);
        } else if (type == ServerMessage.ServerMessageType.NOTIFICATION) {
            System.out.println("\n" + SET_TEXT_COLOR_YELLOW + serverMessage.getMessage() + RESET_TEXT_COLOR);
            System.out.print(SET_TEXT_COLOR_YELLOW + SET_TEXT_FAINT + SET_TEXT_ITALIC
                    + "Chess >>> "
                    + RESET_TEXT_COLOR + RESET_TEXT_ITALIC + RESET_TEXT_BOLD_FAINT);
        } else {
            System.out.println(SET_TEXT_COLOR_RED + serverMessage.getMessage() + RESET_TEXT_COLOR);
            System.out.print(SET_TEXT_COLOR_YELLOW + SET_TEXT_FAINT + SET_TEXT_ITALIC
                    + "Chess >>> "
                    + RESET_TEXT_COLOR + RESET_TEXT_ITALIC + RESET_TEXT_BOLD_FAINT);
        }
    }

    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "clear" -> clear();
                case "register" -> register(params);
                case "login" -> login(params);
                case "logout" -> logout();
                case "create" -> create(params);
                case "list" -> list();
                case "play" -> play(params);
                case "observe" -> observe(params);
                case "redraw" -> redraw();
                case "move" -> move(params);
                case "resign" -> resign();
                case "leave" -> leave();
                case "quit" -> quit();
                default -> help();
            };
        } catch (DataAccessException ex) {
            try {
                JsonObject jsonObject = JsonParser.parseString(ex.getMessage()).getAsJsonObject();
                return SET_TEXT_COLOR_RED + jsonObject.get("message").getAsString();
            } catch (Exception e) {
                return SET_TEXT_COLOR_RED + ex.getMessage();
            }
        }
    }

    private String quit() {
        if (state == State.LOGGEDIN) {
            return help();
        } else if (state == State.LOGGEDOUT) {
            return "quit";
        } else {
            state = State.LOGGEDIN;
            return "";
        }
    }

    private String clear() throws DataAccessException {
        server.clear();
        return SET_TEXT_COLOR_BLUE + "Database Cleared";
    }

    private String register(String... params) throws DataAccessException {
        if (state != State.LOGGEDOUT) {return help();}
        if (params.length >= 3) {
            username = params[0];
            String password = params[1];
            String email = params[2];
            RegisterLoginResult registerResult = server.register(new RegisterRequest(username, password, email));
            authToken = registerResult.authToken();
            state = State.LOGGEDIN;
            return SET_TEXT_COLOR_GREEN + String.format("You signed in as %s.", username);
        }
        throw new DataAccessException(400, "Expected: [username] [password] [email]");
    }

    private String login(String... params) throws DataAccessException {
        if (state != State.LOGGEDOUT) {return help();}
        if (params.length >= 2) {
            username = params[0];
            String password = params[1];
            RegisterLoginResult loginResult = server.login(new LoginRequest(username, password));
            authToken = loginResult.authToken();
            state = State.LOGGEDIN;
            return SET_TEXT_COLOR_GREEN + String.format("You signed in as %s.", username);
        }
        throw new DataAccessException(400, "Expected: [username] [password]");
    }

    private String logout() throws DataAccessException {
        if (state != State.LOGGEDIN) {return help();}
        server.logout(authToken);
        state = State.LOGGEDOUT;
        return SET_TEXT_COLOR_GREEN + "Successfully logged out";
    }

    private String create(String... params) throws DataAccessException {
        if (state != State.LOGGEDIN) {return help();}
        if (params.length >= 1) {
            server.createGame(new CreateRequest(params[0]), authToken);
            return SET_TEXT_COLOR_GREEN + "Game created successfully";
        }
        throw new DataAccessException(400, "Expected: [game name]");
    }

    private String list() throws DataAccessException {
        if (state != State.LOGGEDIN) {return help();}
        ListResult listResult = server.listGames(authToken);
        String gameList = "";
        int count = 0;
        for (ListEntry listEntry : listResult.games()) {
            count += 1;
            String white = listEntry.whiteUsername();
            String black = listEntry.blackUsername();
            gameList += count + ". " + listEntry.gameName()
                    + "\n   White Player: " + ((white != null) ? white : "[available]")
                    + "\n   Black Player: " + ((black != null) ? black : "[available]")
                    + "\n";
        }
        return gameList;
    }

    private void getGameID(int gameNumber) throws DataAccessException {
        ListResult listResult = server.listGames(authToken);
        ArrayList<ListEntry> listEntryArrayList = listResult.games();
        if (gameNumber < 1 || gameNumber > listEntryArrayList.size()) {
            throw new DataAccessException(400, "Error: give a valid game number");
        }
        ListEntry listEntry = listEntryArrayList.get(gameNumber - 1);
        gameID = listEntry.gameID();
    }

    private String play(String... params) throws DataAccessException {
        if (state != State.LOGGEDIN) {return help();}
        if (params.length < 2) {
            throw new DataAccessException(400, "Expected: [game number] [white/black]");
        }
        int gameNumber = 0;
        try {
            gameNumber = Integer.parseInt(params[0]);
        } catch (NumberFormatException ex) {
            throw new DataAccessException(400, "Expected: [game number] [white/black]");
        }
        getGameID(gameNumber);
        if (Objects.equals(params[1], "white")) {
            teamColor = ChessGame.TeamColor.WHITE;
        } else if (Objects.equals(params[1], "black")) {
            teamColor = ChessGame.TeamColor.BLACK;
        } else { throw new DataAccessException(400, "Expected: [game number] [white/black]"); }
        server.joinGame(new JoinRequest(teamColor, gameID), authToken);
        state = State.PLAYING;
        ws.connect(authToken, gameID, teamColor);
        return SET_TEXT_COLOR_GREEN + "Successfully joined game as player";
    }

    private String observe(String ... params) throws DataAccessException {
        if (state != State.LOGGEDIN) {return help();}
        if (params.length < 1) {
            throw new DataAccessException(400, "Expected: [game number]");
        }
        int gameNumber = 0;
        try {
            gameNumber = Integer.parseInt(params[0]);
        } catch (NumberFormatException ex) {
            throw new DataAccessException(400, "Expected: [game number] [white/black]");
        }
        getGameID(gameNumber);
        state = State.OBSERVING;
        teamColor = ChessGame.TeamColor.WHITE;
        ws.connect(authToken, gameID, null);
        return SET_TEXT_COLOR_GREEN + "Successfully joined game as observer";
    };

    private String redraw() {
        if (state != State.PLAYING  && state != State.OBSERVING) {return help();}
        return drawBoard(teamColor, game.getBoard());
    }

    private int columnToNumber(char row) throws DataAccessException {
        switch (row) {
            case 'a' -> {return 1;}
            case 'b' -> {return 2;}
            case 'c' -> {return 3;}
            case 'd' -> {return 4;}
            case 'e' -> {return 5;}
            case 'f' -> {return 6;}
            case 'g' -> {return 7;}
            case 'h' -> {return 8;}
            default -> {throw new DataAccessException(400, "Expected: [move e.g. e2e4]");}
        }
    }

    private ChessMove encodeMove(String moveString) throws DataAccessException {
        char startColChar = moveString.charAt(0);
        char startRowChar = moveString.charAt(1);
        char endColChar = moveString.charAt(2);
        char endRowChar = moveString.charAt(3);
        int startCol = columnToNumber(startColChar);
        int startRow = Character.getNumericValue(startRowChar);
        int endCol = columnToNumber(endColChar);
        int endRow = Character.getNumericValue(endRowChar);
        if (startRow < 0 || startRow > 8 || endRow < 0 || endRow > 8) {
            throw new DataAccessException(400, "Expected: [move e.g. e2e4]");
        }
        return new ChessMove(new ChessPosition(startRow, startCol), new ChessPosition(endRow, endCol), null);
    }

    private  String move(String ... params) throws DataAccessException {
        if (state != State.PLAYING) { return help(); }
        if (params.length < 1) {
            throw new DataAccessException(400, "Expected: [move e.g. e2e4]");
        }
        if (teamColor != game.getTeamTurn()) { return SET_TEXT_COLOR_RED + "Error: It is not your turn"; }
        ChessMove chessMove = encodeMove(params[0]);
        if (game.getBoard().getPiece(chessMove.getStartPosition()) == null) {
            return SET_TEXT_COLOR_RED +"Error: move is invalid";
        }
        if (Objects.equals(game.getBoard().getPiece(chessMove.getStartPosition()).getPieceType(), ChessPiece.PieceType.PAWN) &&
                ((teamColor == ChessGame.TeamColor.WHITE && chessMove.getEndPosition().getRow() == 8) ||
                (teamColor == ChessGame.TeamColor.BLACK && chessMove.getEndPosition().getRow() == 1))) {
            Scanner scanner = new Scanner(System.in);
            System.out.print("""
                    Choose promotion piece by typing one of the following:
                    - queen
                    - rook
                    - knight
                    - bishop
                    (input not matching one of these will default to queen)
                    
                    """);
            System.out.print(SET_TEXT_COLOR_YELLOW + SET_TEXT_FAINT + SET_TEXT_ITALIC
                    + "Chess >>> "
                    + RESET_TEXT_COLOR + RESET_TEXT_ITALIC + RESET_TEXT_BOLD_FAINT);
            String line = scanner.nextLine();
            ChessPiece promotionPiece;
            switch (line) {
                case "rook" -> promotionPiece = new ChessPiece(teamColor, ChessPiece.PieceType.ROOK);
                case "knight" -> promotionPiece = new ChessPiece(teamColor, ChessPiece.PieceType.KNIGHT);
                case "bishop" -> promotionPiece = new ChessPiece(teamColor, ChessPiece.PieceType.BISHOP);
                default -> promotionPiece = new ChessPiece(teamColor, ChessPiece.PieceType.QUEEN);
            }
            chessMove = new ChessMove(chessMove.getStartPosition(), chessMove.getEndPosition(), promotionPiece.getPieceType());
        }
        ws.makeMove(authToken, gameID, chessMove);
        return "";
    }

    private String resign() throws DataAccessException {
        ws.resignGame(authToken, gameID);
        return "";
    }

    private String leave() throws DataAccessException {
        state = State.LOGGEDIN;
        ws.leaveGame(authToken, gameID, teamColor);
        game = null;
        gameID = -1;
        teamColor = null;
        return SET_TEXT_COLOR_GREEN + "Successfully left game";
    }

    private String menu() {
        if (state == State.LOGGEDOUT) {
            return """
                    Choose one of the options below:
                    - help
                    - quit
                    - register [username] [password] [email@example.com]
                    - login [username] [password]
                    """;
        } else if (state == State.LOGGEDIN) {
            return """
                    Choose one of the options below:
                    - help
                    - logout
                    - create [game name]
                    - list
                    - play [game number] [white/black]
                    - observe [game number]
                    """;
        } else if (state == State.PLAYING) {
            return """
                    Choose one of the options below:
                    - help
                    - redraw
                    - move [e.g. e2e4]
                    - resign
                    - leave
                    - quit
                    """;
        } else {
            return """
                    Choose one of the options below:
                    - help
                    - redraw
                    - leave
                    - quit
                    """;
        }
    }

    private String help() {
        if (state == State.LOGGEDOUT) {
            return SET_TEXT_COLOR_BLUE +
                    """
                    Type the word that appears in the list along with
                    any parameters each separated by a single space.
                    - help (shows this help screen)
                    - quit (exits the program)
                    - register [username] [password] [email@example.com]
                      (creates an account with your credentials)
                    - login [username] [password]
                      (allows you to log in to your registered account)
                    """;
        } else if (state == State.LOGGEDIN) {
            return SET_TEXT_COLOR_BLUE +
                    """
                    Type the word that appears in the list along with
                    any parameters each separated by a single space.
                    - help (shows this help screen)
                    - logout (returns you to the login menu)
                    - create [game name] (creates a new chess game)
                    - list (lists all exiting games)
                    - play [game number] [white/black]
                      (adds you to selected game as a player and desired color)
                    - observe [game number] (adds you to selected game as an observer)
                    """;
        } else if (state == State.PLAYING) {
            return SET_TEXT_COLOR_BLUE +
                    """
                    Type the word that appears in the list.
                    - help (shows this help screen)
                    - redraw (redraws the board)
                    - move [e.g. e2e4]
                      (moves your piece, enter the column and row
                      the piece is currently on followed by the
                      column and row you are moving it to.)
                    - resign (concedes the game to your opponent)
                    - leave (exits the current game)
                    - quit (returns you to the previous menu)
                    """;
        } else {
            return SET_TEXT_COLOR_BLUE +
                    """
                    Type the word that appears in the list.
                    - help (shows this help screen)
                    - redraw (redraws the board)
                    - leave (exits the current game)
                    - quit (returns you to the previous menu)
                    """;
        }
    }
}
