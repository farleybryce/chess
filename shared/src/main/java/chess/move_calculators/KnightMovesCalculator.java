package chess.move_calculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMovesCalculator {
    // calculate possible moves for king piece
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        // list of all the legal moves the Knight can make
        ArrayList<ChessMove> legalMoves = new ArrayList<>();
        // get row and column coordinates of Knight
        int myRow = myPosition.getRow();
        int myCol = myPosition.getColumn();


        for (int i = myRow-2; i < myRow+3; i++) {
            // determine the column offset/displacement based on the row offset from current position
            int rowDisplacement = Math.abs(i - myRow);
            int colDisplacement;
            if (rowDisplacement == 2) {
                colDisplacement = 1;
            } else if (rowDisplacement == 1) {
                colDisplacement = 2;
            } else {
                colDisplacement = 0;
            }
            // check if target move is inbounds
            if (i < 1 || 8 < i) { continue; }
            // don't move to current spot
            if (colDisplacement == 0) { continue; }
            // iterate over the two possible column coordinates in target row
            for (int j = -1; j < 2; j+=2) {
                // find column coordinates
                int colCoordinate = myCol + (j * colDisplacement);
                // check if target move is inbounds
                if (colCoordinate < 1 || 8 < colCoordinate) { continue; }
                SquareValid.squareValid(i, colCoordinate, legalMoves, board, myPosition, piece);
            }

        }

        return legalMoves;
    }
}
