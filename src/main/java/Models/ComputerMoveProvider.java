package Models;

import Enums.Difficulty;

public class ComputerMoveProvider implements MoveProvider {

    private final Board board;
    private Board aiBoard;
    private int row, col;
    private Difficulty difficulty;

    public ComputerMoveProvider(Board board, Difficulty difficulty) {
        this.board = board;
        this.difficulty = difficulty;
    }
        
    private Move moveByMiniMax() {
        // TODO: implement AI logic

        do {
            row = (int) (Math.random() * 3);
            col = (int) (Math.random() * 3);
            
        } while (!board.isEmpty(row, col));

        System.out.println(difficulty.name + " Played");
        return new Move(row, col);

    }

    @Override
    public Move getNextMove() {

        return moveByMiniMax();
    }
}
