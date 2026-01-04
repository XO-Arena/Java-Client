/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Models;

import Enums.PlayerGender;
import Enums.PlayerState;
import Enums.PlayerType;

/**
 *
 * @author omara
 */
public class Player {

    private String username;
    private PlayerGender gender;
    private PlayerType type; // HUMAN, AI, ONLINE
    private PlayerState state;
    private int score;

    public Player(String username, String password, PlayerGender gender) { }

    public String getUsername() { 
        return username;
    }
    public void setUsername(String username) { 
        this.username = username;
    }
    
    public PlayerGender getGender() {
        return gender;
    }
    public void setGender(PlayerGender gender) {
        this.gender = gender;
    }
    public PlayerState getState() { 
        return state;
    }
    public void setState(PlayerState state) {
        this.state = state;
    }
    public int getScore() {
        return score;
    }
    public void updateScore(int score) { 
        this.score += score;
    }
}

