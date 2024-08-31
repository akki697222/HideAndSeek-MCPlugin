package io.github.akki.hideandseek;

import io.github.akki.hideandseek.commands.*;
import io.github.akki.hideandseek.system.Game;
import io.github.akki.hideandseek.system.Timer;
import io.github.akki.hideandseek.system.tasks.PrepareCountdown;
import io.github.akki.hideandseek.system.tasks.ScoreBoardUpdater;
import io.github.akki.hideandseek.system.tasks.TimerUpdater;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.*;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.logging.Logger;

public final class HideandSeek extends JavaPlugin {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(HideandSeek.class);
    public static Plugin hideandseekPlugin;
    public static final Logger logger = Logger.getLogger("Hideandseek");
    public static Timer timer;
    public static FileConfiguration config;
    public static FileConfiguration mapConfig;

    public static File mapConfFile;

    public static BossBar mainBossBar;
    public static BossBar timerBossBar;
    public static BossBar countdownBossBar;

    public static Team hider;
    public static Team seeker;
    public static Team visitor;
    public static Team dead;
    public static Team spectator;
    public static Team waiting;

    public static Objective battery;
    public static Objective shopPoint;
    public static Objective gameView;

    public static Scoreboard scoreboard;

    public static void reloadAllConfig() {
        getPlugin().reloadConfig();
        try {
            mapConfig.load(mapConfFile);
        } catch (Exception e) {
            logger.warning("Failed to reload config.");
            logger.warning(e.toString());
        }
    }

    @Override
    public void onDisable() {
        logger.warning(config.getString("message.plugin.disable"));
    }

    public static Plugin getPlugin() {
        return hideandseekPlugin;
    }

    @Override
    public void onEnable() {
        //initialize config
        saveDefaultConfig();
        config = getConfig();

        mapConfFile = new File(getDataFolder(), "maps.yml");

        if (!mapConfFile.exists()) {
            mapConfFile.getParentFile().mkdirs();
            saveResource("maps.yml", false);
        }

        logger.info(config.getString("message.plugin.initialize"));
        logger.info("Hide-and-seek " + this.getPluginMeta().getVersion() + "\n2024 akki697222 / MIT Licence");

        hideandseekPlugin = this;

        timer = new Timer();
        mainBossBar = Bukkit.createBossBar(ChatColor.GOLD + "INITIALIZING...", BarColor.WHITE, BarStyle.SOLID);
        mainBossBar.setVisible(true);
        timerBossBar = Bukkit.createBossBar(ChatColor.RED + "TIMER NOT INITIALIZED", BarColor.RED, BarStyle.SOLID);
        timerBossBar.setVisible(false);
        countdownBossBar = Bukkit.createBossBar(ChatColor.RED + "COUNTDOWN NOT INITIALIZED", BarColor.RED, BarStyle.SOLID);
        countdownBossBar.setVisible(false);


        mapConfig = YamlConfiguration.loadConfiguration(mapConfFile);

        Bukkit.getScheduler().runTask(this, () -> {
            ScoreboardManager manager = Bukkit.getScoreboardManager();
            scoreboard = manager.getMainScoreboard();
            World world = Bukkit.getWorld("world");

            world.setGameRule(GameRule.FALL_DAMAGE, false);
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
            world.setGameRule(GameRule.SPAWN_RADIUS, 0);
            world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);
            world.setGameRule(GameRule.MOB_GRIEFING, false);
            world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
            world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
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
            if (scoreboard.getTeam("waiting") == null) {
                waiting = scoreboard.registerNewTeam("waiting");
            } else {
                waiting = scoreboard.getTeam("waiting");
            }
            if (scoreboard.getObjective("battery") == null) {
                battery = scoreboard.registerNewObjective("battery", "dummy");
            } else {
                battery = scoreboard.getObjective("battery");
            }
            if (scoreboard.getObjective("gameView") == null) {
                gameView = scoreboard.registerNewObjective("gameView", "dummy", ChatColor.GREEN + "    " + config.getString("message.game.title") + "    ");
            } else {
                gameView = scoreboard.getObjective("gameView");
            }
            if (scoreboard.getObjective("shopPoint") == null) {
                shopPoint = scoreboard.registerNewObjective("shopPoint", "dummy");
            } else {
                shopPoint = scoreboard.getObjective("shopPoint");
            }

            hider.setPrefix(ChatColor.GREEN + "[" + config.getString("message.team.hiderPrefix") + "] ");
            seeker.setPrefix(ChatColor.RED + "[" + config.getString("message.team.seekerPrefix") + "] ");
            visitor.setPrefix(ChatColor.BLUE + "[" + config.getString("message.team.visitorPrefix") + "] ");
            dead.setPrefix(ChatColor.DARK_RED + "[" + config.getString("message.team.deadPrefix") + "] ");
            spectator.setPrefix(ChatColor.DARK_GRAY + "[" + config.getString("message.team.specPrefix") + "] ");
            waiting.setPrefix(ChatColor.YELLOW + "[" + config.getString("message.team.waitingPrefix") + "] ");

            hider.setNameTagVisibility(NameTagVisibility.HIDE_FOR_OTHER_TEAMS);
            seeker.setNameTagVisibility(NameTagVisibility.HIDE_FOR_OTHER_TEAMS);

            hider.setColor(ChatColor.GREEN);
            seeker.setColor(ChatColor.RED);

            hider.setAllowFriendlyFire(false);
            seeker.setAllowFriendlyFire(false);
            visitor.setAllowFriendlyFire(false);
            dead.setAllowFriendlyFire(false);
            spectator.setAllowFriendlyFire(false);
            waiting.setAllowFriendlyFire(false);
        });

        new TimerUpdater().runTaskTimer(this, 0L, 20L);
        new ScoreBoardUpdater().runTaskTimer(this, 0L, 10L);
        new PrepareCountdown().runTaskTimer(this, 0L, 20L);
        getServer().getPluginManager().registerEvents(new HideandSeekEventListener(), this);

        this.getCommand("timer").setExecutor(new TimerCommand());
        this.getCommand("hideandseek").setExecutor(new HideandSeekCommand());
        this.getCommand("hider").setExecutor(new HiderCommand());
        this.getCommand("seeker").setExecutor(new SeekerCommand());
        this.getCommand("spectate").setExecutor(new SpectateCommand());
        this.getCommand("map").setExecutor(new MapCommand());
        this.getCommand("eventtester").setExecutor(new EventTesterCommand());
        this.getCommand("spectator").setExecutor(new SpectatorCommand());
        this.getCommand("ready").setExecutor(new ReadyCommand());
        this.getCommand("shop").setExecutor(new ShopCommand());
        this.getCommand("fly").setExecutor(new FlyCommand());

        this.configCheck();

        Game.setBossBarDefault();
        Game.setBossBarTimer();

        logger.info(config.getString("message.plugin.enable"));
    }

    public void configCheck() {
        if (config.getInt("lobby.minStart") <= 1) {
            new InvalidConfigurationException("lobby.minStart.notenough"); //TODO メッセージ
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }
}
