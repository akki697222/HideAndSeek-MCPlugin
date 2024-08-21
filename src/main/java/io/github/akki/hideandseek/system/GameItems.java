package io.github.akki.hideandseek.system;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;

import static io.github.akki.hideandseek.HideandSeek.config;
import static io.github.akki.hideandseek.HideandSeek.logger;

public class GameItems {
    public static ItemStack getItem(SpecialItems items) {
        return getItem(items, 1);
    }

    public static ItemStack getItem(SpecialItems items, int amount) {
        if (amount <= 0) {
            logger.warning(""); //TODO メッセージ
            amount = 1;
        }

        switch (items) {
            case SEEKER_SEARCHER: {
                ItemStack item = new ItemStack(Material.RECOVERY_COMPASS, amount);
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    meta.setDisplayName("Seeker Searcher");
                    meta.setLore(Collections.singletonList(ChatColor.GOLD + "近くに鬼がいるかどうか、見てみよう..."));

                    item.setItemMeta(meta);
                }
                return item;
            }
            case KNOCKBACK_STICK: {
                ItemStack item = new ItemStack(Material.STICK, amount);
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    meta.setDisplayName("§a鬼の金棒");
                    item.setItemMeta(meta);
                }
                item.addUnsafeEnchantment(Enchantment.KNOCKBACK, config.getInt("item.knockback_stick.level"));
                return item;
            }
            case FLASH_POTION: {
                ItemStack item = new ItemStack(Material.SPLASH_POTION, amount);
                PotionMeta potionMeta = (PotionMeta) item.getItemMeta();

                if (potionMeta != null) {
                    potionMeta.setDisplayName("フラッシュポーション");
                    potionMeta.setColor(Color.WHITE);
                    potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * config.getInt("item.flash.duration"), 255), true);
                    potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.SLOW, 20 * config.getInt("item.flash.duration"), 2), true);
                    item.setItemMeta(potionMeta);
                }
                return item;
            }
            case BATTERY_PACK: {
                ItemStack item = new ItemStack(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE, amount);
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    meta.setDisplayName("バッテリーパック");
                    meta.setLore(Collections.singletonList(ChatColor.GOLD + "バッテリーを全回復します。"));

                    item.setItemMeta(meta);
                }
                return item;
            }
            default:
                ItemStack item = new ItemStack(Material.BARRIER, 1);
                ItemMeta itemMeta = item.getItemMeta();
                if (itemMeta != null) {
                    itemMeta.setDisplayName("§cERROR: アイテム '" + items + "' がスローされました。アイテムの処理を忘れていませんか？");
                    item.setItemMeta(itemMeta);
                }
                return item;
        }
    }
}
