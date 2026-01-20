package chess;

import java.util.ArrayList;

public class SquareValid {
    public static boolean squareValid (int y, int x, ArrayList<ChessMove> legalMoves, ChessBoard board, ChessPiece piece, ChessPosition myPosition) {
        ChessPosition targetPosition = new ChessPosition(y, x);
        ChessPiece targetPiece = board.getPiece(targetPosition);
        // check if target space is occupied by piece of the same color
        if ((targetPiece == null) || (targetPiece.getTeamColor() != piece.getTeamColor())) {
            legalMoves.add(new ChessMove(myPosition, targetPosition, null));
            return true;
        } else { return false; }
    }

}
