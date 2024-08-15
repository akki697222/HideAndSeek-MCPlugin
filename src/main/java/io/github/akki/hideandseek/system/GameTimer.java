package io.github.akki.hideandseek.system;

import static io.github.akki.hideandseek.HideandSeek.config;

public class GameTimer implements ITimer {
    private int currentTime = 0;
    private int defaultTime = config.getInt("timer.default");
    private boolean isTimerStarted = false;
    private boolean isTimerPaused = false;

    @Override
    public int getCurrentTime() {
        return currentTime;
    }

    @Override
    public void setCurrentTime(int time) {
        currentTime = time;
    }

    @Override
    public int getDefaultTime() {
        return defaultTime;
    }

    @Override
    public void setDefaultTime(int time) {
        defaultTime = time;
    }

    @Override
    public boolean getPaused() {
        return isTimerPaused;
    }

    @Override
    public boolean getStarted() {
        return isTimerStarted;
    }

    @Override
    public boolean getStopped() {
        return !isTimerStarted;
    }

    @Override
    public void startTimer() {
        isTimerStarted = true;
        currentTime = defaultTime;
    }

    @Override
    public void stopTimer() {
        isTimerStarted = false;
        isTimerPaused = false;
    }

    @Override
    public void pauseTimer() {
        isTimerPaused = !isTimerPaused;
    }

    @Override
    public void resetTimer() {
        isTimerStarted = false;
        isTimerPaused = false;
        currentTime = 0;
    }
}
