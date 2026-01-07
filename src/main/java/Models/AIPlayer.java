/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Models;

import Enums.Difficulty;
import Enums.PlayerSymbol;
import Enums.PlayerType;
import Enums.UserGender;

/**
 *
 * @author mohan
 */
public class AIPlayer extends Player {
    
    private Difficulty difficulty;
    
    public AIPlayer(Difficulty difficulty, PlayerSymbol symbol) {
        super(difficulty.name, UserGender.Male, difficulty.score, PlayerType.COMPUTER, symbol);
        this.difficulty = difficulty;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }
}
