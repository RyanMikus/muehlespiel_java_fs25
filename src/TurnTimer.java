import javax.swing.*;

public class TurnTimer {
    //region Fields
    private Timer timer;
    private int timeLeft;
    private final int initialTime;

    private Runnable onTick;
    private Runnable onTimeout;
    //endregion


    public TurnTimer(int initialTime, Runnable onTick, Runnable onTimeout) {
        this.initialTime = initialTime;
        this.timeLeft = initialTime;
        this.onTick = onTick;
        this.onTimeout = onTimeout;
    }

    public void startTimer() {
        timer = new Timer(1000, e -> {
            timeLeft--;

            if(onTick != null) {
                onTick.run();
            }

            if(timeLeft <= 0) {
                stopTimer();
                if(onTimeout != null) {
                    onTimeout.run();
                }
            }
            timer.start();
        });
    }

    public void stopTimer() {
        if(timer != null && timer.isRunning()) {
            timer.stop();
        }
    }

    public void resetTimer() {
        stopTimer();
        timeLeft = initialTime;
        startTimer();
    }

    public int getTimeLeft() {
        return timeLeft;
    }
}
