package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import static ui.EscapeSequences.*;


public class DrawBoard {

    private static String drawLetterRow(ChessGame.TeamColor color) {
        if (color == ChessGame.TeamColor.WHITE) {
            return SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_BLUE
                    + "    a  b  c  d  e  f  g  h    " + RESET_BG_COLOR;
        } else {
            return SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_BLUE
                    + "    h  g  f  e  d  c  b  a    " + RESET_BG_COLOR;
        }
    }

    private static String drawNumberRowSquare(int rowNumber) {
        return SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_BLUE
                + " " + String.valueOf(rowNumber) + " ";
    }

    private static String drawSquare(ChessGame.TeamColor squareColor, ChessGame.TeamColor pieceColor, ChessPiece piece) {
        String setSquareColor = SET_BG_COLOR_TAN;
        String setPieceColor = SET_TEXT_COLOR_BLACK;
        String setPieceLetter;

        if (squareColor == ChessGame.TeamColor.BLACK) { setSquareColor = SET_BG_COLOR_DARK_GREEN; }
        if (pieceColor == ChessGame.TeamColor.WHITE) { setPieceColor = SET_TEXT_COLOR_WHITE; }
        if (piece == null) {
            setPieceLetter = " ";
        } else {
            switch (piece.getPieceType()) {
                case KING -> setPieceLetter = "K";
                case QUEEN -> setPieceLetter = "Q";
                case BISHOP -> setPieceLetter = "B";
                case KNIGHT -> setPieceLetter = "N";
                case ROOK -> setPieceLetter = "R";
                case PAWN -> setPieceLetter = "P";
                default -> setPieceLetter = " ";
            }
        }

        return setSquareColor + setPieceColor + " " + SET_TEXT_BOLD + setPieceLetter + " " + RESET_TEXT_BOLD_FAINT;

    }

    private static ChessGame.TeamColor getSquareColor(int row, int col, ChessGame.TeamColor teamColor) {
        ChessGame.TeamColor color = ChessGame.TeamColor.WHITE;
        if ((row + col) % 2 == 0) { color = ChessGame.TeamColor.BLACK; }
        return color;
    }
    private static String drawPieceSquares(int i, int j, ChessBoard board, ChessGame.TeamColor teamColor) {
        String pieceSquaresString = "";

        ChessPiece piece = board.getPiece(new ChessPosition(i, j));
        ChessGame.TeamColor squareColor = getSquareColor(i, j, teamColor);
        ChessGame.TeamColor pieceColor;
        if (piece != null) {
            pieceColor = piece.getTeamColor();
        } else {
            pieceColor = ChessGame.TeamColor.BLACK;
        }
        pieceSquaresString += drawSquare(squareColor, pieceColor, piece);

        return pieceSquaresString;
    }

    public static String drawBoard(ChessGame.TeamColor teamColor, ChessBoard board) {
        String boardString = "";
        boardString += drawLetterRow(teamColor) + "\n";
        if (teamColor == ChessGame.TeamColor.WHITE) {
            for (int i=8; i>0; i--) {
                boardString += drawNumberRowSquare(i) + RESET_BG_COLOR;
                for (int j = 1; j < 9; j++) {
                    boardString += drawPieceSquares(i, j, board, teamColor);
                }
                boardString += drawNumberRowSquare(i) + RESET_BG_COLOR + "\n";
            }
        } else {
            for (int i=1; i<9; i++) {
                boardString += drawNumberRowSquare(i) + RESET_BG_COLOR;
                for (int j = 8; j > 0; j--) {
                    boardString += drawPieceSquares(i, j, board, teamColor);
                }
                boardString += drawNumberRowSquare(i) + RESET_BG_COLOR + "\n";
            }
        }
        boardString += drawLetterRow(teamColor) + "\n";
        return boardString ;
    }
}

