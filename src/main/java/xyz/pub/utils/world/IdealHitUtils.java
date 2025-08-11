package xyz.pub.utils.world;

import xyz.pub.utils.Wrapper;
import lombok.experimental.UtilityClass;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.AxeItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.SwordItem;

@UtilityClass
public class IdealHitUtils implements Wrapper {

    public float getAICooldown() {
        return 20.0f - (mc.player.getAttackCooldownProgress(0.5f) * 20.0f);
    }

    public boolean canAIFall() {
        return mc.player.fallDistance > 0.0f && !mc.player.isOnGround();
    }

    public boolean canCritical(LivingEntity target) {
        if (target == null) return false;

        return mc.player.fallDistance > 0.0f &&
                !mc.player.isOnGround() &&
                !mc.player.isClimbing() &&
                !mc.player.isTouchingWater() &&
                !mc.player.hasVehicle() &&
                !mc.player.hasStatusEffect(StatusEffects.BLINDNESS);
    }

    public Block getBlock(double x, double y, double z) {
        return mc.world.getBlockState(new net.minecraft.util.math.BlockPos((int) x, (int) y, (int) z)).getBlock();
    }

    public boolean findFall(float fallDistance) {
        return mc.player.fallDistance >= fallDistance;
    }

    public boolean isWeapon() {
        return mc.player.getMainHandStack().getItem() instanceof SwordItem ||
                mc.player.getMainHandStack().getItem() instanceof AxeItem ||
                mc.player.getMainHandStack().getItem() instanceof PickaxeItem ||
                mc.player.getMainHandStack().getItem() instanceof ShovelItem ||
                mc.player.getMainHandStack().getItem() instanceof HoeItem;
    }
}