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

    public PlayerDTO(String username, UserGender gender, int score, UserState state, PlayerType type, PlayerSymbol symbol) {
        super(username, gender, score, state);
        this.symbol = symbol;
    }
    
    public PlayerDTO() {}
    
    public PlayerSymbol getSymbol() {
        return symbol;
    }

    public void setSymbol(PlayerSymbol symbol) {
        this.symbol = symbol;
    }
}
