package client;

import dataaccess.DataAccessException;
import ui.EscapeSequences.*;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class ChessClient {

    private String username = null;
    private State state = State.LOGGEDOUT;
    private final ServerFacade server;

    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

    public void run() {
        System.out.println(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + "♔ Welcome to Chess. Please Sign in. ♕" + RESET_BG_COLOR + RESET_BG_COLOR);
        System.out.print(menu());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            System.out.print(SET_TEXT_COLOR_GREEN + SET_TEXT_FAINT + SET_TEXT_ITALIC
                    + "Chess Login >>> "
                    + RESET_TEXT_COLOR + RESET_TEXT_ITALIC + RESET_TEXT_BOLD_FAINT);
            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.print(SET_TEXT_COLOR_BLUE + result + RESET_TEXT_COLOR);
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
                case "quit" -> "quit";
                default -> help();
            };
        } catch (DataAccessException ex) {
            return ex.getMessage();
        }
    }

    public void clear() throws DataAccessException {
        server.clear();
    }

    public String register(String... params) throws DataAccessException {
        if (params.length >= 3) {
            state = State.LOGGEDIN;
            username = params[0];
            return String.format("You signed in as %s.", username);
        }
        throw new DataAccessException(400, "Expected: [username] [password] [email]");
    }

    public String login(String... params) throws DataAccessException {
        if (params.length >= 2) {
            state = State.LOGGEDIN;
            username = String.join("-", params);
            return String.format("You signed in as %s.", username);
        }
        throw new DataAccessException(400, "Expected: [username] [password]");
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
                    - play [game number]
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
            return """
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
            return """
                    Type the word that appears in the list along with
                    any parameters each separated by a single space.
                    - help (shows this help screen)
                    - logout (returns you to the login menu)
                    - create [game name] (creates a new chess game)
                    - list (lists all exiting games)
                    - play [game number] (adds you to selected game as a player)
                    - observe [game number] (adds you to selected game as an observer)
                    """;
        } else if (state == State.PLAYING) {
            return """
                    Type the word that appears in the list.
                    - help (shows this help screen)
                    - quit (returns you to the previous menu)
                    """;
        } else {
            return """
                    Type the word that appears in the list.
                    - help (shows this help screen)
                    - quit (returns you to the previous menu)
                    """;
        }
    }
}
