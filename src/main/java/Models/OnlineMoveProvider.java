package Models;

public class OnlineMoveProvider implements MoveProvider {

    @Override
    public Move getNextMove() {
        /*
        for example
        
        int row = NetworkManager.receiveRow();
        int col = NetworkManager.receiveCol();
        
         */

        int row = 3, col = 2;
        return new Move(row, col);
    }

}
