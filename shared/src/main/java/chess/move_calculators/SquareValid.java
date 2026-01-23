package chess.move_calculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;

public class SquareValid {
    public static boolean squareValid(int y, int x, ArrayList<ChessMove> legalMoves, ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        ChessPosition targetPosition = new ChessPosition(y, x);
        ChessPiece targetPiece = board.getPiece(targetPosition);
        // check if target space is occupied by piece of the same color
        if ((targetPiece == null) || (targetPiece.getTeamColor() != piece.getTeamColor())) {
            legalMoves.add(new ChessMove(myPosition, targetPosition, null));
            return true;
        } else { return false; }
    }

    public static boolean checkLineOfSight(int targetRow, int targetCol, int rowVar, int colVar, ArrayList<ChessMove> legalMoves, ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        // check if looking out of bounds
        if ((targetRow < 1) || (8 < targetRow) || (targetCol < 1) || (8 < targetCol)) {
            return true;
        }
        ChessPosition targetPosition = new ChessPosition(targetRow, targetCol);
        ChessPiece targetPiece = board.getPiece(targetPosition);
        //check if looking at current position
        if (targetPosition.equals(myPosition)) {
            return checkLineOfSight(targetRow + rowVar, targetCol + colVar, rowVar, colVar, legalMoves, board, myPosition, piece);
        // check if looking at empty square
        } else if (targetPiece == null) {
            legalMoves.add(new ChessMove(myPosition, targetPosition, null));
            return checkLineOfSight(targetRow + rowVar, targetCol + colVar, rowVar, colVar, legalMoves, board, myPosition, piece);
        // check if looking at square with capturable piece
        } else if (targetPiece.getTeamColor() != piece.getTeamColor()) {
            legalMoves.add(new ChessMove(myPosition, targetPosition, null));
            return true;
        } else { return true; }
    }

}
