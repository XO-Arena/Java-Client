package Models;

public class Board {

    private int[][] cells;

    public Board() {
        cells = new int[3][3];
    }

    public void setCell(int row, int col, int player) {

        cells[row][col] = player;
    }

    public int getCell(int row, int col) {

        return cells[row][col];
    }

    public int[][] getCells() {

        return cells;
    }

    public boolean isEmpty(int row, int col) {

        return cells[row][col] == 0;
    }

    public boolean isFull() {

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (cells[i][j] == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public void reset() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                cells[i][j] = 0;
            }
        }
    }

}
