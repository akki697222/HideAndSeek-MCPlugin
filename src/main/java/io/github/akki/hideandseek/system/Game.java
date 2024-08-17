package io.github.akki.hideandseek.system;

import io.github.akki.hideandseek.system.mapsystem.MapManager;
import io.github.akki.hideandseek.system.tasks.GameCountdown;
import io.github.akki.hideandseek.system.tasks.GameTick;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Team;

import javax.annotation.Nonnull;

import java.util.*;

import static io.github.akki.hideandseek.HideandSeek.*;

public class Game {
    public static int countdown = config.getInt("game.countdown");
    public static boolean isGameStarted = false;
    public static BukkitTask gameTick;
    public static Player finalPlayer;
    public static List<Player> surivedHiders = new ArrayList<>();

    public static ItemStack flashPotion;
    public static ItemStack demonStick;

    public static List<Player> nextHider = new ArrayList<>();
    public static List<Player> nextSeeker = new ArrayList<>();
    public static List<Player> nextSpectator = new ArrayList<>();

    public static boolean isCountdown = false;

    private static int hiders = 0;

    public static void initItem() {
        flashPotion = new ItemStack(Material.SPLASH_POTION, 1);
        PotionMeta potionMeta = (PotionMeta) flashPotion.getItemMeta();

        if (potionMeta != null) {
            potionMeta.setDisplayName("フラッシュポーション");
            potionMeta.setColor(Color.WHITE);
            potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 10, 255), true);
            potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 10, 2), true);
            flashPotion.setItemMeta(potionMeta);
        }

        demonStick = new ItemStack(Material.STICK, 1);

        ItemMeta meta = demonStick.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§a鬼の金棒");
            demonStick.setItemMeta(meta);
        }

        demonStick.addUnsafeEnchantment(Enchantment.KNOCKBACK, 10);
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

        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        List<Player> playerList = new ArrayList<>(onlinePlayers);

        if (seekers >= playerList.size() || seekers < 1) {
            return false;
        }

        for (Player player : playerList) {
            if (!isPlayersInTeam(player, "spectator")) {
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

    public static void startCountdown() {
        Map<String, Object> map = MapManager.selectRandomMap();
        if (map == null) {
            throw(new NullPointerException("Invalid Map: Returned map is null."));
        }
        isCountdown = true;
        countdown = config.getInt("game.countdown");
        Bukkit.broadcastMessage(ChatColor.GOLD + config.getString("message.game.startCountdown"));
        Bukkit.broadcastMessage(ChatColor.GOLD + String.format(config.getString("message.game.selectedMap"), map.get("title")));
        mainBossBar.setVisible(false);
        countdownBossBar.setVisible(true);
        timerBossBar.setVisible(false);
        new GameCountdown().runTaskTimer(getPlugin(), 0L, 20L);

        int x = (int) map.get("x");
        int y = (int) map.get("y");
        int z = (int) map.get("z");

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.teleport(new Location(Bukkit.getWorld("world"), x, y, z));
            if (!isPlayersInTeam(player, "spectator")) {
                player.setGameMode(GameMode.ADVENTURE);
            }
            player.getInventory().clear();
            if (isPlayersInTeam(player, "seeker")) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 15, 255));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 15, 255));
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 20 * 15, 238));
            } else if (isPlayersInTeam(player, "hider")) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 15, 3));
                hiders++;
            }
        }
    }

    public static void startGame() {
        isGameStarted = true;
        isCountdown = false;
        Bukkit.broadcastMessage(ChatColor.GOLD + config.getString("message.game.start"));
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

    public static void giveItems(Player player) {
        switch (Bukkit.getWorld("world").getDifficulty()) {
            case PEACEFUL: {
                if (isPlayersInTeam(player, "seeker")) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * timer.getDefaultTime(), 2));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * timer.getDefaultTime(), 255));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 20 * timer.getDefaultTime(), 255));
                }
                break;
            }
            case EASY: {
                if (isPlayersInTeam(player, "seeker")) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * timer.getDefaultTime(), 1));
                    player.getInventory().addItem(new ItemStack(Material.DIAMOND_AXE, 1));
                    player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 16));
                } else if ((isPlayersInTeam(player, "hider"))) {
                    player.getInventory().addItem(new ItemStack(Material.BREAD, 64));
                }
                break;
            }
            case NORMAL: {
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
            case HARD: {
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
            default:
                break;
        }

        if (Objects.equals(config.getString("item.flash"), "enable")) {
            player.getInventory().addItem(flashPotion);
        }
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

    public static void endGame(boolean isTimedUp, boolean winnedTeams, boolean isForceEnded) { //winnedTeams : Hider true Seeker false
        int time = timer.getCurrentTime();
        isGameStarted = false;
        if (gameTick != null) {
            gameTick.cancel();
        }
        stopTimer();

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setGameMode(GameMode.SPECTATOR);
            player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_DEATH, 3.0f, 1.0f);
            player.getActivePotionEffects().forEach(effect ->
                    player.removePotionEffect(effect.getType())
            );
            player.setHealth(player.getMaxHealth());
            player.setFoodLevel(20);
            player.setSaturation(20);
        }
        if (isForceEnded) {
            Bukkit.broadcastMessage(config.getString("message.game.forceEnd"));
        } else teamProcess(winnedTeams, isTimedUp, timer.getDefaultTime() - time);

        Bukkit.getScheduler().runTaskLater(getPlugin(), Game::lobbyProcess, 20 * 5);
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

    public static void finalItem(int hiderCount) {
        if (isFinal) {
            return;
        }
        isFinal = true;
        Bukkit.broadcastMessage(config.getString("message.game.finalPlayer"));
        finalPlayer.getInventory().addItem(new ItemStack(Material.IRON_SWORD, 1));
        switch (Bukkit.getWorld("world").getDifficulty()) {
            case EASY: {
                finalPlayer.getInventory().addItem(flashPotion);
                break;
            }
            case NORMAL: {
                finalPlayer.getInventory().addItem(flashPotion);
                finalPlayer.getInventory().addItem(demonStick);
                break;
            }
            case HARD: {
                finalPlayer.getInventory().addItem(flashPotion);
                finalPlayer.getInventory().addItem(flashPotion);
                finalPlayer.getInventory().addItem(demonStick);
                finalPlayer.getInventory().addItem(new ItemStack(Material.TOTEM_OF_UNDYING, 1));
                break;
            }
            default:
                break;
        }
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

    public static void countdownTick() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playNote(player.getLocation(), Instrument.PLING, Note.natural(1, Note.Tone.C));
        }
        countdownBossBar.setProgress(calcProgress(countdown, config.getInt("game.countdown")));
        countdownBossBar.setTitle(ChatColor.GOLD + String.format(config.getString("message.game.countdown"), countdown));
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

    public static void setBossBarTimer() {
        timerBossBar.setTitle(ChatColor.GREEN + String.format(config.getString("message.game.timer"), timer.getDefaultTime()));
        timerBossBar.setColor(BarColor.GREEN);
        timerBossBar.setProgress(0.0);
    }

    private static double calcProgress(int value, int maxValue) {
        return (double) (maxValue - value) / maxValue;
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
        timerBossBar.setTitle(ccolor + String.format(config.getString("message.game.timer"), time));
    }

    public static void initPlayer(@Nonnull Player player) {
        mainBossBar.addPlayer(player);
        timerBossBar.addPlayer(player);
        countdownBossBar.addPlayer(player);
        if (isGameStarted) {
            if (isPlayersInTeam(player, "seeker")) {
                seeker.addPlayer(player);
                player.setGameMode(GameMode.ADVENTURE);
            } else {
                spectator.addPlayer(player);
                player.setGameMode(GameMode.SPECTATOR);
                player.teleport(new Location(Bukkit.getWorld("world"), config.getInt("game.lobbyPos.x"), config.getInt("game.lobbyPos.y"), config.getInt("game.lobbyPos.z")));
            }
        } else {
            visitor.addPlayer(player);
            player.setGameMode(GameMode.ADVENTURE);
            player.teleport(new Location(Bukkit.getWorld("world"), config.getInt("game.lobbyPos.x"), config.getInt("game.lobbyPos.y"), config.getInt("game.lobbyPos.z")));
        }
    }
}
