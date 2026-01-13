package util;

import java.util.function.Consumer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

/**
 *
 * @author Ahmed_El_Sayyad
 */
public class GameTimer {
    private Timeline timeline;
    private int remainingSeconds;
    private final int initialSeconds;
    private final Consumer<Integer> onTick;  
    private final Runnable onFinished;       

    public GameTimer(int seconds, Consumer<Integer> onTick, Runnable onFinished) {
        this.initialSeconds = seconds;
        this.remainingSeconds = seconds;
        this.onTick = onTick;
        this.onFinished = onFinished;
        setupTimeline();
    }

    private void setupTimeline() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            remainingSeconds--;
            onTick.accept(remainingSeconds);
            if (remainingSeconds <= 0) {
                stop();
                onFinished.run();
            }
        }));
        timeline.setCycleCount(initialSeconds);
    }

    public void start() {
        remainingSeconds = initialSeconds;
        onTick.accept(remainingSeconds); 
        timeline.play();
    }

    public void stop() {
        if (timeline != null) {
            timeline.stop();
        }
    }
}
