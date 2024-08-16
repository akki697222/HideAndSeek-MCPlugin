package io.github.akki.hideandseek.system.tasks;

import io.github.akki.hideandseek.system.Game;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import static io.github.akki.hideandseek.HideandSeek.*;
import static io.github.akki.hideandseek.system.Game.*;
import static io.github.akki.hideandseek.system.GameEvent.randomEvent;

public class GameTimerUpdater extends BukkitRunnable {
    public static int eventTimer = 0;

    public GameTimerUpdater() {
        timer.setCurrentTime(timer.getDefaultTime());
    }

    @Override
    public void run() {
        if (timer.getCurrentTime() <= 0) {
            if (!timer.getStopped()) {
                endGame(true, true, false);
                stopTimer();
            }
            return;
        }

        if (timer.getCurrentTime() <= config.getInt("game.glow")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (isPlayersInTeam(player, "hider")) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 999999, 255));
                }
            }
        }

        if (!timer.getPaused() && timer.getStarted()) {
            timer.setCurrentTime(timer.getCurrentTime() - 1);
        }

        if (eventTimer >= config.getInt("game.event")) {
            eventTimer = 0;
            randomEvent();
        } else if (!timer.getPaused() && timer.getStarted()){
            eventTimer++;
        }

        updateBossBarTimer(timer.getCurrentTime(), timer.getDefaultTime());
    }
}
