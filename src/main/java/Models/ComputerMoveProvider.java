package Models;

public class ComputerMoveProvider implements MoveProvider {

    private final Board board;
    private int row, col;

    public ComputerMoveProvider(Board board) {
        this.board = board;
    }
        
    private Move moveByMiniMax() {
        // TODO: implement AI logic

        do {
            row = (int) (Math.random() * 3);
            col = (int) (Math.random() * 3);
            
        } while (!board.isEmpty(row, col));

        return new Move(row, col);

    }

    @Override
    public Move getNextMove() {

        return moveByMiniMax();
    }
}
