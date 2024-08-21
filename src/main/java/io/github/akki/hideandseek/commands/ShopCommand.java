package io.github.akki.hideandseek.commands;

import io.github.akki.hideandseek.system.Shop;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static io.github.akki.hideandseek.system.Game.isGameStarted;

public class ShopCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!isGameStarted) {
            sender.sendMessage(ChatColor.RED + "ショップコマンドはゲーム中のみ使用可能です。");
            return true;
        }
        if (sender instanceof Player) {
            Shop.openShop(((Player) sender).getPlayer());
        } else {
            sender.sendMessage("サーバー側では実行できないコマンドです。");
        }
        return true;
    }
}
