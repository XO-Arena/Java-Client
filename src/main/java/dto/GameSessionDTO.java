/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dto;

import enums.GameResult;
import enums.PlayerSymbol;
import enums.SessionStatus;
import models.GameSession;
import models.Player;

/**
 *
 * @author mohannad
 */
public class GameSessionDTO {
    private String sessionId;
    private PlayerDTO player1;
    private PlayerDTO player2;
    private PlayerSymbol currentTurn;
    private BoardDTO board;
    private GameResult result;
    private SessionStatus status;
    private int player1Wins;
    private int player2Wins;
    private int draws;

    public GameSessionDTO() {
    }

    public GameSessionDTO(String sessionId, PlayerDTO player1, PlayerDTO player2, PlayerSymbol currentTurn, BoardDTO board, GameResult result, SessionStatus status, int player1Wins, int player2Wins, int draws) {
        this.sessionId = sessionId;
        this.player1 = player1;
        this.player2 = player2;
        this.currentTurn = currentTurn;
        this.board = board;
        this.result = result;
        this.status = status;
        this.player1Wins = player1Wins;
        this.player2Wins = player2Wins;
        this.draws = draws;
    }
    
    public static GameSessionDTO fromModel(GameSession gameSession) {
        return new GameSessionDTO(
                gameSession.getSessionId(),
                PlayerDTO.fromUser(gameSession.getPlayer1(), gameSession.getPlayer1().getSymbol()),
                PlayerDTO.fromUser(gameSession.getPlayer2(), gameSession.getPlayer2().getSymbol()),
                gameSession.getCurrentPlayer().getSymbol(),
                BoardDTO.fromModel(gameSession.getGame().getBoard()),
                gameSession.getLastResult(),
                gameSession.isGameEnded() ? SessionStatus.FINISHED : SessionStatus.IN_PROGRESS,
                gameSession.getPlayer1Wins(),
                gameSession.getPlayer2Wins(),
                gameSession.getDrawCount()
        );
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public PlayerDTO getPlayer1() {
        return player1;
    }

    public void setPlayer1(PlayerDTO player1) {
        this.player1 = player1;
    }

    public PlayerDTO getPlayer2() {
        return player2;
    }

    public void setPlayer2(PlayerDTO player2) {
        this.player2 = player2;
    }

    public PlayerSymbol getCurrentTurn() {
        return currentTurn;
    }

    public void setCurrentTurn(PlayerSymbol currentTurn) {
        this.currentTurn = currentTurn;
    }

    public BoardDTO getBoard() {
        return board;
    }

    public void setBoard(BoardDTO board) {
        this.board = board;
    }

    public GameResult getResult() {
        return result;
    }

    public void setResult(GameResult result) {
        this.result = result;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(SessionStatus status) {
        this.status = status;
    }

    public int getPlayer1Wins() {
        return player1Wins;
    }

    public void setPlayer1Wins(int player1Wins) {
        this.player1Wins = player1Wins;
    }

    public int getPlayer2Wins() { 
        return player2Wins;
    }

    public void setPlayer2Wins(int player2Wins) {
        this.player2Wins = player2Wins;
    }
    
    public int getDraws() {
        return draws;
    }

    public void setDraws(int draws) {
        this.draws = draws;
    }
}
