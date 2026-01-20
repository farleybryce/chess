package chess;

import java.util.ArrayList;

public class SquareValid {
    public static boolean squareValid(int y, int x, ArrayList<ChessMove> legalMoves, ChessBoard board, ChessPiece piece, ChessPosition myPosition) {
        ChessPosition targetPosition = new ChessPosition(y, x);
        ChessPiece targetPiece = board.getPiece(targetPosition);
        // check if target space is occupied by piece of the same color
        if ((targetPiece == null) || (targetPiece.getTeamColor() != piece.getTeamColor())) {
            legalMoves.add(new ChessMove(myPosition, targetPosition, null));
            return true;
        } else { return false; }
    }

    public static boolean checkLineOfSight(int row, int col, int rowVar, int colVar, ArrayList<ChessMove> legalMoves, ChessBoard board, ChessPiece piece, ChessPosition myPosition) {
        ChessPosition targetPosition = new ChessPosition(row, col);
        ChessPiece targetPiece = board.getPiece(targetPosition);
        //check if looking at current position
        if (targetPosition.equals(myPosition)) {
            return checkLineOfSight(row + rowVar, col + colVar, rowVar, colVar, legalMoves, board, piece, myPosition);
        // check if looking out of bounds
        } else if ((row < 1) || (8 < row) || (col < 1) || (8 < col)) {
            return true;
        // check if looking at empty square
        } else if (targetPiece == null) {
            legalMoves.add(new ChessMove(myPosition, targetPosition, null));
            return checkLineOfSight(row + rowVar, col + colVar, rowVar, colVar, legalMoves, board, piece, myPosition);
        // check if looking at square with capturable piece
        } else if (targetPiece.getTeamColor() != piece.getTeamColor()) {
            legalMoves.add(new ChessMove(myPosition, targetPosition, null));
            return true;
        } else { return true; }
    }

}
