package enums;


public enum PlayerSymbol {
    X,
    O;
    
    public PlayerSymbol getOpponent() {
        return this == X ? O : X;
    }
}
