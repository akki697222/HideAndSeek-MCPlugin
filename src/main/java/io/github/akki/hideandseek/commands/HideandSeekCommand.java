package io.github.akki.hideandseek.commands;

import io.github.akki.hideandseek.HideandSeek;
import io.github.akki.hideandseek.system.Game;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

import static io.github.akki.hideandseek.HideandSeek.*;
import static io.github.akki.hideandseek.system.Game.*;

public class HideandSeekCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(config.getString("message.command.hideandseek.help"));
            return true;
        }
        if (Objects.equals(args[0], "start")) {
            if (isGameStarted || isCountdown) {
                sender.sendMessage(config.getString("message.command.hideandseek.alreadyStarted"));
            } else {
                if (args.length >= 2) {
                    if (Objects.equals(args[1], "custom")) {
                        List<Player> hiderList = getNextHider();
                        List<Player> seekerList = getNextSeeker();

                        if (hiderList.isEmpty() || seekerList.isEmpty()) {
                            sender.sendMessage(config.getString("message.command.hideandseek.failedcustom"));
                            return true;
                        } else {
                            Game.customGame();
                            Game.startCountdown();
                        }
                    } else {
                        int argint;
                        try {
                            argint = Integer.parseInt(args[1]);
                        } catch (NumberFormatException e) {
                            return true;
                        }
                        if (randomizeTeams(argint)) {
                            Game.startCountdown();
                        } else {
                            sender.sendMessage(config.getString("message.command.hideandseek.overlimit"));
                        }
                    }
                } else {
                    sender.sendMessage(config.getString("message.command.hideandseek.noargument"));
                    return true;
                }
            }
        } else if (Objects.equals(args[0], "stop")) {
            if (isGameStarted) {
                Game.stopGame();
            } else {
                sender.sendMessage(config.getString("message.command.hideandseek.alreadyStopped"));
            }
        } else if (Objects.equals(args[0], "reload")) {
            Plugin plugin = HideandSeek.getPlugin();
            plugin.reloadConfig();
            try {
                mapConfig.load(mapConfFile);
            } catch (Exception e) {
                logger.warning("Failed to reload config\n" + e);
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
                        hideandseekPlugin.saveConfig();
                        hideandseekPlugin.reloadConfig();
                    } else if (Objects.equals(args[1], "lobby")) {
                        Location currentLocation = ((Player) sender).getLocation();
                        config.set("game.lobbyPos.x", Math.floor(currentLocation.getX()));
                        config.set("game.lobbyPos.y", Math.floor(currentLocation.getY()));
                        config.set("game.lobbyPos.z", Math.floor(currentLocation.getZ()));
                        sender.sendMessage(String.format(config.getString("message.command.hideandseek.setSpawn"), currentLocation.getX(), currentLocation.getY(), currentLocation.getZ()));
                        hideandseekPlugin.saveConfig();
                        hideandseekPlugin.reloadConfig();
                    }
                } else {
                    sender.sendMessage("サーバー側では実行できないコマンドです。");
                }
            } else {
                sender.sendMessage(config.getString("message.command.hideandseek.setnumber"));
            }
        } else if (Objects.equals(args[0], "settings")) {
            if (args.length >= 2) {
                if (Objects.equals(args[1], "panel")) {
                    if (sender instanceof Player) {

                    } else {
                        sender.sendMessage("サーバー側では実行できないコマンドです。");
                    }
                } else {
                    sender.sendMessage(config.getString("message.command.hideandseek.noargument"));
                }
            } else {
                sender.sendMessage(ChatColor.BOLD + config.getString("message.command.hideandseek.settings.title"));
                sender.sendMessage(String.format(config.getString("message.command.hideandseek.settings.players"), Bukkit.getOnlinePlayers().size()));
                sender.sendMessage(String.format(config.getString("message.command.hideandseek.settings.hiders"), getNextHider().size()));
                sender.sendMessage(String.format(config.getString("message.command.hideandseek.settings.seekers"), getNextSeeker().size()));
                sender.sendMessage(String.format(config.getString("message.command.hideandseek.settings.spectates"), getNextSpectator().size()));
                sender.sendMessage(ChatColor.BOLD + config.getString("message.command.hideandseek.settings.items"));
                sender.sendMessage(String.format(config.getString("message.command.hideandseek.settings.item.flash"), config.getString("item.flash")));
                sender.sendMessage(ChatColor.BOLD + config.getString("message.command.hideandseek.settings.time"));
                sender.sendMessage(String.format(config.getString("message.command.hideandseek.settings.times.event"), config.getInt("game.event")));
                sender.sendMessage(String.format(config.getString("message.command.hideandseek.settings.times.countdown"), config.getInt("game.countdown")));
            }
        }
        return true;
    }

    public static String getMessage(String name) {
        return config.getString("message.ui.settings." + name);
    }
}
