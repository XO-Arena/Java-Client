package Models;

import Enums.PlayerSymbol;
import Enums.PlayerType;
import Enums.UserGender;

public class Player extends User {

    private PlayerType type;
    private PlayerSymbol symbole;

    public Player(String username, UserGender gender, int score, PlayerType type, PlayerSymbol symbole) {
        super(username, gender, score);
        this.type = type;
        this.symbole = symbole;
    }

    public PlayerType getType() {
        return this.type;
    }

}
