package io.github.akki.hideandseek.system.tasks;

import io.github.akki.hideandseek.system.Game;
import org.bukkit.scheduler.BukkitRunnable;

import static io.github.akki.hideandseek.HideandSeek.*;
import static io.github.akki.hideandseek.system.Game.*;

public class GameTimerUpdater extends BukkitRunnable {

    public GameTimerUpdater() {
        timer.setCurrentTime(timer.getDefaultTime());
    }

    @Override
    public void run() {
        if (timer.getCurrentTime() <= 0) {
            stopTimer();
            if (!timer.getStopped()) {
                endGame(true, true, false);
            }
            return;
        }

        if (!timer.getPaused() && timer.getStarted()) {
            timer.setCurrentTime(timer.getCurrentTime() - 1);
        }

        updateBossBarTimer(timer.getCurrentTime(), timer.getDefaultTime());
    }
}
