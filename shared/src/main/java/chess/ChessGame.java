package chess;

import java.util.*;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor currentTeamColor;
    private ChessBoard currentBoard;
    public ChessGame() {
        this.currentTeamColor = TeamColor.WHITE;
        this.currentBoard = new ChessBoard();
        currentBoard.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTeamColor;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currentTeamColor = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = currentBoard.getPiece(startPosition);
        HashSet<ChessMove> possibleMoves = new HashSet<>(piece.pieceMoves(currentBoard, startPosition));
        HashSet<ChessMove> validMovesSet = new HashSet<>();
        for (ChessMove move : possibleMoves) {
            HypotheticalGame possibleBoard = new HypotheticalGame();
            possibleBoard.deepCopyBoard(currentBoard);
            HypotheticalGame.movePiece(move, possibleBoard.getBoard());
            if (!possibleBoard.isInCheck(piece.getTeamColor())) {
                validMovesSet.add(move);
            }
        }
        return validMovesSet;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = currentBoard.getPiece(move.getStartPosition());
        if ((piece == null) || (piece.getTeamColor() != currentTeamColor)) {
            throw new InvalidMoveException();
        }
        Collection<ChessMove> validMovesCollection = validMoves(move.getStartPosition());
        if (validMovesCollection.contains(move)) {
            HypotheticalGame.movePiece(move, currentBoard);
            if (currentTeamColor == TeamColor.WHITE) {
                currentTeamColor = TeamColor.BLACK;
            } else {
                currentTeamColor = TeamColor.WHITE;
            }
        } else {
            throw new InvalidMoveException();
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        HypotheticalGame testGame = new HypotheticalGame();
        testGame.deepCopyBoard(currentBoard);
        return testGame.isInCheck(teamColor);
    }

    private boolean validMoveDoesNotExist(TeamColor teamColor) {
        HashSet<ChessMove> validMovesSet = new HashSet<>();
        for (int i=1; i<9; i++) {
            for (int j=1; j<9; j++) {
                ChessPosition position = new ChessPosition(i,j);
                ChessPiece piece = currentBoard.getPiece(position);
                if ((piece != null) && (piece.getTeamColor() == teamColor)) {
                    validMovesSet.addAll(validMoves(position));
                }
                if (!validMovesSet.isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (this.isInCheck(teamColor)) {
            return validMoveDoesNotExist(teamColor);
        }
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (!this.isInCheck(teamColor)) {
            return validMoveDoesNotExist(teamColor);
        }
        return false;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.currentBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.currentBoard;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return currentTeamColor == chessGame.currentTeamColor && Objects.equals(currentBoard, chessGame.currentBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentTeamColor, currentBoard);
    }
}
