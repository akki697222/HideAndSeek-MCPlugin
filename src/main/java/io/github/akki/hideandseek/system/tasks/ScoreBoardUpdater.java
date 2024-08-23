package io.github.akki.hideandseek.system.tasks;

import io.github.akki.hideandseek.utils.ObjectiveUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;

import static io.github.akki.hideandseek.HideandSeek.*;
import static io.github.akki.hideandseek.system.Game.*;

public class ScoreBoardUpdater extends BukkitRunnable {
    @Override
    public void run() {
        if (isGameStarted) {
            ObjectiveUtil.removeEntry(gameView);
            gameView.getScore("マップ: " + currentMap.get("title")).setScore(0);
            gameView.getScore("難易度: " + getMode().toString()).setScore(0);
            gameView.setDisplaySlot(DisplaySlot.SIDEBAR);

            for (Player player : Bukkit.getOnlinePlayers()) {
                player.setScoreboard(scoreboard);
                player.sendActionBar(ChatColor.RED + "鬼の人数: " + getSeeker() + ChatColor.RESET + " | " + ChatColor.GREEN + "逃げの人数: " + getHider() + ChatColor.RESET + " | " + "バッテリー残量: " + battery.getScore(player).getScore());
            }
        } else {
            ObjectiveUtil.removeEntry(gameView);
            gameView.getScore("プレイヤー数: " + Bukkit.getOnlinePlayers().size()).setScore(0);
            gameView.setDisplaySlot(DisplaySlot.SIDEBAR);
        }
    }
}
