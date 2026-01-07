/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Enums;

import Models.Player;

/**
 *
 * @author mohan
 */
public enum Difficulty {
    EASY("Easy", 50),
    MEDIUM("Medium", 200),
    HARD("Hard", 500),
    IMPOSSIBLE("Impossible", 1000);
    
    public String name;
    public int score;
    
    Difficulty(String name, int score) {
        this.name = name;
        this.score = score;
    }
}
