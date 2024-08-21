package io.github.akki.hideandseek.system.tasks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
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
        if (isCountdown) {
            PotionEffect heal = new PotionEffect(PotionEffectType.HEAL, 999999, 255);
            for (Player pl : Bukkit.getOnlinePlayers()) {
                pl.addPotionEffect(heal);
            }
        } else {
            for (Player pl : Bukkit.getOnlinePlayers()) {
                pl.removePotionEffect(PotionEffectType.HEAL);
            }
        }
    }
}
