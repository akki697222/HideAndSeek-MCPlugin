package io.github.akki.hideandseek;

import io.github.akki.hideandseek.commands.*;
import io.github.akki.hideandseek.system.Game;
import io.github.akki.hideandseek.system.GameTimer;
import io.github.akki.hideandseek.system.tasks.GameTimerUpdater;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.logging.Logger;

public final class HideandSeek extends JavaPlugin {
    public static Plugin hideandseekPlugin;
    public static final Logger logger = Logger.getLogger("Hide'n'seek");
    public static GameTimer timer;
    public static FileConfiguration config;

    public static BossBar mainBossBar;
    public static BossBar timerBossBar;
    public static BossBar countdownBossBar;

    public static Team hider;
    public static Team seeker;
    public static Team visitor;
    public static Team dead;
    public static Team spectator;

    public static Scoreboard scoreboard;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        config = getConfig();

        logger.info(config.getString("message.plugin.enable"));

        timer = new GameTimer();
        mainBossBar = Bukkit.createBossBar(ChatColor.GOLD + "INITIALIZING...", BarColor.WHITE, BarStyle.SOLID);
        mainBossBar.setVisible(true);
        timerBossBar = Bukkit.createBossBar(ChatColor.RED + "TIMER NOT INITIALIZED", BarColor.RED, BarStyle.SOLID);
        timerBossBar.setVisible(false);
        countdownBossBar = Bukkit.createBossBar(ChatColor.GOLD + "COUNTDOWN NOT INITIALIZED", BarColor.YELLOW, BarStyle.SOLID);
        countdownBossBar.setVisible(false);

        hideandseekPlugin = this;

        Bukkit.getScheduler().runTask(this, () -> {
            ScoreboardManager manager = Bukkit.getScoreboardManager();
            scoreboard = manager.getMainScoreboard();
            World world = Bukkit.getWorld("world");

            world.setGameRule(GameRule.FALL_DAMAGE, false);
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
            world.setGameRule(GameRule.SPAWN_RADIUS, 0);
            world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);
            world.setDifficulty(Difficulty.NORMAL);

            if (scoreboard.getTeam("hider") == null) {
                hider = scoreboard.registerNewTeam("hider");
            } else {
                hider = scoreboard.getTeam("hider");
            }
            if (scoreboard.getTeam("seeker") == null) {
                seeker = scoreboard.registerNewTeam("seeker");
            } else {
                seeker = scoreboard.getTeam("seeker");
            }
            if (scoreboard.getTeam("visitor") == null) {
                visitor = scoreboard.registerNewTeam("visitor");
            } else {
                visitor = scoreboard.getTeam("visitor");
            }
            if (scoreboard.getTeam("dead") == null) {
                dead = scoreboard.registerNewTeam("dead");
            } else {
                dead = scoreboard.getTeam("dead");
            }
            if (scoreboard.getTeam("spectator") == null) {
                spectator = scoreboard.registerNewTeam("spectator");
            } else {
                spectator = scoreboard.getTeam("spectator");
            }
            hider.setPrefix(ChatColor.GREEN + "[" + config.getString("message.team.hiderPrefix") + "]");
            seeker.setPrefix(ChatColor.RED + "[" + config.getString("message.team.seekerPrefix") + "]");
            visitor.setPrefix(ChatColor.BLUE + "[" + config.getString("message.team.visitorPrefix") + "]");
            dead.setPrefix(ChatColor.DARK_RED + "[" + config.getString("message.team.deadPrefix") + "]");
            spectator.setPrefix(ChatColor.DARK_GRAY + "[" + config.getString("message.team.specPrefix") + "]");
            hider.setNameTagVisibility(NameTagVisibility.HIDE_FOR_OTHER_TEAMS);
            seeker.setNameTagVisibility(NameTagVisibility.HIDE_FOR_OTHER_TEAMS);
            hider.setAllowFriendlyFire(false);
            seeker.setAllowFriendlyFire(false);
            visitor.setAllowFriendlyFire(false);
            dead.setAllowFriendlyFire(false);
            spectator.setAllowFriendlyFire(false);
        });

        new GameTimerUpdater().runTaskTimer(this, 0L, 20L);
        getServer().getPluginManager().registerEvents(new HideandSeekEventListener(), this);

        this.getCommand("timer").setExecutor(new TimerCommand());
        this.getCommand("hideandseek").setExecutor(new HideandSeekCommand());
        this.getCommand("hider").setExecutor(new HiderCommand());
        this.getCommand("seeker").setExecutor(new SeekerCommand());
        this.getCommand("spectate").setExecutor(new SpectateCommand());

        Game.setBossBarDefault();
        Game.setBossBarTimer();
    }

    @Override
    public void onDisable() {
        logger.warning(config.getString("message.plugin.disable"));
    }

    public static Plugin getPlugin() {
        return hideandseekPlugin;
    }
}
