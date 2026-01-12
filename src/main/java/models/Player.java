package models;

import dto.PlayerDTO;
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

    public static Player fromPlayerDto(PlayerDTO playerDTO) {
        return new Player(
                playerDTO.getUsername(),
                playerDTO.getGender(),
                playerDTO.getScore(),
                PlayerType.ONLINE,
                playerDTO.getSymbol()
        );
    }

    public PlayerType getType() {
        return this.type;
    }
    
    public void setType(PlayerType type) {
        this.type = type;
    }

    public PlayerSymbol getSymbol() {
        return symbol;
    }

    public void setSymbol(PlayerSymbol symbol) {
        this.symbol = symbol;
    }
}
