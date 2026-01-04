package Models;

import Enums.GameResult;

public class Game {

    private Board board;
    private int currentPlayer;
    private boolean isGameEnded;

    public Game() {
        board = new Board();
        currentPlayer = 1;
        isGameEnded = false;
    }

    public Board getBoard() {
        return board;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public boolean isGameEnded() {
        return isGameEnded;
    }

    public boolean playMove(int row, int col) {

        if (isGameEnded) {
            return false;
        }

        if (!board.isEmpty(row, col)) {
            return false;
        }

        board.setCell(row, col, currentPlayer);

        currentPlayer = (currentPlayer == 1) ? 2 : 1;

        return true;
    }

    //Game Logic
    public GameResult checkResult() {
        // check Rows
        for (int i = 0; i < 3; i++) {
            if (board.getCell(i, 0) != 0
                    && board.getCell(i, 0) == board.getCell(i, 1)
                    && board.getCell(i, 1) == board.getCell(i, 2)) {
                isGameEnded = true;
                return board.getCell(i, 0) == 1 ? GameResult.X_WIN : GameResult.O_WIN;
            }
        }

        // Check Columns
        for (int i = 0; i < 3; i++) {
            if (board.getCell(0, i) != 0
                    && board.getCell(0, i) == board.getCell(1, i)
                    && board.getCell(1, i) == board.getCell(2, i)) {
                isGameEnded = true;
                return board.getCell(0, i) == 1 ? GameResult.X_WIN : GameResult.O_WIN;
            }
        }

        // Check Sambosk
        if (board.getCell(1, 1) != 0) {

            if (board.getCell(0, 0) == board.getCell(1, 1)
                    && board.getCell(1, 1) == board.getCell(2, 2)) {
                isGameEnded = true;
                return board.getCell(1, 1) == 1 ? GameResult.X_WIN : GameResult.O_WIN;
            }

            if (board.getCell(0, 2) == board.getCell(1, 1)
                    && board.getCell(1, 1) == board.getCell(2, 0)) {
                isGameEnded = true;
                return board.getCell(1, 1) == 1 ? GameResult.X_WIN : GameResult.O_WIN;
            }
        }

        // Draw
        if (board.isFull()) {
            isGameEnded = true;
            return GameResult.DRAW;
        }

        return GameResult.NONE;
    }

    public void reset() {
        board.reset();
        currentPlayer = 1;
        isGameEnded = false;
    }

}
