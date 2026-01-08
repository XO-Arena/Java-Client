package Models;

import enums.PlayerSymbol;
import enums.PlayerType;
import enums.UserGender;

public class Player extends User {

    private PlayerType type;
    private PlayerSymbol symbol;

    public Player(String username, UserGender gender, int score, PlayerType type, PlayerSymbol symbol) {
        super(username, gender, score);
        this.type = type;
        this.symbol = symbol;
    }

    public PlayerType getType() {
        return this.type;
    }

    public PlayerSymbol getSymbol() {
        return symbol;
    }

    public void setSymbol(PlayerSymbol symbol) {
        this.symbol = symbol;
    }
}
