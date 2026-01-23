package chess.move_calculators;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator {
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        // list of all the legal moves the Pawn can make
        ArrayList<ChessMove> legalMoves = new ArrayList<>();
        // list of all the legal target squares the pawn can go to
        ArrayList<ChessPosition> legalTargets = new ArrayList<>();
        // get row and column coordinates of Pawn
        int myRow = myPosition.getRow();
        int myCol = myPosition.getColumn();
        //determine whether the pawn is advancing up or down the board (movement direction)
        int movDir = 1;
        if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
            movDir = -1;
        }
        //check if forward movement is blocked
        boolean forwardMovBlocked = true;
        ChessPosition oneRowForwardPosition = new ChessPosition(myRow + movDir, myCol);
        ChessPiece oneRowForwardPiece = board.getPiece(oneRowForwardPosition);
        if (oneRowForwardPiece == null) {
            legalTargets.add(oneRowForwardPosition);
            forwardMovBlocked = false;
        }
        //check if pawn can advance two spaces
        if ((!forwardMovBlocked) && ((movDir == 1 && myRow == 2) || (movDir == -1 && myRow == 7))) {
            ChessPosition twoRowForwardPosition = new ChessPosition(myRow + (2 * movDir), myCol);
            ChessPiece twoRowForwardPiece = board.getPiece(twoRowForwardPosition);
            if (twoRowForwardPiece == null) {
                legalTargets.add(twoRowForwardPosition);
            }
        }
        //check if diagonal capture is inbounds and then valid
        if (myCol != 1) {
            ChessPosition targetCapturePositionLeft = new ChessPosition(myRow + movDir, myCol - 1);
            ChessPiece targetCapturePieceLeft = board.getPiece(targetCapturePositionLeft);
            if ((targetCapturePieceLeft != null) && (targetCapturePieceLeft.getTeamColor() != piece.getTeamColor())) {
                legalTargets.add(targetCapturePositionLeft);
            }
        }
        if (myCol != 8) {
            ChessPosition targetCapturePositionRight = new ChessPosition(myRow + movDir, myCol + 1);
            ChessPiece targetCapturePieceRight = board.getPiece(targetCapturePositionRight);
            if ((targetCapturePieceRight != null) && (targetCapturePieceRight.getTeamColor() != piece.getTeamColor())) {
                legalTargets.add(targetCapturePositionRight);
            }
        }

        //handle promotion
        for (ChessPosition target : legalTargets) {
            if ((target.getRow() == 1) || (target.getRow() == 8)) {
                legalMoves.add(new ChessMove(myPosition, target, ChessPiece.PieceType.KNIGHT));
                legalMoves.add(new ChessMove(myPosition, target, ChessPiece.PieceType.BISHOP));
                legalMoves.add(new ChessMove(myPosition, target, ChessPiece.PieceType.ROOK));
                legalMoves.add(new ChessMove(myPosition, target, ChessPiece.PieceType.QUEEN));
            } else {
                legalMoves.add(new ChessMove(myPosition, target, null));
            }
        }

        return legalMoves;
    }
}
