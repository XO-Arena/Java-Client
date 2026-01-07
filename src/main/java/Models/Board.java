package Models;

import Enums.GameResult;
import Enums.PlayerSymbol;

public class Board {

    private PlayerSymbol[][] cells;
    private String winCode;

    public Board() {
        cells = new PlayerSymbol[3][3];
    }

    public void setCell(int row, int col, PlayerSymbol player) {
        cells[row][col] = player;
    }

    public PlayerSymbol getCell(int row, int col) {
        return cells[row][col];
    }

    public PlayerSymbol[][] getCells() {
        return cells;
    }

    public boolean isEmpty(int row, int col) {
        return cells[row][col] == null;
    }

    public boolean isFull() {

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (cells[i][j] == null) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public GameResult getBoardResult() {
        for (int i = 0; i < 3; i++) {
            if (getCell(i, 0) != null
                    && getCell(i, 0) == getCell(i, 1)
                    && getCell(i, 1) == getCell(i, 2)) {
                winCode = i + "0" + i + "1" + i + "2";
                return getCell(i, 0) == PlayerSymbol.X ? GameResult.X_WIN : GameResult.O_WIN;
            }
        }

        for (int i = 0; i < 3; i++) {
            if (getCell(0, i) != null
                    && getCell(0, i) == getCell(1, i)
                    && getCell(1, i) == getCell(2, i)) {
                winCode = "0" + i + "1" + i + "2" + i;
                return getCell(0, i) == PlayerSymbol.X ? GameResult.X_WIN : GameResult.O_WIN;
            }
        }

        if (getCell(1, 1) != null) {

            if (getCell(0, 0) == getCell(1, 1)
                    && getCell(1, 1) == getCell(2, 2)) {
                winCode = "001122";
                return getCell(1, 1) == PlayerSymbol.X ? GameResult.X_WIN : GameResult.O_WIN;
            }

            if (getCell(0, 2) == getCell(1, 1)
                    && getCell(1, 1) == getCell(2, 0)) {
                winCode = "021120";
                return getCell(1, 1) == PlayerSymbol.X ? GameResult.X_WIN : GameResult.O_WIN;
            }
        }
        
        winCode = null;
        if (isFull()) {
            return GameResult.DRAW;
        }

        return GameResult.NONE;
    }
    
    public String getWinCode() {
        if (winCode == null) getBoardResult();
        return winCode;
    }

    public void reset() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                cells[i][j] = null;
            }
        }
        winCode = null;
    }

}
