package io.github.akki.hideandseek;

import io.github.akki.hideandseek.system.Game;
import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import static io.github.akki.hideandseek.HideandSeek.*;
import static io.github.akki.hideandseek.system.Game.*;

public class HideandSeekEventListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent playerJoinEvent) {
        playerJoinEvent.setJoinMessage(String.format(config.getString("message.event.join"), playerJoinEvent.getPlayer().getName()));
        playerJoinEvent.getPlayer().getInventory().clear();
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

        EntityDamageEvent.DamageCause cause = playerDeathEvent.getEntity().getLastDamageCause().getCause();

        switch (cause) {
            case BLOCK_EXPLOSION: {
                Bukkit.broadcastMessage(String.format(config.getString("message.game.playerExplosion"), player.getName()));
                break;
            }
            default: {
                if (killer == null) {
                    Bukkit.broadcastMessage(String.format(config.getString("message.game.playerDeath"), player.getName()));
                } else {
                    if (isPlayersInTeam(player, "seeker")) {
                        Bukkit.broadcastMessage(String.format(config.getString("message.game.seekerKilled"), player.getName(), killer.getName()));
                    } else if (isPlayersInTeam(player, "hider")) {
                        Bukkit.broadcastMessage(String.format(config.getString("message.game.hiderKilled"), player.getName()));
                    }
                }
                break;
            }
        }

        if (isGameStarted || isCountdown) {
            int x = (int) currentMap.get("x");
            int y = (int) currentMap.get("y");
            int z = (int) currentMap.get("z");
            if (isPlayersInTeam(player, "hider") && killer != null) {
                Team team = scoreboard.getEntryTeam(player.getName());
                if (team != null) {
                    team.removeEntry(player.getName());
                }

                dead.addPlayer(player);
                player.setGameMode(GameMode.SPECTATOR);
                if (isPlayersInTeam(killer, "seeker")) {
                    killer.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 2);
                    killer.sendMessage(ChatColor.GREEN + String.format(config.getString("message.game.getPoint"), config.getInt("shop.seekersAdd")));
                    shopPoint.getScore(killer).setScore(shopPoint.getScore(killer).getScore() + config.getInt("shop.seekersAdd"));
                }
            } else if (!isPlayersInTeam(player, "seeker")) {
                Team team = scoreboard.getEntryTeam(player.getName());
                if (team != null) {
                    team.removeEntry(player.getName());
                }

                dead.addPlayer(player);
                player.setGameMode(GameMode.SPECTATOR);
            } else if (isPlayersInTeam(player, "seeker") && cause == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) {
                Bukkit.getScheduler().runTaskTimer(getPlugin(), new BukkitRunnable() {
                    int respawns = config.getInt("game.respawn");

                    @Override
                    public void run() {
                        player.sendTitle(String.format(config.getString("message.game.respawnat"), respawns), "");
                        respawns--;
                        if (respawns <= 0) {
                            cancel();
                        }
                    }
                }, 0L, 20L);
                player.teleport(new Location(Bukkit.getWorld("world"), x, y, z));
            } else if (isPlayersInTeam(player, "seeker") && killer != null) {
                Team team = scoreboard.getEntryTeam(player.getName());
                if (team != null) {
                    team.removeEntry(player.getName());
                }

                dead.addPlayer(player);
                player.setGameMode(GameMode.SPECTATOR);
            } else if (isPlayersInTeam(player, "seeker") && killer == null) {
                Bukkit.getScheduler().runTaskTimer(getPlugin(), new BukkitRunnable() {
                    int respawns = config.getInt("game.respawn");

                    @Override
                    public void run() {
                        player.sendTitle(String.format(config.getString("message.game.respawnat"), respawns), "");
                        respawns--;
                        if (respawns <= 0) {
                            cancel();
                        }
                    }
                }, 0L, 20L);
                player.teleport(new Location(Bukkit.getWorld("world"), x, y, z));
            }
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (event.getEntity() instanceof TNTPrimed) {
            event.blockList().clear();
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent playerMoveEvent) {
        if (isPlayersInTeam(playerMoveEvent.getPlayer(), "seeker") && isCountdown) {
            playerMoveEvent.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction().toString().contains("RIGHT_CLICK")) {
            Player player = event.getPlayer();
            ItemStack item = event.getItem();

            if (item == null) {
                return;
            }

            if (item.getType() == Material.RECOVERY_COMPASS) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null && meta.hasDisplayName() && meta.getDisplayName().equals("Player Searcher")) {
                    if (battery.getScore(player).getScore() > 0) {
                        if (isPlayersInTeam(player, "hider") && isTeamNearby(player, "seeker", config.getInt("item.playersearcher.hiderSearches"))) {
                            player.sendMessage(String.format(config.getString("message.item.playersearcher.seekerfound"), config.getInt("item.playersearcher.hiderSearches")));
                        } else if (isPlayersInTeam(player, "seeker") && isTeamNearby(player, "hider", config.getInt("item.playersearcher.seekerSearches"))) {
                            player.sendMessage(String.format(config.getString("message.item.playersearcher.hiderFound"), config.getInt("item.playersearcher.seekerSearches")));
                        } else {
                            player.sendMessage(String.format(config.getString("message.item.playersearcher.notfound"), config.getInt("item.playersearcher.seekerSearches")));
                        }
                        battery.getScore(player).setScore(battery.getScore(player).getScore() - 1);
                    } else {
                        player.sendMessage(config.getString("message.item.noBattery"));
                    }
                }
            }

            if (item.getType() == Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null && meta.hasDisplayName() && meta.getDisplayName().equals("バッテリーパック")) {
                    battery.getScore(player).setScore(config.getInt("game.maxBattery"));
                    player.sendMessage(config.getString("message.game.useBattery"));
                    player.getInventory().remove(item);
                }
            }
        }
    }

    @EventHandler
    public void inventoryMoveEvent(InventoryMoveItemEvent event) {
        if (isGameStarted || isCountdown) {
            ItemMeta itemMeta = event.getItem().getItemMeta();
            if (itemMeta != null) {
                if (event.getItem().getType() == Material.LEATHER_CHESTPLATE) {
                    event.setCancelled(true);
                }
            }
        }
    }

    private boolean isTeamNearby(Player player, String teamName, int radius) {
        Team team = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(teamName);

        if (team == null) {
            return false;
        }

        for (Player nearbyPlayer : player.getWorld().getPlayers()) {
            if (nearbyPlayer != player && nearbyPlayer.getLocation().distance(player.getLocation()) <= radius) {
                if (team.hasEntry(nearbyPlayer.getName())) {
                    return true;
                }
            }
        }
        return false;
    }
}
