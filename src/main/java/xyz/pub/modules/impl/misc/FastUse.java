package xyz.pub.modules.impl.misc;

import xyz.pub.api.events.impl.EventTick;
import xyz.pub.api.mixins.accessors.IMinecraftClient;
import xyz.pub.modules.api.Category;
import xyz.pub.modules.api.Module;
import meteordevelopment.orbit.EventHandler;

public class FastUse extends Module {

    public FastUse() {
        super("FastUse", Category.Misc);
    }

    @EventHandler
    public void onTick(EventTick e) {
        if (fullNullCheck()) return;

        ((IMinecraftClient) mc).setItemUseCooldown(0);
    }
}