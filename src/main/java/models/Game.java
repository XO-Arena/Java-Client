package models;

import enums.GameResult;
import enums.PlayerSymbol;

public class Game {

    private Board board;
    private PlayerSymbol currentPlayer;
    private boolean hasEnded;

    public Game() {
        board = new Board();
        currentPlayer = PlayerSymbol.X;
        hasEnded = false;
    }

    public Board getBoard() {
        return board;
    }

    public PlayerSymbol getCurrentPlayer() {
        return currentPlayer;
    }

    public boolean playMove(int row, int col) {

        if (!board.isEmpty(row, col)) {
            return false;
        }

        board.setCell(row, col, currentPlayer);
        return true;
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
