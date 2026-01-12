package models;

import com.mycompany.java.client.project.data.Request;
import com.mycompany.java.client.project.data.ServerConnection;
import enums.GameResult;
import enums.PlayerSymbol;
import enums.RequestType;
import enums.SessionType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameSession {

    private Game game;
    private Player player1;
    private Player player2;

    private int drawCount;
    private int player1Wins;
    private int player2Wins;

    private SessionType sessionType;
    private GameResult lastResult;
    private List<Player> spectatorsList;
    private MoveProvider moveProvider;
    
    private String sessionId;
    
    private ServerConnection con;

    public GameSession(Player player1, Player player2, SessionType type) {
        this.player1 = player1;
        this.player2 = player2;
        this.sessionType = type;
        this.game = new Game();
        this.lastResult = GameResult.NONE;
        this.player1Wins = this.player2Wins = this.drawCount = 0;

        this.spectatorsList = new ArrayList<>();
        
        if (sessionType == SessionType.ONLINE) {
            try {
                con = ServerConnection.getConnection();
            } catch (IOException ex) {
                System.getLogger(GameSession.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
        }
        
        switch(type) {
            case LOCAL:
                moveProvider = new LocalMoveProvider();
                break;
            case AI:
                AIPlayer aiPlayer = (AIPlayer) player2;
                moveProvider = new ComputerMoveProvider(game.getBoard(), aiPlayer.getDifficulty(), player2.getSymbol());
                break;
            case ONLINE:
                moveProvider = new OnlineMoveProvider();
                break;
        }
    }
    public void startNewGame() {
        this.game = new Game();
        this.lastResult = GameResult.NONE;
        if (sessionType == SessionType.AI) {
            AIPlayer aiPlayer = (AIPlayer) player2;
            moveProvider = new ComputerMoveProvider(game.getBoard(), aiPlayer.getDifficulty(), player2.getSymbol());
        }
    }
    
    public Game getGame(){return this.game;}
    public int getPlayer1Wins(){return this.player1Wins;}
    public int getPlayer2Wins(){return this.player2Wins;}

    public void setPlayer1Wins(int player1Wins) {
        this.player1Wins = player1Wins;
    }

    public void setPlayer2Wins(int player2Wins) {
        this.player2Wins = player2Wins;
    }

    public void setDrawCount(int drawCount) {
        this.drawCount = drawCount;
    }
    
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    public void leaveMatch() {
        if (con != null) {
            com.google.gson.JsonPrimitive payload = new com.google.gson.JsonPrimitive(sessionId);
            con.sendRequest(new Request(RequestType.LEAVE_GAME, payload));
        }
    }
    
    public boolean playMove(int row ,int col) {

        if (game.hasEnded()) {
            return false;
        }

        boolean success = game.playMove(row,col);

        if (!success) {
            return false;
        }

        lastResult = game.checkResult();

        if (lastResult == GameResult.NONE) {
            game.switchPlayer();

        } else {
            handleGameEnd();
        }

        return true;
    }
    
    public MoveProvider getMoveProvider() {
        return moveProvider;
    }

    private void handleGameEnd() {

        game.setHasEnded(true);
        int rewardPoints = 10;

        switch (lastResult) {

            case X_WIN: {
                if (player1.getScore() - player2.getScore() > 50) {
                    rewardPoints /= 2;
                }
                player1.updateScore(rewardPoints);
                player1Wins++;
            }
            break;
            case O_WIN: {
                if (player2.getScore() - player1.getScore() > 50) {
                    rewardPoints /= 2;
                }
                player2.updateScore(rewardPoints);
                player2Wins++;
            }
            break;
            case DRAW: {
                player1.updateScore(1);
                player2.updateScore(1);
                drawCount++;
            }
        }
    }
    
    public void switchPlayerSymbols() {
        PlayerSymbol s1 = player1.getSymbol();
        player1.setSymbol(player2.getSymbol());
        player2.setSymbol(s1);
    }

    public void resetSession() {
        game.reset();
        lastResult = GameResult.NONE;
    }

    public Player getCurrentPlayer() {
        return game.getCurrentPlayer() == PlayerSymbol.X ? player1 : player2;
    }

    public GameResult getLastResult() {
        return lastResult;
    }
    
    public void setLastResult(GameResult result) {
        this.lastResult = result;
    }

    public boolean isGameEnded() {
        return game.hasEnded();
    }

    public int getDrawCount() {
        return drawCount;
    }


    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public SessionType getSessionType() {
        return sessionType;
    }

    public List<Player> getSpectators() {
        return spectatorsList;
    }

}
