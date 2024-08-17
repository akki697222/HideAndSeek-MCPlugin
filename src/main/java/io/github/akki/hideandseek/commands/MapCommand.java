package io.github.akki.hideandseek.commands;

import io.github.akki.hideandseek.system.Game;
import io.github.akki.hideandseek.system.mapsystem.MapManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static io.github.akki.hideandseek.HideandSeek.config;
import static io.github.akki.hideandseek.HideandSeek.hideandseekPlugin;

public class MapCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(config.getString("message.command.map.help"));
            return true;
        }
        if (Objects.equals(args[0], "add")) {
            if (args.length >= 3) {
                if (sender instanceof Player) {
                    Location pl = ((Player) sender).getLocation();
                    MapManager.addMap(args[1], args[2], (int) pl.getX(), (int) pl.getY(), (int) pl.getZ());
                } else {
                    sender.sendMessage("サーバー側では実行できないコマンドです。");
                }
            } else {
                sender.sendMessage(config.getString("message.command.hideandseek.noargument"));
                return true;
            }
        }
        return true;
    }
}
