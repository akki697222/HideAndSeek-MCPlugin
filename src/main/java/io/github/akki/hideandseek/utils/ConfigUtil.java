package io.github.akki.hideandseek.utils;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.Objects;

public class ConfigUtil {
    public static boolean isEnabled(FileConfiguration config, String path) {
        return Objects.equals(config.getString(path), "enable");
    }

    public static boolean isDisabled(FileConfiguration config, String path) {
        return Objects.equals(config.getString(path), "disable");
    }
}
