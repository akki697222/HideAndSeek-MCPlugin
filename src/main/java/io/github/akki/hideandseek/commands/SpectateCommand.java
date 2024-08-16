package io.github.akki.hideandseek.commands;

import io.github.akki.hideandseek.system.Game;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static io.github.akki.hideandseek.HideandSeek.config;

public class SpectateCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(config.getString("message.command.spectate.help"));
            return true;
        }
        if (Objects.equals(args[0], "add")) {
            if (args.length == 2) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getName().equals(args[1])) {
                        Game.addNextSpectator(player);
                        return true;
                    }
                }
                sender.sendMessage(config.getString("message.command.spectate.playernotfound"));
            }
        } else if (Objects.equals(args[0], "remove")) {
            if (args.length == 2) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getName().equals(args[1])) {
                        Game.removeNextSpectator(player);
                        return true;
                    }
                }
                sender.sendMessage(config.getString("message.command.spectate.playernotfound"));
            }
        }
        return true;
    }
}
