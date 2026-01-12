package dto;

import enums.PlayerSymbol;

public class MoveDTO {
    private String sessionId;
    private int row;
    private int col;
    private PlayerSymbol symbol;

    public MoveDTO() {
    }

    public MoveDTO(String sessionId, int row, int col, PlayerSymbol symbol) {
        this.sessionId = sessionId;
        this.row = row;
        this.col = col;
        this.symbol = symbol;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public PlayerSymbol getSymbol() {
        return symbol;
    }

    public void setSymbol(PlayerSymbol symbol) {
        this.symbol = symbol;
    }
}
