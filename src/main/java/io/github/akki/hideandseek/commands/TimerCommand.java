package io.github.akki.hideandseek.commands;

import io.github.akki.hideandseek.system.Game;
import org.bukkit.command.*;

import java.util.Objects;

import static io.github.akki.hideandseek.HideandSeek.*;

public class TimerCommand implements CommandExecutor {
    @Override
    public boolean onCommand (CommandSender sender, Command command, String label, String[] args ) {
        if (args.length == 0) {
            sender.sendMessage(config.getString("message.command.timer.help"));
            return true;
        }
        if (Objects.equals(args[0], "start")) {
            if (timer.getStarted()) {
                sender.sendMessage(config.getString("message.command.timer.alreadyStarted"));
            } else {
                Game.startTimer();
                sender.sendMessage(config.getString("message.command.timer.start"));
            }
        } else if (Objects.equals(args[0], "stop")) {
            if (timer.getStopped()) {
                sender.sendMessage(config.getString("message.command.timer.alreadyStopped"));
            } else {
                Game.stopTimer();
                sender.sendMessage(config.getString("message.command.timer.stop"));
            }
        } else if (Objects.equals(args[0], "pause")) {
            timer.pauseTimer();
            if (timer.getPaused()) {
                sender.sendMessage(config.getString("message.command.timer.pause.1"));
            } else {
                sender.sendMessage(config.getString("message.command.timer.pause.2"));
            }
        } else if (Objects.equals(args[0], "set")) {
            if (timer.getStarted()) {
                sender.sendMessage(config.getString("message.command.timer.setFailed"));
                return true;
            }
            if (args.length >= 2) {
                timer.setDefaultTime(Integer.parseInt(args[1]));
                sender.sendMessage(String.format(config.getString("message.command.timer.set"), args[1]));
            } else {
                timer.setDefaultTime(config.getInt("timer.default"));
                sender.sendMessage(String.format(config.getString("message.command.timer.setDefault"), config.getInt("timer.default")));
            }
        } else if (Objects.equals(args[0], "get")) {
            sender.sendMessage(String.format(config.getString("message.command.timer.getCurrentSettings"), timer.getDefaultTime()));
        } else {
            sender.sendMessage(String.format(config.getString("message.command.notfound"), command.getName(), args[0]));
        }
        return true;
    }
}
