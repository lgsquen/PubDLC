package xyz.pub.screen.clickgui.components.impl;

import xyz.pub.modules.settings.impl.BooleanSetting;
import xyz.pub.screen.clickgui.components.Component;
import xyz.pub.utils.animations.Animation;
import xyz.pub.utils.animations.Easing;
import xyz.pub.utils.math.MathUtils;
import xyz.pub.utils.render.ColorUtils;
import xyz.pub.utils.render.fonts.Fonts;
import xyz.pub.utils.render.Render2D;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.resource.language.I18n;

import java.awt.*;

public class BooleanComponent extends Component {

    private final BooleanSetting setting;
    private final Animation toggleAnimation = new Animation(300, 1f, true, Easing.DRUGHACK);

    public BooleanComponent(BooleanSetting setting) {
        super(setting.getName());
        this.setting = setting;
        this.visible = setting::isVisible;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        toggleAnimation.update(setting.getValue());
        Render2D.drawFont(context.getMatrices(), Fonts.REGULAR.getFont(7.5f), I18n.translate(setting.getName()), x + 4f, y + 3f, Color.WHITE);
        Render2D.drawRoundedRect(context.getMatrices(), x + width - 20f, y + 3.5f, 16f * toggleAnimation.getValue(), 8f, 2.5f, ColorUtils.getGlobalColor((int) (255 * toggleAnimation.getLinear())));
        Render2D.drawRoundedRect(context.getMatrices(), x + width - 4f - (16 * toggleAnimation.getReversedValue()), y + 3.5f, 16f * toggleAnimation.getReversedValue(), 8f, 2.5f, new Color(23, 23, 23, 100));
        Render2D.drawRoundedRect(context.getMatrices(), x + width - 19.5f + (8f * toggleAnimation.getValue()), y + 4f, 7f, 7f, 2.5f, Color.WHITE);
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (MathUtils.isHovered(x + width - 20f, y + 3.5f, 16f, 8f, (float) mouseX, (float) mouseY) && button == 0) setting.setValue(!setting.getValue());
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button) {

    }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) {

    }

    @Override
    public void keyReleased(int keyCode, int scanCode, int modifiers) {

    }

    @Override
    public void charTyped(char chr, int modifiers) {

    }
}