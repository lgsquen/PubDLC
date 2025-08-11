package xyz.pub.screen.clickgui.components.impl;

import xyz.pub.modules.settings.impl.StringSetting;
import xyz.pub.screen.clickgui.components.Component;
import xyz.pub.utils.animations.Animation;
import xyz.pub.utils.animations.Easing;
import xyz.pub.utils.math.MathUtils;
import xyz.pub.utils.render.ColorUtils;
import xyz.pub.utils.render.fonts.Fonts;
import xyz.pub.utils.render.Render2D;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public class StringComponent extends Component {

    private final StringSetting setting;
    private boolean typing, selected;
    private final Animation animation = new Animation(300, 1f, false, Easing.BOTH_SINE);
    private float scrollOffset = 0f;

    public StringComponent(StringSetting setting) {
        super(setting.getName());
        this.setting = setting;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        animation.update(typing);

        float textX = x + 7.5f;
        float textY = y + 2.5f;
        float maxTextWidth = width - 16f;
        float textWidth = Fonts.REGULAR.getWidth(I18n.translate(setting.getValue()), 8f);
        
        if (textWidth > maxTextWidth) {
            if (textWidth - scrollOffset > maxTextWidth) scrollOffset = textWidth - maxTextWidth;
            if (textWidth - scrollOffset < 0) scrollOffset = textWidth;
        } else scrollOffset = 0f;

        Render2D.drawRoundedRect(context.getMatrices(), x + 4f, y, width - 8f, height, 1.5f, ColorUtils.getGlobalColor());

        Render2D.startScissor(context, x + 4, y, width - 8, height);

        if (selected)
            Render2D.drawRoundedRect(context.getMatrices(), textX, textY, Fonts.REGULAR.getWidth(I18n.translate(setting.getValue()), 8f), Fonts.REGULAR.getHeight(8f), 0f, new Color(0, 0, 255, 150));

        Render2D.drawFont(context.getMatrices(), Fonts.REGULAR.getFont(8f), setting.getName(), textX, textY, new Color(255, 255, 255, (int) (255 * animation.getReversedValue())));

        if (!setting.getValue().isEmpty())
            Render2D.drawFont(context.getMatrices(), Fonts.REGULAR.getFont(8f), I18n.translate(setting.getValue()), textX - scrollOffset, textY, new Color(255, 255, 255, (int) (255 * animation.getValue())));

        Render2D.drawFont(context.getMatrices(), Fonts.REGULAR.getFont(8f), "|", textX - scrollOffset + textWidth, textY - 0.25f, ColorUtils.alpha(ColorUtils.pulse(Color.WHITE, 15), (int) (ColorUtils.pulse(Color.WHITE, 15).getAlpha() * animation.getValue())));

        Render2D.stopScissor(context);
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (MathUtils.isHovered(x, y, width, height, (float) mouseX, (float) mouseY) && button == 0) typing = !typing;
        else typing = false;
        selected = false;
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button) {}

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        switch (keyCode) {
            case GLFW.GLFW_KEY_BACKSPACE -> {
                if (selected) {
                    setting.setValue("");
                    selected = false;
                }
                if (typing && setting.getValue() != null && !setting.getValue().isEmpty()) setting.setValue(setting.getValue().substring(0, setting.getValue().length() - 1));
            }
            case GLFW.GLFW_KEY_ESCAPE, GLFW.GLFW_KEY_ENTER -> {
                if (typing) {
                    typing = false;
                    selected = false;
                }
            }
            case GLFW.GLFW_KEY_DELETE -> {
                if (typing) {
                    setting.setValue("");
                    selected = false;
                }
            }
            case GLFW.GLFW_KEY_C -> {
                if (Screen.hasControlDown() && typing && selected && setting.getValue() != null && !setting.getValue().isEmpty()) {
                    GLFW.glfwSetClipboardString(mc.getWindow().getHandle(), setting.getValue());
                    selected = false;
                }
            }
            case GLFW.GLFW_KEY_V -> {
                if (Screen.hasControlDown() && typing && GLFW.glfwGetClipboardString(mc.getWindow().getHandle()) != null) {
                    selected = false;
                    setting.setValue(setting.getValue() + GLFW.glfwGetClipboardString(mc.getWindow().getHandle()));
                }
            }
            case GLFW.GLFW_KEY_A -> {
                if (Screen.hasControlDown() && typing && setting.getValue() != null && !setting.getValue().isEmpty()) selected = true;
            }
        }
    }

    @Override
    public void keyReleased(int keyCode, int scanCode, int modifiers) {}

    @Override
    public void charTyped(char chr, int modifiers) {
        if (!typing) return;
        if (setting.isOnlyDigit() && !Character.isDigit(chr)) return;
        setting.setValue(setting.getValue() + chr);
        selected = false;
    }
}
