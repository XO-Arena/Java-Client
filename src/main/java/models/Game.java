package models;

import enums.GameResult;
import enums.PlayerSymbol;
import java.util.ArrayList;
import java.util.List;

public class Game {

    private Board board;
    private PlayerSymbol currentPlayer;
    private boolean hasEnded;
    private List<Move> moves;
    public Game() {
        board = new Board();
        currentPlayer = PlayerSymbol.X;
        hasEnded = false;
        moves = new ArrayList<>();
    }

    public Board getBoard() {
        return board;
    }

    public PlayerSymbol getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(PlayerSymbol currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public boolean playMove(int row, int col) {

        if (!board.isEmpty(row, col)) {
            return false;
        }

        board.setCell(row, col, currentPlayer);
        moves.add(new Move(currentPlayer,row,col));
        return true;
    }
    
    public List<Move> getMoves() {
        return moves;
    }
    
    public void switchPlayer() {
        currentPlayer = (currentPlayer == PlayerSymbol.X) ? PlayerSymbol.O : PlayerSymbol.X;
    }

    public GameResult checkResult() {
        return board.getBoardResult();
    }

    public boolean hasEnded() {
        return hasEnded;
    }

    public void setHasEnded(boolean hasEnded) {
        this.hasEnded = hasEnded;
    }
    
    public void reset() {
        board.reset();
        currentPlayer = PlayerSymbol.X;
        hasEnded = false;
    }
}
