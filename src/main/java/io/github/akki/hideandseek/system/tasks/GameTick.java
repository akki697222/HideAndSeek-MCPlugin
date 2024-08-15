package io.github.akki.hideandseek.system.tasks;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static io.github.akki.hideandseek.system.Game.*;

public class GameTick extends BukkitRunnable {
    @Override
    public void run() {
        if (!checkHiders()) {
            endGame(false, false, false);
            cancel();
        }
        if (!checkSeekers()) {
            endGame(false, true, false);
            cancel();
        }
    }
}
