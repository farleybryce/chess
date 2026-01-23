package chess.move_calculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class QueenMovesCalculator {
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        // list of all the legal moves the Queen can make
        ArrayList<ChessMove> legalMoves = new ArrayList<>();
        // get row and column coordinates of Queen
        int myRow = myPosition.getRow();
        int myCol = myPosition.getColumn();

        for (int i = -1; i < 2; i += 2) {
            // orthogonal movement
            SquareValid.checkLineOfSight(myRow, myCol, i, 0, legalMoves, board, myPosition, piece);
            SquareValid.checkLineOfSight(myRow, myCol, 0, i, legalMoves, board, myPosition, piece);
            // diagonal movement
            for (int j = -1; j < 2; j += 2) {
                SquareValid.checkLineOfSight(myRow, myCol, i, j, legalMoves, board, myPosition, piece);
            }
        }

        return legalMoves;
    }
}
