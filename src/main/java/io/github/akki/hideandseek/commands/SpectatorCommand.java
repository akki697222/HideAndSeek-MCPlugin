package io.github.akki.hideandseek.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static io.github.akki.hideandseek.HideandSeek.config;
import static io.github.akki.hideandseek.system.Game.*;

public class SpectatorCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(config.getString("message.command.spectator.help"));
            return true;
        }
        if (sender instanceof Player) {
            if (!isGameStarted) {
                if (args[0].equals("join")) {
                    if (isPlayersInTeam((Player) sender, "spectator")) {
                        sender.sendMessage(ChatColor.RED + config.getString("message.command.spectator.alreadyJoined"));
                    } else {
                        addNextSpectator((Player) sender);
                    }
                } else if (args[0].equals("leave")) {
                    if (isPlayersInTeam((Player) sender, "spectator")) {
                        removeNextSpectator((Player) sender);
                    } else {
                        sender.sendMessage(ChatColor.RED + config.getString("message.command.spectator.alreadyLeaved"));
                    }
                }
            } else {
                sender.sendMessage(ChatColor.RED + config.getString("message.command.spectator.changeFailed"));
            }
        } else {
            sender.sendMessage("サーバー側では実行できないコマンドです。");
        }
        return true;
    }
}
