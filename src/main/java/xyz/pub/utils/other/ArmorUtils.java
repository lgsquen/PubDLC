package xyz.pub.utils.other;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class ArmorUtils {

    public static boolean isChestplate(Item item) {
        if (!(item instanceof ArmorItem armorItem)) return false;

        return item == Items.LEATHER_CHESTPLATE ||
                item == Items.CHAINMAIL_CHESTPLATE ||
                item == Items.IRON_CHESTPLATE ||
                item == Items.GOLDEN_CHESTPLATE ||
                item == Items.DIAMOND_CHESTPLATE ||
                item == Items.NETHERITE_CHESTPLATE;
    }

    public static boolean isChestplate(ItemStack stack) {
        return isChestplate(stack.getItem());
    }

    public static boolean isHelmet(Item item) {
        return item == Items.LEATHER_HELMET ||
                item == Items.CHAINMAIL_HELMET ||
                item == Items.IRON_HELMET ||
                item == Items.GOLDEN_HELMET ||
                item == Items.DIAMOND_HELMET ||
                item == Items.NETHERITE_HELMET ||
                item == Items.TURTLE_HELMET;
    }

    public static boolean isLeggings(Item item) {
        return item == Items.LEATHER_LEGGINGS ||
                item == Items.CHAINMAIL_LEGGINGS ||
                item == Items.IRON_LEGGINGS ||
                item == Items.GOLDEN_LEGGINGS ||
                item == Items.DIAMOND_LEGGINGS ||
                item == Items.NETHERITE_LEGGINGS;
    }

    public static boolean isBoots(Item item) {
        return item == Items.LEATHER_BOOTS ||
                item == Items.CHAINMAIL_BOOTS ||
                item == Items.IRON_BOOTS ||
                item == Items.GOLDEN_BOOTS ||
                item == Items.DIAMOND_BOOTS ||
                item == Items.NETHERITE_BOOTS;
    }

    public static EquipmentSlot getArmorSlot(Item item) {
        if (isHelmet(item)) return EquipmentSlot.HEAD;
        if (isChestplate(item)) return EquipmentSlot.CHEST;
        if (isLeggings(item)) return EquipmentSlot.LEGS;
        if (isBoots(item)) return EquipmentSlot.FEET;
        return null;
    }

    public static boolean isArmor(Item item) {
        return isHelmet(item) || isChestplate(item) || isLeggings(item) || isBoots(item);
    }
}