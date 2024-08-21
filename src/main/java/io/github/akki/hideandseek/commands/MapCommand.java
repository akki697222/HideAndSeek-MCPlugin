package io.github.akki.hideandseek.commands;

import io.github.akki.hideandseek.system.map.MapManager;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static io.github.akki.hideandseek.HideandSeek.config;

public class MapCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(config.getString("message.command.map.help"));
            return true;
        }
        if (Objects.equals(args[0], "add")) {
            if (args.length >= 5) {
                if (sender instanceof Player) {
                    Location pl = ((Player) sender).getLocation();
                    int parsedInt;
                    try {
                        parsedInt = Integer.parseInt(args[3]);
                    } catch (NumberFormatException e) {
                        sender.sendMessage(e.getMessage());
                        return true;
                    }
                    MapManager.addMap(args[1], args[2], (int) pl.getX(), (int) pl.getY(), (int) pl.getZ(), parsedInt, args[4]);
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
