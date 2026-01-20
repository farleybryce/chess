package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator {
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        // list of all the legal moves the Pawn can make
        ArrayList<ChessMove> legalMoves = new ArrayList<>();
        // get row and column coordinates of Pawn
        int myRow = myPosition.getRow();
        int myCol = myPosition.getColumn();



        return legalMoves;
    }
}
