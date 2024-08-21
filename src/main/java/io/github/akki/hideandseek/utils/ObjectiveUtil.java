package io.github.akki.hideandseek.utils;

import org.bukkit.scoreboard.Objective;


public class ObjectiveUtil {
    public static void removeEntry(Objective objective) {
        for (String entry : objective.getScoreboard().getEntries()) {
            if (objective.getScore(entry).isScoreSet()) {
                objective.getScoreboard().resetScores(entry);
            }
        }
    }
}
