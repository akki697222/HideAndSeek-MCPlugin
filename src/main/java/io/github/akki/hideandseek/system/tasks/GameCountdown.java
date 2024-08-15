package io.github.akki.hideandseek.system.tasks;

import org.bukkit.scheduler.BukkitRunnable;

import static io.github.akki.hideandseek.system.Game.*;

public class GameCountdown extends BukkitRunnable {
    @Override
    public void run() {
        if (countdown <= 0) {
            if (!isGameStarted) {
                startGame();
                cancel();
            }
        } else {
            countdownTick();
            countdown--;
        }
    }
}
