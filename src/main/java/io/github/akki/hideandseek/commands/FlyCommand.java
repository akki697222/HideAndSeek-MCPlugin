package io.github.akki.hideandseek.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static io.github.akki.hideandseek.HideandSeek.config;
import static io.github.akki.hideandseek.system.Game.isCountdown;
import static io.github.akki.hideandseek.system.Game.isGameStarted;

public class FlyCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (isGameStarted || isCountdown) {
                sender.sendMessage("ゲーム中に飛ぶことはできません。");
                player.setAllowFlight(false);
                player.setFlying(false);
                return true;
            } else if (player.isFlying()) {
                sender.sendMessage(config.getString("message.command.fly.disable"));
                player.setAllowFlight(false);
                player.setFlying(false);
            } else {
                sender.sendMessage(config.getString("message.command.fly.enable"));
                player.setAllowFlight(true);
                player.setFlying(true);
            }
        } else {
            sender.sendMessage("サーバー側では実行できないコマンドです。");
        }
        return true;
    }
}
