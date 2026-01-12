/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dto;

import enums.PlayerSymbol;
import enums.PlayerType;
import enums.UserGender;
import enums.UserState;

/**
 *
 * @author mohannad
 */
public class PlayerDTO extends UserDTO {
    private PlayerSymbol symbol;
    private PlayerType type;

    public PlayerDTO(String username, UserGender gender, int score, UserState state, PlayerType type, PlayerSymbol symbol) {
        super(username, gender, score, state);
        this.type = type;
        this.symbol = symbol;
    }
    
    public PlayerDTO() {}
    
    public static PlayerDTO fromUser(models.User user, PlayerSymbol symbol) {
        PlayerType type = null;
        if (user instanceof models.Player) {
            type = ((models.Player) user).getType();
        }
        return new PlayerDTO(
                user.getUsername(),
                user.getGender(),
                user.getScore(),
                user.getState(),
                type,
                symbol
        );
    }
    
    public PlayerSymbol getSymbol() {
        return symbol;
    }

    public void setSymbol(PlayerSymbol symbol) {
        this.symbol = symbol;
    }

    public PlayerType getType() {
        return type;
    }

    public void setType(PlayerType type) {
        this.type = type;
    }
}
