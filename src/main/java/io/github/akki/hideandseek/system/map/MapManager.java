package io.github.akki.hideandseek.system.map;

import io.github.akki.hideandseek.system.Game;
import org.bukkit.configuration.ConfigurationSection;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static io.github.akki.hideandseek.HideandSeek.*;

public class MapManager {
    public static void addMap(String name, String title, int spawnX, int spawnY, int spawnZ, int time, String mode) {
        mapConfig.set("maps." + name + ".title", title);
        mapConfig.set("maps." + name + ".x", spawnX);
        mapConfig.set("maps." + name + ".y", spawnY);
        mapConfig.set("maps." + name + ".z", spawnZ);
        mapConfig.set("maps." + name + ".time", time);
        mapConfig.set("maps." + name + ".mode", mode);
        mapConfig.set("maps." + name + ".credit", "NO PERSON");

        try {
            mapConfig.save(mapConfFile);
        } catch (IOException e) {
            logger.warning("Failed to save config\n" + e);
        }

        try {
            mapConfig.load(mapConfFile);
        } catch (Exception e) {
            logger.warning("Failed to load config\n" + e);
        }
    }

    public static Map<String, Object> selectRandomMap() {
        try {
            mapConfig.load(mapConfFile);
        } catch (Exception e) {
            logger.warning("Failed to load config\n" + e);
        }
        ConfigurationSection mapsSection = mapConfig.getConfigurationSection("maps");
        if (mapsSection != null) {
            Set<String> mapKeys = mapsSection.getKeys(false);
            if (!mapKeys.isEmpty()) {
                String[] keysArray = mapKeys.toArray(new String[0]);
                for (String key : keysArray) {
                    logger.info(key);
                }
                String randomKey = keysArray[new Random().nextInt(keysArray.length)];

                ConfigurationSection selectedMap = mapsSection.getConfigurationSection(randomKey);

                if (selectedMap != null) {
                    Map<String, Object> mapInfo = new HashMap<>();
                    mapInfo.put("title", selectedMap.getString("title"));
                    mapInfo.put("x", selectedMap.getInt("x"));
                    mapInfo.put("y", selectedMap.getInt("y"));
                    mapInfo.put("z", selectedMap.getInt("z"));

                    mapInfo.put("time", selectedMap.getInt("time"));
                    mapInfo.put("mode", selectedMap.getString("mode"));
                    mapInfo.put("credit", selectedMap.getString("credit"));

                    if (Game.parseMode(selectedMap.getString("mode")) != Game.getMode()) {
                        return selectRandomMap();
                    }

                    return mapInfo;
                }
                logger.warning("selected map is null");
            }
            logger.warning("map key is null");
        }
        logger.warning("maps section is null");
        return null;
    }
}
