package xyz.pub.modules.impl.combat;

import xyz.pub.Pub;
import xyz.pub.api.events.impl.EventPlayerTick;
import xyz.pub.modules.api.Category;
import xyz.pub.modules.api.Module;
import xyz.pub.modules.impl.client.Targets;
import xyz.pub.modules.settings.impl.NumberSetting;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

public class Trigger extends Module {

    private final NumberSetting range = new NumberSetting("settings.trigger.range", 3.5f, 1f, 6f, 0.1f);

    public Trigger() {
        super("Trigger", Category.Combat);
    }

    @EventHandler
    public void onPlayerTick(EventPlayerTick e) {
        if (fullNullCheck() || !isToggled()) return;

        if (mc.crosshairTarget == null || mc.crosshairTarget.getType() != HitResult.Type.ENTITY) return;

        EntityHitResult entityHit = (EntityHitResult) mc.crosshairTarget;
        if (!(entityHit.getEntity() instanceof LivingEntity target)) return;

        if (mc.player.distanceTo(target) > range.getValue()) return;

        if (!isValidTarget(target)) return;

        if (mc.player.getAttackCooldownProgress(0.0f) < 1.0f) return;

        attack(target);
    }

    private void attack(LivingEntity target) {
        mc.interactionManager.attackEntity(mc.player, target);
        mc.player.swingHand(Hand.MAIN_HAND);
    }

    private boolean isValidTarget(LivingEntity entity) {
        if (entity == mc.player) return false;
        if (entity.isDead() || entity.getHealth() <= 0) return false;

        Targets targets = Pub.getInstance().getModuleManager().getModule(Targets.class);
        if (targets == null || !targets.isToggled()) return false;

        return targets.isValid(entity);
    }
}