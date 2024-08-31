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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
                List<String> lores = new ArrayList<>(Arrays.asList(ChatColor.GRAY + "近くにプレイヤーがいるかどうか、見てみよう...", ChatColor.GRAY + "鬼の場合は逃げ、逃げの場合は鬼を、設定された半径の中で探索します。バッテリーを1消費します。"));
                if (meta != null) {
                    meta.setDisplayName("Player Searcher");
                    meta.setLore(lores);

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
            case JUMP_POTION: {
                ItemStack item = new ItemStack(Material.SPLASH_POTION, amount);
                PotionMeta potionMeta = (PotionMeta) item.getItemMeta();

                if (potionMeta != null) {
                    potionMeta.setDisplayName("レッドブル");
                    potionMeta.setLore(Collections.singletonList("翼を授ける～"));
                    potionMeta.setColor(Color.RED);
                    potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.JUMP, 20 * config.getInt("item.jump_potion.duration"), 3), true);
                    item.setItemMeta(potionMeta);
                }
                return item;
            }
            case SPEEDUP_POTION: {
                ItemStack item = new ItemStack(Material.SPLASH_POTION, amount);
                PotionMeta potionMeta = (PotionMeta) item.getItemMeta();

                if (potionMeta != null) {
                    potionMeta.setDisplayName("ドーピング");
                    potionMeta.setLore(Collections.singletonList("走れ！もっと早く！"));
                    potionMeta.setColor(Color.BLUE);
                    potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 20 * config.getInt("item.speed_potion.duration"), 3), true);
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
