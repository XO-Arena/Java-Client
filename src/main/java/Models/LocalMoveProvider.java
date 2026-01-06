package Models;

public class LocalMoveProvider implements MoveProvider {

    private int row;
    private int col;

    public LocalMoveProvider(int row, int col) {
        this.row = row;
        this.col = col;
    }

    @Override
    public Move getNextMove() {
        return new Move(row, col);
    }

}
