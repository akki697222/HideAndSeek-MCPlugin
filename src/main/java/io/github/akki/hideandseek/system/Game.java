package io.github.akki.hideandseek.system;

import io.github.akki.hideandseek.system.map.MapManager;
import io.github.akki.hideandseek.system.tasks.Countdown;
import io.github.akki.hideandseek.system.tasks.GameTick;
import io.github.akki.hideandseek.utils.ConfigUtil;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Team;
import org.yaml.snakeyaml.util.ArrayStack;

import javax.annotation.Nonnull;
import java.util.*;

import static io.github.akki.hideandseek.HideandSeek.*;

public class Game {
    public static int countdown = config.getInt("game.countdown");
    public static boolean isGameStarted = false;
    public static BukkitTask gameTick;
    public static Player finalPlayer;
    public static List<Player> surivedHiders = new ArrayList<>();

    public static List<Player> nextHider = new ArrayList<>();
    public static List<Player> nextSeeker = new ArrayList<>();
    public static List<Player> nextSpectator = new ArrayList<>();

    public static Map<String, Object> currentMap;

    public static boolean isCountdown = false;

    private static int hiders = 0;
    public static enumMode currentMode = enumMode.normal;

    public static enumMode getMode() {
        return currentMode;
    }

    public static void setMode(enumMode mode) {
        currentMode = mode;
    }

    public static enumMode parseMode(String mode) {
        if (Objects.equals(mode, "normal")) {
            return enumMode.normal;
        } else if (Objects.equals(mode, "hard")) {
            return enumMode.hard;
        } else if (Objects.equals(mode, "chaos")) {
            return enumMode.chaos;
        } else {
            return null;
        }
    }

    public static int getHider() {
        int hid = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (isPlayersInTeam(player, "hider")) {
                hid++;
            }
        }
        return hid;
    }

    public static void addNextHider(Player player) {
        removeNextSeeker(player);
        removeNextSpectator(player);
        nextHider.add(player);
    }

    public static void addNextSeeker(Player player) {
        removeNextHider(player);
        removeNextSpectator(player);
        nextSeeker.add(player);
    }

    public static void addNextSpectator(Player player) {
        removeNextSeeker(player);
        removeNextHider(player);
        nextSpectator.add(player);
    }

    public static int getSeeker() {
        int hid = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (isPlayersInTeam(player, "seeker")) {
                hid++;
            }
        }
        return hid;
    }

    public static boolean checkWaiting() {
        int waitings = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (isPlayersInTeam(player, "waiting")) {
                waitings++;
            }
        }
        return waitings > 1;
    }

    public static void removeNextHider(Player player) {
        nextHider.remove(player);
    }

    public static void removeNextSeeker(Player player) {
        nextSeeker.remove(player);
    }

    public static void removeNextSpectator(Player player) {
        nextSpectator.remove(player);
    }

    public static List<Player> getNextHider() {
        return nextHider;
    }

    public static List<Player> getNextSeeker() {
        return nextSeeker;
    }

    public static List<Player> getNextSpectator() {
        return nextSpectator;
    }

    public static boolean randomizeTeams(int seekers) {
        Random random = new Random();
        List<Player> playerList = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if ((!isPlayersInTeam(player, "spectator") || !nextSpectator.contains(player) || !isPlayersInTeam(player, "visitor")) && isPlayersInTeam(player, "waiting")) {
                playerList.add(player);
            }
        }

        if (seekers >= playerList.size() || seekers < 1) {
            return false;
        }

        for (Player player : playerList) {
            if ((!isPlayersInTeam(player, "spectator") || !isPlayersInTeam(player, "visitor")) && isPlayersInTeam(player, "waiting")) {
                Team team = scoreboard.getEntryTeam(player.getName());
                if (team != null) {
                    team.removeEntry(player.getName());
                }
                player.setMaxHealth(config.getDouble("game.health.hider"));
                hider.addPlayer(player);
            }
        }

        Collections.shuffle(playerList, random);

        for (int i = 0; i < seekers; i++) {
            Player randomPlayer = playerList.get(i);
            randomPlayer.setMaxHealth(config.getDouble("game.health.seeker"));
            seeker.addPlayer(randomPlayer);
        }

        return true;
    }

    public static void startCountdown() {
        reloadAllConfig();
        if (currentMap == null) {
            throw(new NullPointerException("Invalid Map: Returned map is null."));
        }
        isCountdown = true;
        countdown = config.getInt("game.countdown");

        switch (currentMode) {
            case normal:
                Bukkit.broadcastMessage(ChatColor.BOLD + config.getString("message.mode.normal"));
                break;
            case hard:
                Bukkit.broadcastMessage(ChatColor.BOLD + config.getString("message.mode.hard"));
                break;
            case chaos:
                Bukkit.broadcastMessage(ChatColor.BOLD + config.getString("message.mode.chaos"));
                break;
            default:
                break;
        }

        Bukkit.broadcastMessage(ChatColor.GOLD + "[SYSTEM] " + ChatColor.RESET + config.getString("message.game.startCountdown"));
        Bukkit.broadcastMessage(ChatColor.GOLD + "[SYSTEM] " + ChatColor.RESET + String.format(config.getString("message.game.selectedMap"), currentMap.get("title")));
        Bukkit.broadcastMessage(ChatColor.GOLD + "[SYSTEM] " + ChatColor.RESET + String.format(config.getString("message.game.selectedDifficulty"), currentMode.toString()));
        Bukkit.broadcastMessage(ChatColor.GOLD + "[SYSTEM] " + ChatColor.RESET + String.format(config.getString("message.game.selectedMapCredit"), currentMap.get("credit")));

        for (Entity entity : Bukkit.getWorld("world").getEntities()) {
            if (entity instanceof Item) {
                entity.remove();
            }
        }

        mainBossBar.setVisible(false);
        countdownBossBar.setVisible(true);
        timerBossBar.setVisible(false);
        new Countdown().runTaskTimer(getPlugin(), 0L, 20L);

        int x = (int) currentMap.get("x");
        int y = (int) currentMap.get("y");
        int z = (int) currentMap.get("z");

        int time = (int) currentMap.get("time");

        Bukkit.getWorld("world").setTime(time);

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!isPlayersInTeam(player, "visitor")) {
                player.teleport(new Location(Bukkit.getWorld("world"), x, y, z));
            }
            if (!isPlayersInTeam(player, "spectator")) {
                player.setGameMode(GameMode.ADVENTURE);
            }
            if (isPlayersInTeam(player, "spectator")) {
                player.setGameMode(GameMode.SPECTATOR);
            }
            player.getInventory().clear();
            player.setAllowFlight(false);
            player.setFlying(false);
            battery.getScore(player).setScore(config.getInt("game.maxBattery"));
            if (isPlayersInTeam(player, "seeker")) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * config.getInt("game.countdown"), 255));
                ItemStack leatherChestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
                LeatherArmorMeta meta = (LeatherArmorMeta) leatherChestplate.getItemMeta();
                if (meta != null) {
                    meta.setColor(Color.RED);
                    meta.setDisplayName("鬼の服");
                    leatherChestplate.setItemMeta(meta);
                }
                player.getInventory().setChestplate(leatherChestplate);
                shopPoint.getScore(player).setScore(config.getInt("shop.seekerDefault"));
            } else if (isPlayersInTeam(player, "hider")) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, config.getInt("game.countdown"), 255));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * config.getInt("game.countdown"), 3));
                shopPoint.getScore(player).setScore(config.getInt("shop.hiderDefault"));
                hiders++;
            }
        }

        for (Player player : nextSpectator) {
            Team joinedTeam = scoreboard.getEntryTeam(player.getName());
            if (joinedTeam != null) {
                joinedTeam.removeEntry(player.getName());
            }

            player.setGameMode(GameMode.SPECTATOR);
            spectator.addPlayer(player);
        }
    }

    public static void startTimer() {
        mainBossBar.setVisible(false);
        countdownBossBar.setVisible(false);
        timerBossBar.setVisible(true);
        timer.resetTimer();
        timer.startTimer();
    }

    public static void stopTimer() {
        mainBossBar.setVisible(true);
        countdownBossBar.setVisible(false);
        timerBossBar.setVisible(false);
        timer.resetTimer();
        timer.stopTimer();
        timer.setCurrentTime(timer.getDefaultTime());
        setBossBarDefault();
    }

    public static void customGame() {
        for (Player player : nextHider) {
            Team joinedTeam = scoreboard.getEntryTeam(player.getName());
            if (joinedTeam != null) {
                joinedTeam.removeEntry(player.getName());
            }

            player.setMaxHealth(config.getDouble("game.health.hider"));
            hider.addPlayer(player);
        }

        for (Player player : nextSeeker) {
            Team joinedTeam = scoreboard.getEntryTeam(player.getName());
            if (joinedTeam != null) {
                joinedTeam.removeEntry(player.getName());
            }

            player.setMaxHealth(config.getDouble("game.health.seeker"));
            seeker.addPlayer(player);
        }

        for (Player player : nextSpectator) {
            Team joinedTeam = scoreboard.getEntryTeam(player.getName());
            if (joinedTeam != null) {
                joinedTeam.removeEntry(player.getName());
            }

            player.setGameMode(GameMode.SPECTATOR);
            spectator.addPlayer(player);
        }

        nextHider = new ArrayList<>();
        nextSeeker = new ArrayList<>();
        nextSpectator = new ArrayList<>();
    }

    public static void giveItems(Player player) {
        switch (currentMode) {
            case normal: {
                if (isPlayersInTeam(player, "seeker")) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * timer.getDefaultTime(), 2));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 20 * timer.getDefaultTime(), 255));
                    player.getInventory().addItem(new ItemStack(Material.NETHERITE_AXE, 1));
                    player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 64));
                } else if ((isPlayersInTeam(player, "hider"))) {
                    player.getInventory().addItem(new ItemStack(Material.BREAD, 16));
                }
                break;
            }
            case hard: {
                if (isPlayersInTeam(player, "seeker")) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * timer.getDefaultTime(), 3));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 20 * timer.getDefaultTime(), 255));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 20 * timer.getDefaultTime(), 3));
                    player.getInventory().addItem(new ItemStack(Material.NETHERITE_AXE, 1));
                    player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 64));
                    player.getInventory().addItem(new ItemStack(Material.BOW, 1));
                    player.getInventory().addItem(new ItemStack(Material.SPECTRAL_ARROW, 128));
                } else if ((isPlayersInTeam(player, "hider"))) {
                    player.getInventory().addItem(new ItemStack(Material.WOODEN_SWORD, 1));
                    player.getInventory().addItem(new ItemStack(Material.BREAD, 8));
                }
                break;
            }
            case chaos: {
                if (isPlayersInTeam(player, "seeker")) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * timer.getDefaultTime(), 3));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 20 * timer.getDefaultTime(), 255));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 20 * timer.getDefaultTime(), 3));
                    player.getInventory().addItem(new ItemStack(Material.NETHERITE_AXE, 1));
                    player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 64));
                } else if ((isPlayersInTeam(player, "hider"))) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * timer.getDefaultTime(), 3));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 20 * timer.getDefaultTime(), 255));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 20 * timer.getDefaultTime(), 3));
                    player.getInventory().addItem(new ItemStack(Material.NETHERITE_AXE, 1));
                    player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 64));
                }
                break;
            }
            default:
                break;
        }

        if (!isPlayersInTeam(player, "waiting") || !isPlayersInTeam(player, "visitor") || !isPlayersInTeam(player, "spectator")) {
            if (ConfigUtil.isEnabled(config, "item.flash.state")) {
                player.getInventory().addItem(GameItems.getItem(SpecialItems.FLASH_POTION));
            }

            if (ConfigUtil.isEnabled(config, "item.playersearcher.state") && isPlayersInTeam(player, "hider")) {
                player.getInventory().addItem(GameItems.getItem(SpecialItems.SEEKER_SEARCHER));
            }

            if (ConfigUtil.isEnabled(config, "item.battery_pack.state")) {
                player.getInventory().addItem(GameItems.getItem(SpecialItems.BATTERY_PACK));
            }

            if (ConfigUtil.isEnabled(config, "item.knockback_stick.state")) {
                player.getInventory().addItem(GameItems.getItem(SpecialItems.KNOCKBACK_STICK));
            }
        }
    }

    public static void startGame() {
        isGameStarted = true;
        isCountdown = false;
        Bukkit.broadcastMessage(ChatColor.GOLD + "[SYSTEM] " + ChatColor.RESET + config.getString("message.game.start"));
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 3.0f, 1.0f);
            player.getActivePotionEffects().forEach(effect ->
                    player.removePotionEffect(effect.getType())
            );
            giveItems(player);
        }
        startTimer();
        gameTick = new GameTick().runTaskTimer(getPlugin(), 0L, 5L);
    }

    public static void endGame(boolean isTimedUp, boolean winnedTeams, boolean isForceEnded) { //winnedTeams : Hider true Seeker false
        int dayTime = config.getInt("game.day");
        Bukkit.getWorld("world").setTime(dayTime);
        int time = timer.getCurrentTime();
        isGameStarted = false;
        if (gameTick != null) {
            gameTick.cancel();
        }
        stopTimer();

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setGameMode(GameMode.SPECTATOR);
            player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0F, 1.0F);
            player.getActivePotionEffects().forEach(effect ->
                    player.removePotionEffect(effect.getType())
            );
            player.setHealth(10);
            player.setFoodLevel(20);
            player.setSaturation(20);
            shopPoint.getScore(player).setScore(0);
        }
        if (isForceEnded) {
            Bukkit.broadcastMessage(ChatColor.GOLD + "[SYSTEM] " + ChatColor.RESET + config.getString("message.game.forceEnd"));
        } else teamProcess(winnedTeams, isTimedUp, timer.getDefaultTime() - time);

        Bukkit.getScheduler().runTaskLater(getPlugin(), Game::lobbyProcess, 20 * 5);
    }

    public static void stopGame() {
        endGame(false, false, true);
    }

    public static boolean isPlayersInTeam(Player player, String name) {
        Team playerTeam = scoreboard.getEntryTeam(player.getName());
        return playerTeam != null && playerTeam.getName().equalsIgnoreCase(name);
    }

    private static void teamProcess(boolean team, boolean timeUp, int endedTime) { //Hider true Seeker false
        String subTitle;
        if (team) {
            subTitle = ChatColor.GREEN + config.getString("message.game.hiderWin");
        } else {
            subTitle = ChatColor.RED + config.getString("message.game.seekerWin");
        }
        String title;
        if (timeUp) {
            title = config.getString("message.game.timeup");
        } else {
            title = config.getString("message.game.gameEnd");
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle(ChatColor.GOLD + title, subTitle, 1, 20 * 3, 20);
        }

        Player topKiller = null;
        int maxKills = -1;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (isPlayersInTeam(player, "seeker")) {
                int kills = player.getStatistic(Statistic.PLAYER_KILLS);
                if (kills > maxKills) {
                    maxKills = kills;
                    topKiller = player;
                }
            }
        }

        List<String> scoreBoard = new ArrayList<>();
        if (team) {
            if (finalPlayer != null && hiders == 1) {
                scoreBoard.add(ChatColor.GREEN + "- Hiders MVP -");
                scoreBoard.add(finalPlayer.getName() + config.getString("message.game.onepersonisenough"));
            } else {
                scoreBoard.add(ChatColor.GREEN + config.getString("message.game.survived"));
                for (Player player : surivedHiders) {
                    scoreBoard.add(player.getName());
                }
            }
        } else {
            scoreBoard.add(ChatColor.GREEN + "- Hiders MVP -");
            scoreBoard.add(finalPlayer.getName() + String.format(config.getString("message.game.survivedtimes"), endedTime));
        }
        if (topKiller != null) {
            if (topKiller.getStatistic(Statistic.PLAYER_KILLS) == hiders) {
                scoreBoard.add(ChatColor.RED + "- Seekers MVP -");
                scoreBoard.add(topKiller.getName() + config.getString("message.game.killedall"));
            } else {
                scoreBoard.add(ChatColor.RED + "- Seekers MVP -");
                scoreBoard.add(topKiller.getName() + String.format(config.getString("message.game.killedplayers"), topKiller.getStatistic(Statistic.PLAYER_KILLS)));
            }
        }
        for (String msg : scoreBoard) {
            Bukkit.broadcastMessage(msg);
        }
    }

    public static void finalItem(int hiderCount) {
        if (isFinal) {
            return;
        }
        isFinal = true;
        Bukkit.broadcastMessage(ChatColor.GOLD + "[SYSTEM] " + ChatColor.RESET + ChatColor.BOLD + config.getString("message.game.finalPlayer"));
        finalPlayer.getInventory().addItem(new ItemStack(Material.IRON_SWORD, 1));
        ItemStack flashItem = GameItems.getItem(SpecialItems.FLASH_POTION, 1);
        ItemStack stickItem = GameItems.getItem(SpecialItems.KNOCKBACK_STICK, 1);
        switch (currentMode) {
            case normal: {
                if (ConfigUtil.isEnabled(config, "item.flash.state")) {
                    finalPlayer.getInventory().addItem(flashItem);
                }
                break;
            }
            case hard: {
                if (ConfigUtil.isEnabled(config, "item.flash.state")) {
                    finalPlayer.getInventory().addItem(flashItem);
                    finalPlayer.getInventory().addItem(flashItem);
                }
                if (ConfigUtil.isEnabled(config, "item.knockback_stick.state")) {
                    finalPlayer.getInventory().addItem(stickItem);
                }
                break;
            }
            default:
                break;
        }
        if (ConfigUtil.isEnabled(config, "game.finalItem.totem")) {
            finalPlayer.getInventory().addItem(new ItemStack(Material.TOTEM_OF_UNDYING, 1));
        }
    }

    public static void lobbyProcess() {
        hiders = 0;
        isFinal = false;
        for (Player player : Bukkit.getOnlinePlayers()) {
            Team joinedTeam = scoreboard.getEntryTeam(player.getName());
            if (joinedTeam != null) {
                joinedTeam.removeEntry(player.getName());
            }
            visitor.addPlayer(player);
            player.getInventory().clear();
            player.setGameMode(GameMode.ADVENTURE);
            player.setStatistic(Statistic.PLAYER_KILLS, 0);
            player.teleport(new Location(Bukkit.getWorld("world"), config.getInt("game.lobbyPos.x"), config.getInt("game.lobbyPos.y"), config.getInt("game.lobbyPos.z")));
        }
    }

    public static boolean isFinal = false;

    public static void countdownTick() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (countdown <= 3) {
                player.playNote(player.getLocation(), Instrument.PLING, Note.natural(1, Note.Tone.C));
            } else {
                player.playNote(player.getLocation(), Instrument.STICKS, Note.natural(0, Note.Tone.F));
            }
        }
        countdownBossBar.setProgress(calcProgress(countdown, config.getInt("game.countdown")));
        countdownBossBar.setTitle(String.format(config.getString("message.game.countdown"), countdown));
    }

    public static boolean checkHiders() {
        int hiderCount = 0;
        surivedHiders = new ArrayList<>();
        Player processedPlayer = null;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (isPlayersInTeam(player, "hider")) {
                processedPlayer = player;
                surivedHiders.add(player);
                hiderCount++;
            }
        }
        if (hiderCount == 1) {
            finalPlayer = processedPlayer;
            finalItem(hiderCount);
        }
        return hiderCount != 0;
    }

    public static boolean checkSeekers() {
        int seekerCount = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (isPlayersInTeam(player, "seeker")) {
                seekerCount++;
            }
        }
        return seekerCount != 0;
    }

    public static void setBossBarTimer() {
        int min = (timer.getDefaultTime() % 3600) / 60;
        int sec = timer.getDefaultTime() % 60;
        timerBossBar.setTitle(ChatColor.GREEN + String.format(config.getString("message.game.timer"), min, sec));
        timerBossBar.setColor(BarColor.GREEN);
        timerBossBar.setProgress(0.0);
    }

    public static void setBossBarDefault() {
        mainBossBar.setVisible(true);
        mainBossBar.setTitle(ChatColor.GREEN + "- " + config.getString("message.game.title") + " -");
        mainBossBar.setColor(BarColor.GREEN);
        mainBossBar.setProgress(1.0);
        countdownBossBar.setVisible(false);
        countdownBossBar.setTitle(ChatColor.GOLD + String.format(config.getString("message.game.countdown"), countdown));
        countdownBossBar.setColor(BarColor.YELLOW);
        countdownBossBar.setProgress(1.0);
    }

    public static void updateBossBarTimer(int time, int startTime) {
        BarColor bcolor = BarColor.WHITE;
        ChatColor ccolor = ChatColor.WHITE;
        double progress = calcProgress(time, startTime);
        if (progress <= 0.5) {
            bcolor = BarColor.GREEN;
            ccolor = ChatColor.GREEN;
        } else if (progress <= 0.8) {
            bcolor = BarColor.YELLOW;
            ccolor = ChatColor.YELLOW;
        } else if (progress <= 1.0) {
            bcolor = BarColor.RED;
            ccolor = ChatColor.RED;
        }
        timerBossBar.setProgress(progress);
        timerBossBar.setColor(bcolor);
        int min = (time % 3600) / 60;
        int sec = time % 60;
        timerBossBar.setTitle(ccolor + String.format(config.getString("message.game.timer"), min, sec));
    }

    private static double calcProgress(int value, int maxValue) {
        return (double) (maxValue - value) / maxValue;
    }

    public static void initPlayer(@Nonnull Player player) {
        mainBossBar.addPlayer(player);
        timerBossBar.addPlayer(player);
        countdownBossBar.addPlayer(player);
        battery.getScore(player).setScore(config.getInt("game.maxBattery"));
        shopPoint.getScore(player).setScore(0);
        player.teleport(new Location(Bukkit.getWorld("world"), config.getInt("game.lobbyPos.x"), config.getInt("game.lobbyPos.y"), config.getInt("game.lobbyPos.z")));
        if (isGameStarted) {
            if (isPlayersInTeam(player, "seeker")) {
                seeker.addPlayer(player);
                player.setGameMode(GameMode.ADVENTURE);
            } else {
                spectator.addPlayer(player);
                player.setGameMode(GameMode.SPECTATOR);

                int x = (int) currentMap.get("x");
                int y = (int) currentMap.get("y");
                int z = (int) currentMap.get("z");

                player.teleport(new Location(Bukkit.getWorld("world"), x, y, z));
            }
        } else {
            visitor.addPlayer(player);
            player.setGameMode(GameMode.ADVENTURE);
        }
    }

    public enum enumMode {
        chaos,
        hard,
        normal,
    }
}
