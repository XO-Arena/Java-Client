package Models;

import Enums.UserGender;
import Enums.UserState;

public class User {

    private String username;
    private UserGender gender;
    private int score;
    private UserState state;

    public User(String username, UserGender gender, int score) {
        this.username = username;
        this.gender = gender;
        this.score = score;
    }

    public String getUserName() {
        return username;
    }

    public void setUserName(String username) {
        this.username = username;
    }

    public UserGender getGender() {
        return gender;
    }

    public void setGender(UserGender gender) {
        this.gender = gender;
    }

    public UserState getState() {
        return state;
    }

    public void setState(UserState state) {
        this.state = state;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void updateScore(int points) {
        score = (score + points < 0) ? 0 : (score + points);
    }
}
