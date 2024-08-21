package io.github.akki.hideandseek.system.tasks;

import io.github.akki.hideandseek.system.Game;
import io.github.akki.hideandseek.system.Lobby;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static io.github.akki.hideandseek.HideandSeek.config;
import static io.github.akki.hideandseek.system.Lobby.gameCountdown;
import static io.github.akki.hideandseek.system.Lobby.waitingSize;

public class PrepareCountdown extends BukkitRunnable {
    @Override
    public void run() {
        waitingSize = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (Game.isPlayersInTeam(player, "waiting")) {
                waitingSize++;
            }
        }
        if (waitingSize >= config.getInt("lobby.minStart")) {
            gameCountdown--;
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendActionBar(String.format(config.getString("message.lobby.countdown"), gameCountdown));
            }
            if (gameCountdown <= 0) {
                Lobby.startGame();
                gameCountdown = config.getInt("lobby.startGame");
            }
        } else {
            gameCountdown = config.getInt("lobby.startGame");
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (Game.isPlayersInTeam(player, "waiting")) {
                    player.sendActionBar(String.format(config.getString("message.lobby.playerNotEnough"), config.getInt("lobby.minStart") - waitingSize));
                }
            }
        }
    }
}
