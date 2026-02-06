package chess;

import java.util.HashSet;
import java.util.Objects;

public class HypotheticalGame {

    private ChessBoard board;
    public HypotheticalGame() {
        this.board = new ChessBoard();
    }

    public void deepCopyBoard(ChessBoard boardToCopy) {
        for (int i=1; i<9; i++) {
            for (int j=1; j<9; j++) {
                ChessPosition positionToCopy = new ChessPosition(i, j);
                ChessPiece pieceToCopy = boardToCopy.getPiece(positionToCopy);
                if (pieceToCopy != null) {
                    board.addPiece(positionToCopy, pieceToCopy);
                }
            }
        }
    }

    public ChessBoard getBoard() {
        return this.board;
    }

    public static void movePiece(ChessMove move, ChessBoard board) {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        board.addPiece(move.getStartPosition(), null);
        if (move.getPromotionPiece() != null) {
            board.addPiece(move.getEndPosition(), new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()));
        } else {
            board.addPiece(move.getEndPosition(), piece);
        }
    }

    public boolean isInCheck(ChessGame.TeamColor teamColor) {
        HashSet<ChessMove> oppMoves = new HashSet<>();
        ChessPosition kingPosition = new ChessPosition(1,1);
        // iterate over all spaces to find the king and the other team's pieces
        for (int i=1; i<9; i++) {
            for (int j=1; j<9; j++) {
                ChessPosition position = new ChessPosition(i,j);
                ChessPiece piece = board.getPiece(position);
                if (piece != null) {
                    if (piece.getTeamColor() != teamColor) {
                        // add the moves of the current piece to a master list
                        oppMoves.addAll(piece.pieceMoves(board, position));
                    } else if (piece.getPieceType() == ChessPiece.PieceType.KING) {
                        kingPosition = position;
                    }
                }
            }
        }
        HashSet<ChessPosition> oppTargetPositions = new HashSet<>();
        for (ChessMove move : oppMoves) {
            oppTargetPositions.add(move.getEndPosition());
        }
        return oppTargetPositions.contains(kingPosition);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HypotheticalGame that = (HypotheticalGame) o;
        return Objects.equals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(board);
    }
}
