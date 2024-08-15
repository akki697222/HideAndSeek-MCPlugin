package io.github.akki.hideandseek.commands;

import io.github.akki.hideandseek.system.Game;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.swing.text.PlainDocument;
import java.util.Objects;

import static io.github.akki.hideandseek.HideandSeek.config;
import static io.github.akki.hideandseek.HideandSeek.logger;
import static io.github.akki.hideandseek.system.Game.*;

public class HideandSeekCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        for (String arg : args) {
            logger.info(arg);
        }
        logger.info(String.valueOf(isGameStarted));
        if (args.length == 0) {
            sender.sendMessage(config.getString("message.command.hideandseek.help"));
            return true;
        }
        if (Objects.equals(args[0], "start")) {
            if (isGameStarted) {
                sender.sendMessage(config.getString("message.command.hideandseek.alreadyStarted"));
            } else {
                if (args.length >= 2) {
                    if (randomizeTeams(Integer.parseInt(args[1]))) {
                        Game.startCountdown();
                    } else {
                        sender.sendMessage(config.getString("message.command.hideandseek.overlimit"));
                    }
                } else {
                    sender.sendMessage(config.getString("message.command.hideandseek.setnumber"));
                }
            }
        } else if (Objects.equals(args[0], "stop")) {
            if (isGameStarted) {
                Game.stopGame();
            } else {
                sender.sendMessage(config.getString("message.command.hideandseek.alreadyStopped"));
            }
        } else if (Objects.equals(args[0], "setspawn")) {
            if (args.length >= 2) {
                if (sender instanceof Player) {
                    if (Objects.equals(args[1], "game")) {
                        Location currentLocation = ((Player) sender).getLocation();
                        config.set("game.startPos.x", Math.floor(currentLocation.getX()));
                        config.set("game.startPos.y", Math.floor(currentLocation.getY()));
                        config.set("game.startPos.z", Math.floor(currentLocation.getZ()));
                        sender.sendMessage(String.format(config.getString("message.command.hideandseek.setSpawn"), currentLocation.getX(), currentLocation.getY(), currentLocation.getZ()));
                    } else if (Objects.equals(args[1], "lobby")) {
                        Location currentLocation = ((Player) sender).getLocation();
                        config.set("game.lobbyPos.x", Math.floor(currentLocation.getX()));
                        config.set("game.lobbyPos.y", Math.floor(currentLocation.getY()));
                        config.set("game.lobbyPos.z", Math.floor(currentLocation.getZ()));
                        sender.sendMessage(String.format(config.getString("message.command.hideandseek.setSpawn"), currentLocation.getX(), currentLocation.getY(), currentLocation.getZ()));
                    }
                } else {
                    sender.sendMessage("サーバー側では実行できないコマンドです。");
                }
            } else {
                sender.sendMessage(config.getString("message.command.hideandseek.setnumber"));
            }
        }
        return true;
    }
}
