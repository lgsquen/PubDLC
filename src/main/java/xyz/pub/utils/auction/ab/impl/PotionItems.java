package xyz.pub.utils.auction.ab.impl;

import xyz.pub.utils.auction.ab.ABItems;
import lombok.experimental.UtilityClass;
import net.minecraft.item.ItemStack;

@UtilityClass
public class PotionItems {

    public ItemStack getSerka() {
        return ABItems.serka();
    }

    public ItemStack getAgentka() {
        return ABItems.agentka();
    }

    public ItemStack getKillerka() {
        return ABItems.killerka();
    }

    public ItemStack getOtrizhka() {
        return ABItems.otrizhka();
    }

    public ItemStack getMedika() {
        return ABItems.medika();
    }

    public ItemStack getPobedilka() {
        return ABItems.pobedilka();
    }
}