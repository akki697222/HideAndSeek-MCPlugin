package io.github.akki.hideandseek.commands;

import io.github.akki.hideandseek.system.Event;
import io.github.akki.hideandseek.system.Event.Events;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static io.github.akki.hideandseek.HideandSeek.config;

public class EventTesterCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(config.getString("message.command.eventtester.help"));
            return true;
        }
        if (args[0].equals(Events.SPEEDUP.toString())) {
            Event.eventEvent(Events.SPEEDUP);
        } else if (args[0].equals(Events.JUMP_BOOST.toString())) {
            Event.eventEvent(Events.JUMP_BOOST);
        } else if (args[0].equals(Events.GLOW_ALL.toString())) {
            Event.eventEvent(Events.GLOW_ALL);
        } else if (args[0].equals(Events.SHUFFLE.toString())) {
            Event.eventEvent(Events.SHUFFLE);
        } else if (args[0].equals(Events.BOOM.toString())) {
            Event.eventEvent(Events.BOOM);
        }
        return true;
    }
}
