package io.github.akki.hideandseek;

import io.github.akki.hideandseek.system.Game;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.Team;

import static io.github.akki.hideandseek.HideandSeek.*;
import static io.github.akki.hideandseek.system.Game.isPlayersInTeam;

public class HideandSeekEventListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent playerJoinEvent) {
        playerJoinEvent.setJoinMessage(String.format(config.getString("message.event.join"), playerJoinEvent.getPlayer().getName()));
        Game.initPlayer(playerJoinEvent.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent playerQuitEvent) {
        playerQuitEvent.setQuitMessage(String.format(config.getString("message.event.leave"), playerQuitEvent.getPlayer().getName()));
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent playerDeathEvent) {
        Player player = playerDeathEvent.getPlayer();
        Player killer = player.getKiller();

        Team team = scoreboard.getEntryTeam(player.getName());
        if (team != null) {
            team.removeEntry(player.getName());
        }

        dead.addPlayer(player);

        if (killer == null) {
            Bukkit.broadcastMessage(String.format(config.getString("message.game.playerDeath"), player.getName()));
        } else {
            if (isPlayersInTeam(player, "seeker")) {
                Bukkit.broadcastMessage(String.format(config.getString("message.game.seekerKilled"), player.getName(), killer.getName()));
            } else if (isPlayersInTeam(player, "hider")) {
                Bukkit.broadcastMessage(String.format(config.getString("message.game.hiderKilled"), player.getName()));
            }
        }
    }
}
