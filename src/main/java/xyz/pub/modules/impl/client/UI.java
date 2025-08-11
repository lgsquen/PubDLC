package xyz.pub.modules.impl.client;

import xyz.pub.Pub;
import xyz.pub.api.events.impl.EventTick;
import xyz.pub.screen.clickgui.ClickGui;
import xyz.pub.modules.api.Category;
import xyz.pub.modules.api.Module;
import xyz.pub.modules.settings.api.Bind;
import meteordevelopment.orbit.EventHandler;
import org.lwjgl.glfw.GLFW;

public class UI extends Module {

    public UI() {
        super("UI", Category.Client);
        setBind(new Bind(GLFW.GLFW_KEY_RIGHT_SHIFT, false));
    }

    @EventHandler
    public void onTick(EventTick e) {
        if (!(mc.currentScreen instanceof ClickGui)) setToggled(false);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        mc.setScreen(Pub.getInstance().getClickGui());
    }
}