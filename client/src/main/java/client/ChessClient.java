package client;

import ui.EscapeSequences.*;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class ChessClient {

    private String username = null;
    private final ServerFacade server;

    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

    public void run() {
        System.out.println(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + "♔ Welcome to Chess. Please Sign in. ♕" + RESET_BG_COLOR + RESET_BG_COLOR);
        System.out.print(loginHelp());

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

    private String loginHelp() {
        return """
                Choose one of the options below:
                - help
                - quit
                - register [username] [password] [email@example.com]
                - login [username] [password]
                """;
    }
}
