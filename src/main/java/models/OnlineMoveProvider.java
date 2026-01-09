package Models;

public class OnlineMoveProvider implements MoveProvider {

    @Override
    public Move getNextMove() {
        /*
        for example
        
        int row = NetworkManager.receiveRow();
        int col = NetworkManager.receiveCol();
        
         */

        return new Move(2, 2);
    }

}
