package models;

import java.util.List;
import models.Move;

public class GameRecord {

    private long id;
    private String player1;
    private String player2;
    private String winner;
    private String date;
    private int movesCount;
    private List<Move> moves;

    public GameRecord() {}

    public GameRecord(long id,String player1, String player2, String winner,
                      String date, List<Move> moves) {
        this.id = id;
        this.player1 = player1;
        this.player2 = player2;
        this.winner = winner;
        this.date = date;
        this.moves = moves;
        this.movesCount = moves.size();
    }

    public long getId() {
        return id;
    }

    public String getPlayer1() {
        return player1;
    }

    public String getPlayer2() {
        return player2;
    }

    public String getWinner() {
        return winner;
    }

    public String getDate() {
        return date;
    }

    public int getMovesCount() {
        return movesCount;
    }

    public List<Move> getMoves() {
        return moves;
    }
}
