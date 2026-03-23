package client;

import chess.ChessBoard;
import chess.ChessGame;
import com.google.gson.*;
import dataaccess.DataAccessException;
import server.*;
import ui.EscapeSequences.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

import static client.DrawBoard.drawBoard;
import static ui.EscapeSequences.*;

public class ChessClient {

    private String username = null;
    private String authToken = null;
    private State state = State.LOGGEDOUT;
    private final ServerFacade server;

    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

    public void run() {
        System.out.println(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + "♔ Welcome to Chess. Please Sign in. ♕" + RESET_BG_COLOR + RESET_TEXT_COLOR);

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            System.out.print(menu());
            System.out.print(SET_TEXT_COLOR_YELLOW + SET_TEXT_FAINT + SET_TEXT_ITALIC
                    + "Chess >>> "
                    + RESET_TEXT_COLOR + RESET_TEXT_ITALIC + RESET_TEXT_BOLD_FAINT);
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

    private int getGameID(int gameNumber) throws DataAccessException {
        ListResult listResult = server.listGames(authToken);
        ArrayList<ListEntry> listEntryArrayList = listResult.games();
        if (gameNumber < 1 || gameNumber > listEntryArrayList.size()) {
            throw new DataAccessException(400, "Error: give a valid game number");
        }
        ListEntry listEntry = listEntryArrayList.get(gameNumber - 1);
        return listEntry.gameID();
    }

    private String play(String... params) throws DataAccessException {
        if (state != State.LOGGEDIN) {return help();}
        if (params.length < 2) {
            throw new DataAccessException(400, "Expected: [game number] [white/black]");
        }
        ChessGame.TeamColor color = null;
        int gameNumber = 0;
        try {
            gameNumber = Integer.parseInt(params[0]);
        } catch (NumberFormatException ex) {
            throw new DataAccessException(400, "Expected: [game number] [white/black]");
        }
        int gameID = getGameID(gameNumber);
        if (Objects.equals(params[1], "white")) {
            color = ChessGame.TeamColor.WHITE;
        } else if (Objects.equals(params[1], "black")) {
            color = ChessGame.TeamColor.BLACK;
        } else { throw new DataAccessException(400, "Expected: [game number] [white/black]"); }
        server.joinGame(new JoinRequest(color, gameID), authToken);
        state = State.PLAYING;
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        return drawBoard(color, board);
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
        int gameID = getGameID(gameNumber);
        state = State.OBSERVING;
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        return drawBoard(ChessGame.TeamColor.WHITE, board);
    };

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
                    - quit
                    """;
        } else {
            return """
                    Choose one of the options below:
                    - help
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
                    - quit (returns you to the previous menu)
                    """;
        } else {
            return SET_TEXT_COLOR_BLUE +
                    """
                    Type the word that appears in the list.
                    - help (shows this help screen)
                    - quit (returns you to the previous menu)
                    """;
        }
    }
}
