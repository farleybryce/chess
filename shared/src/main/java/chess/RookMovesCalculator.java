package chess;

import java.util.ArrayList;
import java.util.Collection;

public class RookMovesCalculator {
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        // list of all the legal moves the King can make
        ArrayList<ChessMove> legalMoves = new ArrayList<>();
        // get row and column coordinates of king
        int myRow = myPosition.getRow();
        int myCol = myPosition.getColumn();

        for (int i = -1; i < 2; i+=2) {
            SquareValid.checkLineOfSight(myRow, myCol, i, 0, legalMoves, board, myPosition, piece);
            SquareValid.checkLineOfSight(myRow, myCol, 0, i, legalMoves, board, myPosition, piece);
        }

        return legalMoves;
    }
}
