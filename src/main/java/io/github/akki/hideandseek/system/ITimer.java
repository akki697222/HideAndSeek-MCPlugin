package io.github.akki.hideandseek.system;

public interface ITimer {
    int getCurrentTime();
    void setCurrentTime(int time);
    int getDefaultTime();
    void setDefaultTime(int time);
    boolean getPaused();
    boolean getStarted();
    boolean getStopped();
    void startTimer();
    void stopTimer();
    void pauseTimer();
    void resetTimer();
}
