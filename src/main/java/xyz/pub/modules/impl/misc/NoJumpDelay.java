package xyz.pub.modules.impl.misc;

import xyz.pub.api.events.impl.EventTick;
import xyz.pub.api.mixins.accessors.ILivingEntity;
import xyz.pub.modules.api.Category;
import xyz.pub.modules.api.Module;
import meteordevelopment.orbit.EventHandler;

public class NoJumpDelay extends Module {

    public NoJumpDelay() {
        super("NoJumpDelay", Category.Misc);
    }

    @EventHandler
    public void onTick(EventTick e) {
        if (fullNullCheck()) return;

        ((ILivingEntity) mc.player).setJumpingCooldown(0);
    }
}