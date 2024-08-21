package io.github.akki.hideandseek.system;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Team;

import java.util.*;

import static io.github.akki.hideandseek.HideandSeek.*;
import static io.github.akki.hideandseek.system.Game.currentMap;

public class Event {
    public static void randomEvent() {
        Events[] eventList = Events.values();
        List<Events> eventArrayList = new ArrayList<>(Arrays.asList(eventList));
        List<Integer> removeList = new ArrayList<>();

        int idx = 0;
        for (Events event : eventArrayList) {
            if (!getEventEnabled(event.toString())) {
                removeList.add(idx);
            }
            idx++;
        }

        removeList.sort(Collections.reverseOrder());

        for (int index : removeList) {
            eventArrayList.remove(index);
        }

        if (!eventArrayList.isEmpty()) {
            int randomIndex = new Random().nextInt(eventArrayList.size());
            eventEvent(eventArrayList.get(randomIndex));
        } else {
            Bukkit.broadcastMessage(ChatColor.RED + "[ERROR] " + ChatColor.RESET + "イベントリストが空です。難易度 " + Game.getMode().toString() + " に対応するイベントが存在しない、もしくはコードにミスがある可能性があります。");
        }
    }

    public static void eventEvent(Events event) {
        switch (event) {
            case SPEEDUP: {
                eventMessage("speedup", config.getInt("event.SPEEDUP.duration"));
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (Game.isPlayersInTeam(player, "hider")) {
                        PotionEffect effect = new PotionEffect(PotionEffectType.SPEED, 20 * config.getInt("event.SPEEDUP.duration"), 3);
                        player.addPotionEffect(effect);
                    }
                }
                break;
            }
            case GLOW_ALL: {
                eventMessage("glowall", config.getInt("event.GLOW_ALL.duration"));
                for (Player player : Bukkit.getOnlinePlayers()) {
                    PotionEffect effect = new PotionEffect(PotionEffectType.GLOWING, 20 * config.getInt("event.GLOW_ALL.duration"), 255);
                    player.addPotionEffect(effect);
                }
                break;
            }
            case JUMP_BOOST: {
                eventMessage("jumpboost", config.getInt("event.JUMP_BOOST.duration"));
                for (Player player : Bukkit.getOnlinePlayers()) {
                    PotionEffect effect = new PotionEffect(PotionEffectType.JUMP, 20 * config.getInt("event.JUMP_BOOST.duration"), 3);
                    player.addPotionEffect(effect);
                }
                break;
            }
            case SHUFFLE: {
                eventMessage("shuffle");
                List<Location> locations = new ArrayList<>();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!Game.isPlayersInTeam(player, "spectator") && !Game.isPlayersInTeam(player, "visitor") && !Game.isPlayersInTeam(player, "dead")) {
                        locations.add(player.getLocation());
                    }
                }
                Collections.shuffle(locations);
                int i = 0;
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!Game.isPlayersInTeam(player, "spectator") && !Game.isPlayersInTeam(player, "visitor") && !Game.isPlayersInTeam(player, "dead")) {
                        player.teleport(locations.get(i));
                        i++;
                    }
                }
                break;
            }
            case BOOM: {
                eventMessage("boom");
                List<Location> locations = new ArrayList<>();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!Game.isPlayersInTeam(player, "spectator") && !Game.isPlayersInTeam(player, "visitor") && !Game.isPlayersInTeam(player, "dead")) {
                        locations.add(player.getLocation());
                    }
                }
                Collections.shuffle(locations);
                TNTPrimed tnt = Bukkit.getWorld("world").spawn(locations.get(new Random().nextInt(locations.size())), TNTPrimed.class);
                tnt.setFuseTicks(0);
                break;
            }
            default: {
                randomEvent();
            }
        }
    }

    public static void eventMessage(String eventname) {
        Bukkit.broadcastMessage(ChatColor.BLUE + "[GAME EVENT] " + ChatColor.RESET + config.getString("message.gameevent." + eventname));
    }

    public static void eventMessage(String eventname, int duration) {
        Bukkit.broadcastMessage(ChatColor.BLUE + "[GAME EVENT] " + ChatColor.RESET + String.format(config.getString("message.gameevent." + eventname), duration));
    }

    public static boolean getEventEnabled(String eventName) {
        logger.info(eventName);
        String[] splitedEvents = config.getString("event." + eventName + ".mode").split(",");
        boolean ans = false;
        for (String splitedEvent : splitedEvents) {
            logger.info(splitedEvent);
        }
        for (String event : splitedEvents) {
            if (Objects.equals(config.getString("event." + eventName + ".state"), "enable") && Objects.equals(event, Game.getMode().toString())) {
                ans = true;
            }
        }
        logger.info(String.valueOf(ans));
        return ans;
    }

    public enum Events {
        GLOW_ALL,
        SPEEDUP,
        JUMP_BOOST,
        SHUFFLE,
        BOOM,
    }
}
