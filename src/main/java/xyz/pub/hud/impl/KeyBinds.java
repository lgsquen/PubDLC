package xyz.pub.hud.impl;

import xyz.pub.Pub;
import xyz.pub.api.events.impl.EventRender2D;
import xyz.pub.hud.HudElement;
import xyz.pub.modules.api.Module;
import xyz.pub.utils.render.ColorUtils;
import xyz.pub.utils.render.Render2D;
import xyz.pub.utils.render.fonts.Fonts;
import meteordevelopment.orbit.EventHandler;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class KeyBinds extends HudElement {

    public KeyBinds() {
        super("KeyBinds");
    }

    private static class ModuleBind {
        String moduleName;
        String bindName;

        ModuleBind(String moduleName, String bindName) {
            this.moduleName = moduleName;
            this.bindName = bindName;
        }
    }

    private final List<ModuleBind> moduleBinds = new ArrayList<>();
    private final List<ModuleBind> exampleBinds = List.of(
            new ModuleBind("Aura", "R"),
            new ModuleBind("Flight", "F"),
            new ModuleBind("Speed", "V"),
            new ModuleBind("NoFall", "N"),
            new ModuleBind("ESP", "B")
    );

    // Простые анимации
    private float scaleAnimation = 0f;
    private float alphaAnimation = 0f;

    private void updateModuleBinds() {
        moduleBinds.clear();
        for (Module module : Pub.getInstance().getModuleManager().getModules()) {
            if (module.isToggled() && !module.getBind().isEmpty()) {
                String bindName = module.getBind().toString();
                moduleBinds.add(new ModuleBind(module.getName(), bindName));
            }
        }
    }

    private float smoothStep(float current, float target, float speed) {
        float delta = target - current;
        return current + delta * speed;
    }

    @Override
    @EventHandler
    public void onRender2D(EventRender2D e) {
        if (fullNullCheck() || closed()) return;

        updateModuleBinds();
        boolean chatOpen = mc.currentScreen != null;


        List<ModuleBind> displayBinds;
        boolean shouldShow;

        if (chatOpen) {
            displayBinds = exampleBinds.subList(0, Math.min(3, exampleBinds.size()));
            shouldShow = true;
        } else {
            displayBinds = moduleBinds;
            shouldShow = !moduleBinds.isEmpty();
        }


        float targetScale = shouldShow ? 1f : 0f;
        float targetAlpha = shouldShow ? 1f : 0f;

        scaleAnimation = smoothStep(scaleAnimation, targetScale, 0.15f);
        alphaAnimation = smoothStep(alphaAnimation, targetAlpha, 0.12f);


        if (scaleAnimation < 0.01f && !shouldShow) {
            return;
        }

        float posX = getX();
        float posY = getY();
        float padding = 6f;
        float round = 4f;
        float fontSize = 8f;
        float lineHeight = Fonts.MEDIUM.getHeight(fontSize) + 2f;
        float minWidth = 80f;


        float titleWidth = Fonts.BOLD.getWidth("KeyBinds", fontSize) + padding * 2;
        float maxContentWidth = Math.max(titleWidth, minWidth);

        for (ModuleBind bind : displayBinds) {
            float moduleWidth = Fonts.MEDIUM.getWidth(bind.moduleName, fontSize);
            float bindWidth = Fonts.MEDIUM.getWidth(bind.bindName, fontSize);
            float requiredWidth = moduleWidth + bindWidth + 20f + padding * 2;
            maxContentWidth = Math.max(maxContentWidth, requiredWidth);
        }

        float width = maxContentWidth;
        float height = lineHeight + (displayBinds.size() * lineHeight) + padding * 2;


        e.getContext().getMatrices().push();
        e.getContext().getMatrices().translate(posX + width / 2, posY + height / 2, 0f);
        e.getContext().getMatrices().scale(scaleAnimation, scaleAnimation, 0);
        e.getContext().getMatrices().translate(-(posX + width / 2), -(posY + height / 2), 0f);


        int backgroundAlpha = (int)(150 * alphaAnimation);
        Render2D.drawStyledRect(
                e.getContext().getMatrices(),
                posX,
                posY,
                width,
                height,
                round,
                new Color(0, 0, 0, backgroundAlpha),
                255
        );


        int textAlpha = (int)(255 * alphaAnimation);
        Render2D.drawFont(
                e.getContext().getMatrices(),
                Fonts.BOLD.getFont(fontSize),
                "KeyBinds",
                posX + padding + 1f,
                posY + padding,
                new Color(255, 255, 255, textAlpha)
        );


        int lineAlpha = (int)(100 * alphaAnimation);
        Render2D.drawRoundedRect(
                e.getContext().getMatrices(),
                posX + padding,
                posY + padding + lineHeight - 1f,
                width - padding * 2,
                1f,
                0.5f,
                new Color(255, 255, 255, lineAlpha)
        );


        float currentY = posY + padding + lineHeight;
        for (ModuleBind bind : displayBinds) {
            float bindWidth = Fonts.MEDIUM.getWidth(bind.bindName, fontSize);


            Render2D.drawFont(
                    e.getContext().getMatrices(),
                    Fonts.MEDIUM.getFont(fontSize),
                    bind.moduleName,
                    posX + padding,
                    currentY,
                    new Color(255, 255, 255, textAlpha)
            );


            float bindX = posX + width - padding - bindWidth;
            Color globalColor = ColorUtils.getGlobalColor();
            Render2D.drawFont(
                    e.getContext().getMatrices(),
                    Fonts.MEDIUM.getFont(fontSize),
                    bind.bindName,
                    bindX,
                    currentY,
                    new Color(globalColor.getRed(), globalColor.getGreen(),
                            globalColor.getBlue(), textAlpha)
            );

            currentY += lineHeight;
        }

        e.getContext().getMatrices().pop();
        setBounds(getX(), getY(), width, height);
        super.onRender2D(e);
    }
}