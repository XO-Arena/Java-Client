package com.mycompany.java.client.project;

/**
 * Model class representing a recorded game
 * 
 * @author ANTER
 */
public class GameRecord {
    
    private int id;
    private String player1;
    private String player2;
    private String winner;
    private String date;
    private int moves;
    private String gameData; // Store game moves/state as JSON or serialized string
    
    /**
     * Default constructor
     */
    public GameRecord() {
    }
    
    /**
     * Constructor with all fields
     */
    public GameRecord(int id, String player1, String player2, String winner, 
                     String date, int moves, String gameData) {
        this.id = id;
        this.player1 = player1;
        this.player2 = player2;
        this.winner = winner;
        this.date = date;
        this.moves = moves;
        this.gameData = gameData;
    }
    
    /**
     * Constructor without ID (for new records)
     */
    public GameRecord(String player1, String player2, String winner, 
                     String date, int moves, String gameData) {
        this.player1 = player1;
        this.player2 = player2;
        this.winner = winner;
        this.date = date;
        this.moves = moves;
        this.gameData = gameData;
    }

    // Getters and Setters
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlayer1() {
        return player1;
    }

    public void setPlayer1(String player1) {
        this.player1 = player1;
    }

    public String getPlayer2() {
        return player2;
    }

    public void setPlayer2(String player2) {
        this.player2 = player2;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getMoves() {
        return moves;
    }

    public void setMoves(int moves) {
        this.moves = moves;
    }

    public String getGameData() {
        return gameData;
    }

    public void setGameData(String gameData) {
        this.gameData = gameData;
    }

    @Override
    public String toString() {
        return "GameRecord{" +
                "id=" + id +
                ", player1='" + player1 + '\'' +
                ", player2='" + player2 + '\'' +
                ", winner='" + winner + '\'' +
                ", date='" + date + '\'' +
                ", moves=" + moves +
                '}';
    }
}