package io.github.akki.hideandseek.system;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

import static io.github.akki.hideandseek.HideandSeek.config;

public class GameEvent {
    public enum Events {
        GLOW_ALL,
        SPEEDUP,
        JUMP_BOOST,

    }

    public static void randomEvent() {
        Events[] eventList = Events.values();
        int randomIndex = new Random().nextInt(eventList.length);

        eventEvent(eventList[randomIndex]);
    }

    public static void eventEvent(Events event) {
        switch (event) {
            case SPEEDUP: {
                eventMessage("speedup");
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (Game.isPlayersInTeam(player, "hider")) {
                        PotionEffect effect = new PotionEffect(PotionEffectType.SPEED, 20 * 15, 3);
                        player.addPotionEffect(effect);
                    }
                }
                break;
            }
            case GLOW_ALL: {
                eventMessage("glowall");
                for (Player player : Bukkit.getOnlinePlayers()) {
                    PotionEffect effect = new PotionEffect(PotionEffectType.GLOWING, 20 * 10, 255);
                    player.addPotionEffect(effect);
                }
                break;
            }
            case JUMP_BOOST: {
                eventMessage("jumpboost");
                for (Player player : Bukkit.getOnlinePlayers()) {
                    PotionEffect effect = new PotionEffect(PotionEffectType.JUMP, 20 * 15, 3);
                    player.addPotionEffect(effect);
                }
                break;
            }
        }
    }

    public static void eventMessage(String eventname) {
        Bukkit.broadcastMessage(ChatColor.GOLD + config.getString("message.gameevent." + eventname));
    }
}
