package xyz.pub.modules.impl.combat;

import xyz.pub.api.events.impl.EventTick;
import xyz.pub.api.mixins.accessors.IMinecraftClient;
import xyz.pub.modules.api.Category;
import xyz.pub.modules.api.Module;
import meteordevelopment.orbit.EventHandler;

public class NoAttackCooldown extends Module {

    public NoAttackCooldown() {
        super("NoAttackCooldown", Category.Combat);
    }

    @EventHandler
    public void onTick(EventTick e) {
        if (fullNullCheck()) return;

        ((IMinecraftClient) mc).setAttackCooldown(0);
    }
}