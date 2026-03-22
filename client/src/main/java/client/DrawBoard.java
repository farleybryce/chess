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
                    + "    a  b  c  d  e  f  g  h    ";
        } else {
            return SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_BLUE
                    + "    h  g  f  e  d  c  b  a    ";
        }
    }

    private static String drawNumberRowSquare(int rowNumber) {
        return SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_BLUE
                + " " + String.valueOf(rowNumber) + " ";
    }

    private static String drawSquare(ChessGame.TeamColor squareColor, ChessGame.TeamColor pieceColor, ChessPiece piece) {
        String setSquareColor = SET_BG_COLOR_LIGHT_GREY;
        String setPieceColor = SET_TEXT_COLOR_BLACK;
        String setPieceLetter;

        if (squareColor == ChessGame.TeamColor.BLACK) { setSquareColor = SET_BG_COLOR_DARK_GREEN; }
        if (pieceColor == ChessGame.TeamColor.WHITE) { setPieceColor = SET_TEXT_COLOR_WHITE; }
        switch (piece.getPieceType()) {
            case KING -> setPieceLetter = "K";
            case QUEEN -> setPieceLetter = "Q";
            case BISHOP -> setPieceLetter = "B";
            case KNIGHT -> setPieceLetter = "N";
            case ROOK -> setPieceLetter = "R";
            case PAWN -> setPieceLetter = "P";
            default -> setPieceLetter = " ";
        }

        return setSquareColor + setPieceColor + setPieceLetter;

    }

    public static ChessGame.TeamColor getSquareColor(int row, int col) {
        ChessGame.TeamColor color = ChessGame.TeamColor.WHITE;
        if ((row + col) % 2 == 0) { color = ChessGame.TeamColor.BLACK; }
        return color;
    }

    public static String drawBoard(ChessGame.TeamColor teamColor, ChessBoard board) {
        String boardString = "";
        boardString += drawLetterRow(teamColor);
        if (teamColor == ChessGame.TeamColor.WHITE) {
            for (int i=8; i>0; i--) {
                boardString += drawNumberRowSquare(i);
                for (int j=1; j<9; j++) {
                    ChessPiece piece = board.getPiece(new ChessPosition(i, j));
                    ChessGame.TeamColor squareColor = getSquareColor(i, j);
                    ChessGame.TeamColor pieceColor;
                    if (piece != null) {
                        pieceColor = piece.getTeamColor();
                    } else {
                        pieceColor = ChessGame.TeamColor.BLACK;
                    }
                    boardString += drawSquare(squareColor, pieceColor, piece);
                }
                boardString += drawNumberRowSquare(i);
            }
        } else {
            for (int i=1; i<9; i++) {
                boardString += drawNumberRowSquare(i);
                for (int j=1; j<9; j++) {
                    ChessPiece piece = board.getPiece(new ChessPosition(i, j));
                    ChessGame.TeamColor squareColor = getSquareColor(i, j);
                    ChessGame.TeamColor pieceColor;
                    if (piece != null) {
                        pieceColor = piece.getTeamColor();
                    } else {
                        pieceColor = ChessGame.TeamColor.BLACK;
                    }
                    boardString += drawSquare(squareColor, pieceColor, piece);
                }
                boardString += drawNumberRowSquare(i);
            }
        }
        boardString += drawLetterRow(teamColor);
        return boardString;
    }
}

