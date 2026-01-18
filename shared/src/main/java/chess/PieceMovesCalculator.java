package chess;

import java.util.Collection;
import java.util.List;

public class PieceMovesCalculator {
    // get a list of possible moves
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        // determine piece type
        switch (piece.getPieceType()) {
            case KING:
                return KingMovesCalculator.pieceMoves(board, myPosition, piece);
            case QUEEN:
                break;
            case BISHOP:
                break;
            case KNIGHT:
                return KnightMovesCalculator.pieceMoves(board, myPosition, piece);
            case ROOK:
                break;
            case PAWN:
                break;
        }
        return List.of();
    }

}
