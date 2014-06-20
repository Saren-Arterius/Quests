package me.blackvein.quests.util;

import java.util.LinkedList;
import java.util.Map.Entry;

import me.blackvein.quests.Quester;
import me.blackvein.quests.Quests;

import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemUtil implements ColorUtil {

    /**
     * Will compare stacks by id, amount, data, name/lore and enchantments
     * 
     * 
     * @param one
     *            ItemStack to compare
     * @param two
     *            ItemStack to compare to
     * @return 0 if stacks are equal, or the first inequality from the following
     *         values:<br>
     * @return -1&nbsp;-> stack ids are unequal<br>
     * @return -2&nbsp;-> stack amounts are unequal<br>
     * @return -3&nbsp;-> stack data is unequal<br>
     * @return -4&nbsp;-> stack name/lore is unequal<br>
     * @return -5&nbsp;-> stack enchantments are unequal<br>
     */
    public static int compareItems(ItemStack one, ItemStack two, boolean ignoreAmount) {

        if (one == null && two != null || one != null && two == null) {
            return 0;
        }

        if (one == null && two == null) {
            return 0;
        }

        if (one.getTypeId() != two.getTypeId()) {
            return -1;
        } else if ((one.getAmount() != two.getAmount()) && ignoreAmount == false) {
            return -2;
        } else if (one.getData().equals(two.getData()) == false) {
            return -3;
        }

        if (one.hasItemMeta() || two.hasItemMeta()) {

            if (one.hasItemMeta() && two.hasItemMeta() == false) {
                return -4;
            } else if (one.hasItemMeta() == false && two.hasItemMeta()) {
                return -4;
            } else if (one.getItemMeta().hasDisplayName() && two.getItemMeta().hasDisplayName() == false) {
                return -4;
            } else if (one.getItemMeta().hasDisplayName() == false && two.getItemMeta().hasDisplayName()) {
                return -4;
            } else if (one.getItemMeta().hasLore() && two.getItemMeta().hasLore() == false) {
                return -4;
            } else if (one.getItemMeta().hasLore() == false && two.getItemMeta().hasLore()) {
                return -4;
            } else if (one.getItemMeta().hasDisplayName()
                    && two.getItemMeta().hasDisplayName()
                    && ChatColor.stripColor(one.getItemMeta().getDisplayName()).equals(
                            ChatColor.stripColor(two.getItemMeta().getDisplayName())) == false) {
                return -4;
            } else if (one.getItemMeta().hasLore() && two.getItemMeta().hasLore()
                    && one.getItemMeta().getLore().equals(two.getItemMeta().getLore()) == false) {
                return -4;
            }

        }

        if (one.getEnchantments().equals(two.getEnchantments()) == false) {
            return -5;
        } else {
            return 0;
        }

    }

    // Formats -> id-id:amount-amount:data-data:enchantment-enchantment
    // level:name-name:lore-lore:
    //
    public static ItemStack readItemStack(String data) {

        if (data == null) {
            return null;
        }
        ItemStack stack = null;
        final String[] args = data.split(":");
        ItemMeta meta = null;
        final LinkedList<String> lore = new LinkedList<String>();
        for (final String arg: args) {

            if (arg.startsWith("id-")) {
                stack = new ItemStack(Integer.parseInt(arg.substring(3)));
                meta = stack.getItemMeta();
            } else if (arg.startsWith("amount-")) {
                stack.setAmount(Integer.parseInt(arg.substring(7)));
            } else if (arg.startsWith("data-")) {
                stack.setDurability(Short.parseShort(arg.substring(5)));
            } else if (arg.startsWith("enchantment-")) {
                final String[] enchs = arg.substring(12).split(" ");
                final Enchantment e = Quests.getEnchantment(enchs[0]);
                meta.addEnchant(e, Integer.parseInt(enchs[1]), true);
            } else if (arg.startsWith("name-")) {
                meta.setDisplayName(arg.substring(5));
            } else if (arg.startsWith("lore-")) {
                lore.add(arg.substring(5));
            }

        }

        if (lore.isEmpty() == false) {
            meta.setLore(lore);
        }

        stack.setItemMeta(meta);

        return stack;

    }

    public static String serialize(ItemStack is) {

        String serial;

        if (is == null) {
            return null;
        }

        serial = "id-" + is.getTypeId();
        serial += ":amount-" + is.getAmount();
        if (is.getDurability() != 0) {
            serial += ":data-" + is.getDurability();
        }
        if (is.getEnchantments().isEmpty() == false) {

            for (final Entry<Enchantment, Integer> e: is.getEnchantments().entrySet()) {
                serial += ":enchantment-" + Quester.enchantmentString(e.getKey()) + " " + e.getValue();
            }

        }
        if (is.hasItemMeta()) {

            final ItemMeta meta = is.getItemMeta();
            if (meta.hasDisplayName()) {
                serial += ":name-" + meta.getDisplayName();
            }
            if (meta.hasLore()) {
                for (final String s: meta.getLore()) {
                    serial += ":lore-" + s;
                }
            }

        }

        return serial;

    }

    public static String getDisplayString(ItemStack is) {

        String text;

        if (is.hasItemMeta() && is.getItemMeta().hasDisplayName()) {
            text = "" + ColorUtil.DARKAQUA + ColorUtil.ITALIC + is.getItemMeta().getDisplayName() + ColorUtil.RESET
                    + ColorUtil.AQUA + " x " + is.getAmount();
        } else {
            text = ColorUtil.AQUA + Quester.prettyItemString(is.getTypeId());
            if (is.getDurability() != 0) {
                text += ColorUtil.AQUA + ":" + is.getDurability();
            }

            text += ColorUtil.AQUA + " x " + is.getAmount();

            if (is.getEnchantments().isEmpty() == false) {
                text += " " + ColorUtil.PURPLE + "*Enchanted*";
            }

        }

        return text;

    }

    public static String getString(ItemStack is) {

        String text;

        if (is.hasItemMeta() && is.getItemMeta().hasDisplayName()) {
            text = "" + ColorUtil.DARKAQUA + ColorUtil.ITALIC + is.getItemMeta().getDisplayName() + ColorUtil.RESET
                    + ColorUtil.AQUA + " x " + is.getAmount();
        } else {
            text = ColorUtil.AQUA + Quester.prettyItemString(is.getTypeId());
            if (is.getDurability() != 0) {
                text += ColorUtil.AQUA + ":" + is.getDurability();
            }

            text += ColorUtil.AQUA + " x " + is.getAmount();

        }

        return text;

    }

    public static String getName(ItemStack is) {

        String text;

        if (is.hasItemMeta() && is.getItemMeta().hasDisplayName()) {
            text = "" + ColorUtil.DARKAQUA + ColorUtil.ITALIC + is.getItemMeta().getDisplayName();
        } else {
            text = ColorUtil.AQUA + Quester.prettyItemString(is.getTypeId());
        }

        return text;

    }

}
