package Models;

import Enums.Difficulty;
import static Enums.Difficulty.EASY;
import static Enums.Difficulty.HARD;
import static Enums.Difficulty.IMPOSSIBLE;
import static Enums.Difficulty.MEDIUM;
import Enums.GameResult;
import Enums.PlayerSymbol;

import java.util.*;

public class ComputerMoveProvider implements MoveProvider {

    private final Board board;
    private Board aiBoard;
    private final Difficulty difficulty;

    private static final Random RANDOM = new Random();
    private final PlayerSymbol symbol;

    private final Map<String, Integer> cache = new HashMap<>();

    public ComputerMoveProvider(Board board, Difficulty difficulty, PlayerSymbol symbol) {
        this.board = board;
        this.difficulty = difficulty;
        this.symbol = symbol;
    }

    @Override
    public Move getNextMove() {
        aiBoard = copyBoard(board);
        return findBestMove();
    }

    private int getMaxDepth() {
        switch (difficulty) {
            case EASY: return 1;
            case MEDIUM: return 3;
            case HARD: return 5;
            case IMPOSSIBLE: return 9;
        }
        return 1;
    }
    
    private double getRandomThreshold() {
        switch (difficulty) {
            case EASY: return 0.3;
            case MEDIUM: return 0.2;
            case HARD: return 0.15;
            case IMPOSSIBLE: return 0;
        }
        return 0;
    }

    private boolean allowRandomness() {
        return difficulty != Difficulty.IMPOSSIBLE;
    }

    private Move findBestMove() {

        List<Move> bestMoves = new ArrayList<>();
        int bestScore = Integer.MIN_VALUE;
        int maxDepth = getMaxDepth();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (aiBoard.isEmpty(i, j)) {

                    Board next = copyBoard(aiBoard);
                    next.setCell(i, j, symbol);

                    int score = minimax(
                            next,
                            1,
                            maxDepth,
                            false
                    );

                    if (score > bestScore) {
                        bestScore = score;
                        bestMoves.clear();
                        bestMoves.add(new Move(i, j));
                    } else if (score == bestScore) {
                        bestMoves.add(new Move(i, j));
                    }
                }
            }
        }

        return bestMoves.get(RANDOM.nextInt(bestMoves.size()));
    }

    private int minimax(Board state, int depth, int maxDepth, boolean isMax) {

        String key = hashBoard(state, isMax, depth);
        if (cache.containsKey(key)) {
            return cache.get(key);
        }

        GameResult result = state.getBoardResult();

        if (result == GameResult.O_WIN) return 10 - depth;
        if (result == GameResult.X_WIN) return depth - 10;
        if (result == GameResult.DRAW) return 0;

        if (depth >= maxDepth) {
            return 0;
        }

        if (allowRandomness() && depth > 1 && RANDOM.nextDouble() < getRandomThreshold()) {
            return RANDOM.nextInt(3) - 1;
        }

        int best = isMax ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (state.isEmpty(i, j)) {

                    Board next = copyBoard(state);
                    next.setCell(
                            i,
                            j,
                            isMax ? symbol : symbol.getOpponent()
                    );

                    int score = minimax(
                            next,
                            depth + 1,
                            maxDepth,
                            !isMax
                    );

                    best = isMax
                            ? Math.max(best, score)
                            : Math.min(best, score);
                }
            }
        }

        cache.put(key, best);
        return best;
    }

    private Board copyBoard(Board original) {
        Board copy = new Board();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                copy.setCell(i, j, original.getCell(i, j));
            }
        }
        return copy;
    }

    private String hashBoard(Board board, boolean isMax, int depth) {
        StringBuilder sb = new StringBuilder(12);
        for (PlayerSymbol[] row : board.getCells()) {
            for (PlayerSymbol cell : row) {
                sb.append(cell == null ? '-' : cell.name());
            }
        }
        sb.append(isMax).append(depth);
        return sb.toString();
    }
}
