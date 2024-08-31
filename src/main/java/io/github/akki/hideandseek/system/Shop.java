package io.github.akki.hideandseek.system;

import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import io.github.akki.hideandseek.HideandSeek;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static io.github.akki.hideandseek.HideandSeek.config;
import static io.github.akki.hideandseek.HideandSeek.shopPoint;

public class Shop {
    public static String[] shopUI = {
            "abcdef   ",
            "         ",
            "        0"
    };

    public static void openShop(Player player) {
        InventoryGui gui = new InventoryGui(HideandSeek.getPlugin(), null, config.getString("message.shop.title"), shopUI);
        gui.setFiller(new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1));
        gui.addElement(new StaticGuiElement('a',
                GameItems.getItem(SpecialItems.FLASH_POTION),
                1,
                click -> {
                    buyEvent(player, GameItems.getItem(SpecialItems.FLASH_POTION), config.getInt("shop.amount.flash"));
                    return true;
                },
                GameItems.getItem(SpecialItems.FLASH_POTION).getItemMeta().getDisplayName(),
                ChatColor.GRAY + "必要ポイント: " + config.getInt("shop.amount.flash")
        ));
        gui.addElement(new StaticGuiElement('b',
                GameItems.getItem(SpecialItems.SPEEDUP_POTION),
                1,
                click -> {
                    buyEvent(player, GameItems.getItem(SpecialItems.SPEEDUP_POTION), config.getInt("shop.amount.speed_potion"));
                    return true;
                },
                GameItems.getItem(SpecialItems.SPEEDUP_POTION).getItemMeta().getDisplayName(),
                ChatColor.GRAY + "必要ポイント: " + config.getInt("shop.amount.speed_potion")
        ));
        gui.addElement(new StaticGuiElement('c',
                GameItems.getItem(SpecialItems.JUMP_POTION),
                1,
                click -> {
                    buyEvent(player, GameItems.getItem(SpecialItems.JUMP_POTION), config.getInt("shop.amount.jump_potion"));
                    return true;
                },
                GameItems.getItem(SpecialItems.JUMP_POTION).getItemMeta().getDisplayName(),
                ChatColor.GRAY + "必要ポイント: " + config.getInt("shop.amount.jump_potion")
        ));
        gui.addElement(new StaticGuiElement('d',
                GameItems.getItem(SpecialItems.SEEKER_SEARCHER),
                1,
                click -> {
                    buyEvent(player, GameItems.getItem(SpecialItems.SEEKER_SEARCHER), config.getInt("shop.amount.playersearcher"));
                    return true;
                },
                GameItems.getItem(SpecialItems.SEEKER_SEARCHER).getItemMeta().getDisplayName(),
                ChatColor.GRAY + "必要ポイント: " + config.getInt("shop.amount.playersearcher")
        ));
        gui.addElement(new StaticGuiElement('e',
                GameItems.getItem(SpecialItems.KNOCKBACK_STICK),
                1,
                click -> {
                    buyEvent(player, GameItems.getItem(SpecialItems.KNOCKBACK_STICK), config.getInt("shop.amount.knockback_stick"));
                    return true;
                },
                GameItems.getItem(SpecialItems.KNOCKBACK_STICK).getItemMeta().getDisplayName(),
                ChatColor.GRAY + "必要ポイント: " + config.getInt("shop.amount.knockback_stick")
        ));
        gui.addElement(new StaticGuiElement('f',
                GameItems.getItem(SpecialItems.BATTERY_PACK),
                1,
                click -> {
                    buyEvent(player, GameItems.getItem(SpecialItems.BATTERY_PACK), config.getInt("shop.amount.battery_pack"));
                    return true;
                },
                GameItems.getItem(SpecialItems.BATTERY_PACK).getItemMeta().getDisplayName(),
                ChatColor.GRAY + "必要ポイント: " + config.getInt("shop.amount.battery_pack")
        ));
        gui.addElement(new StaticGuiElement('0',
                new ItemStack(Material.DIAMOND, 1),
                1,
                click -> true,
                "あなたのポイント: " + shopPoint.getScore(player).getScore()
        ));
        gui.show(player);
    }

    private static void buyEvent(Player player, ItemStack item, int requiredPoints) {
        int point = shopPoint.getScore(player).getScore();
        if (point < requiredPoints) {
            player.sendMessage(ChatColor.GREEN + "[SHOP] " + ChatColor.RESET + ChatColor.BOLD + config.getString("message.shop.pointNotEnough"));
        } else if (point >= requiredPoints) {
            shopPoint.getScore(player).setScore(point - requiredPoints);
            player.getInventory().addItem(item);
            player.sendMessage(ChatColor.GREEN + "[SHOP] " + ChatColor.RESET + ChatColor.BOLD + String.format(config.getString("message.shop.buy"), item.getItemMeta().getDisplayName()));
        }
    }
}
