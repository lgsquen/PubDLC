package xyz.pub.modules.impl.movement;

import xyz.pub.modules.api.Category;
import xyz.pub.modules.api.Module;
import xyz.pub.modules.settings.impl.NumberSetting;

public class ElytraForward extends Module {

    public final NumberSetting forward = new NumberSetting("settings.elytraforward.forward", 3f, 1f, 6f, 1f);

    public ElytraForward() {
        super("ElytraForward", Category.Movement);
    }
}