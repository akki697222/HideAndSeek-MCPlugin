package io.github.akki.hideandseek.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static io.github.akki.hideandseek.HideandSeek.waiting;

public class ReadyCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player) {
            waiting.addPlayer((Player) sender);
        } else {
            sender.sendMessage("サーバー側では実行できないコマンドです。");
            return false;
        }
        return true;
    }
}
