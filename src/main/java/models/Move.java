package models;

import enums.PlayerSymbol;

public class Move {
    
    public PlayerSymbol playerSymbol;
    public final int row;
    public final int col;

    public Move(int row, int col) {
        this.row = row;
        this.col = col;
        this.playerSymbol = null;
    }
     public Move(PlayerSymbol playerSymbol, int row, int col) {
        this.playerSymbol = playerSymbol;
        this.row = row;
        this.col = col;
    }

    public PlayerSymbol getPlayer() {
        return playerSymbol;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
}
