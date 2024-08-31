package io.github.akki.hideandseek.system.tasks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static io.github.akki.hideandseek.system.Game.*;

public class LobbyTick extends BukkitRunnable {
    @Override
    public void run() {
        Bukkit.getWorld("world").setPVP(isGameStarted);
    }
}
