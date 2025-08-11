package xyz.pub.modules.impl.movement;

import xyz.pub.api.events.impl.EventRender2D;
import xyz.pub.modules.api.Category;
import xyz.pub.modules.api.Module;
import xyz.pub.modules.settings.api.Nameable;
import xyz.pub.modules.settings.impl.EnumSetting;
import xyz.pub.modules.settings.impl.NumberSetting;
import xyz.pub.utils.render.fonts.Fonts;
import xyz.pub.utils.render.Render2D;
import lombok.AllArgsConstructor;
import meteordevelopment.orbit.EventHandler;

import java.awt.*;

public class ElytraBooster extends Module {

    public final EnumSetting<Mode> mode = new EnumSetting<>("settings.mode", Mode.Custom);
    public final NumberSetting boost = new NumberSetting("settings.elytrabooster.boost", 1.5f, 1f, 3f, 0.05f, () -> mode.getValue() == Mode.Custom);

    public ElytraBooster() {
        super("ElytraBooster", Category.Movement);
    }

    @EventHandler
    public void onRender2D(EventRender2D e) {
        double motion = Math.hypot(mc.player.getX() - mc.player.prevX, mc.player.getZ() - mc.player.prevZ);

        String speed = String.format("%.2f " + " bps", motion * 20);
        Render2D.drawFont(
                e.getContext().getMatrices(),
                Fonts.BOLD.getFont(9f),
                speed,
                mc.getWindow().getScaledWidth() / 2f - Fonts.BOLD.getWidth(speed, 9f) / 2f,
                mc.getWindow().getScaledHeight() / 2f + 15,
                Color.WHITE
        );
    }

    @AllArgsConstructor
    public enum Mode implements Nameable {
        Custom("settings.elytrabooster.mode.custom"),
        Auto("settings.elytrabooster.mode.auto");

        private final String name;

        @Override
        public String getName() {
            return name;
        }
    }
}