package models;

public class LocalMoveProvider implements MoveProvider {

    @Override
    public Move getNextMove() {
        return new Move(2, 2);
    }
}
