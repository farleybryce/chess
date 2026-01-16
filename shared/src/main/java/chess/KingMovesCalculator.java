package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KingMovesCalculator {
    // calculate possible moves for king piece
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        // list of all the legal moves the King can make
        ArrayList<ChessMove> legalMoves = new ArrayList<>();
        // get row and column coordinates of king
        int myRow = myPosition.getRow();
        int myCol = myPosition.getColumn();

        for (int i = myRow-1; i < myRow+2; i++) {
            for (int j = myCol-1; j < myCol+2; j++) {
                // check if target move is inbounds
                if (i < 1 || 8 < i) { continue; }
                if (j < 1 || 8 < j) { continue; }
                // don't move to current spot
                if ((i == myRow) && (j == myCol)) {
                    continue;
                }
                ChessPosition targetPosition = new ChessPosition(i,j);
                ChessPiece targetPiece = board.getPiece(targetPosition);
                // check if target space is occupied by piece of the same color
                if ((targetPiece == null) || (targetPiece.getTeamColor() != piece.getTeamColor())) {
                    legalMoves.add(new ChessMove(myPosition, targetPosition, null));
                }
            }
        }

        return legalMoves;
    }
}
