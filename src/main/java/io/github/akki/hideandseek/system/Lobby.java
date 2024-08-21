package io.github.akki.hideandseek.system;

import org.bukkit.Bukkit;

import java.util.Random;

import static io.github.akki.hideandseek.HideandSeek.config;

public class Lobby {
    public static int gameCountdown = config.getInt("lobby.startGame");
    public static int waitingSize = 0;

    public static void startGame() {
        int total = config.getInt("lobby.seekerRatio") + config.getInt("lobby.hiderRatio");
        int seeker = (int) Math.round((double) Bukkit.getOnlinePlayers().size() * config.getInt("lobby.seekerRatio") / total);
        int random = new Random().nextInt(Game.enumMode.values().length);
        Game.setMode(Game.enumMode.values()[random]);
        if (seeker <= 0) {
            seeker = 1;
        }
        if (Game.randomizeTeams(seeker)) {
            Game.startCountdown();
        }
    }
}
