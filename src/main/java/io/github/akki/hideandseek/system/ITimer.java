package io.github.akki.hideandseek.system;

public interface ITimer {
    public int getCurrentTime();
    public void setCurrentTime(int time);
    public int getDefaultTime();
    public void setDefaultTime(int time);
    public boolean getPaused();
    public boolean getStarted();
    public boolean getStopped();
    public void startTimer();
    public void stopTimer();
    public void pauseTimer();
    public void resetTimer();
}
