package io.github.akki.hideandseek.system.tasks;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import static io.github.akki.hideandseek.HideandSeek.config;
import static io.github.akki.hideandseek.HideandSeek.timer;
import static io.github.akki.hideandseek.system.Event.randomEvent;
import static io.github.akki.hideandseek.system.Game.*;

public class TimerUpdater extends BukkitRunnable {
    public static int eventTimer = 0;
    public static boolean glowed = false;

    public TimerUpdater() {
        timer.setCurrentTime(timer.getDefaultTime());
    }

    @Override
    public void run() {
        if (timer.getCurrentTime() <= 0) {
            if (!timer.getStopped()) {
                glowed = false;
                endGame(true, true, false);
                stopTimer();
            }
            return;
        }

        if (timer.getCurrentTime() <= config.getInt("game.glow") && !glowed) {
            glowed = true;
            Bukkit.broadcastMessage(String.format(config.getString("message.game.glow"), config.getInt("game.glow")));
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
            try {
                randomEvent();
            } catch (Exception e) {
                Bukkit.broadcastMessage(ChatColor.RED + "[FATAL] " + ChatColor.RESET + e.getMessage());
            }
        } else if (!timer.getPaused() && timer.getStarted()){
            eventTimer++;
        }

        updateBossBarTimer(timer.getCurrentTime(), timer.getDefaultTime());
    }
}
