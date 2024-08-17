package io.github.akki.hideandseek.commands;

import io.github.akki.hideandseek.system.GameEvent;
import io.github.akki.hideandseek.system.GameEvent.Events;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class EventTester implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args[0].equals(Events.SPEEDUP.toString())) {
            GameEvent.eventEvent(Events.SPEEDUP);
        } else if (args[0].equals(Events.JUMP_BOOST.toString())) {
            GameEvent.eventEvent(Events.JUMP_BOOST);
        } else if (args[0].equals(Events.GLOW_ALL.toString())) {
            GameEvent.eventEvent(Events.GLOW_ALL);
        } else if (args[0].equals(Events.SHUFFLE.toString())) {
            GameEvent.eventEvent(Events.SHUFFLE);
        } else if (args[0].equals(Events.BOOM.toString())) {
            GameEvent.eventEvent(Events.BOOM);
        }
        return true;
    }
}
