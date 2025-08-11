package xyz.pub.hud.impl;

import xyz.pub.api.events.impl.EventRender2D;
import xyz.pub.hud.HudElement;
import xyz.pub.utils.render.ColorUtils;
import xyz.pub.utils.render.Render2D;
import xyz.pub.utils.render.fonts.Fonts;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.client.resource.language.I18n;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Potions extends HudElement {

    public Potions() {
        super("Potions");
    }

    private static class PotionInfo {
        String name;
        String level;
        String time;

        PotionInfo(String name, String level, String time) {
            this.name = name;
            this.level = level;
            this.time = time;
        }
    }

    // Простые анимации
    private float scaleAnimation = 0f;
    private float alphaAnimation = 0f;

    private List<PotionInfo> getPotionInfos() {
        List<PotionInfo> potions = new ArrayList<>();

        for (StatusEffectInstance effect : mc.player.getStatusEffects()) {
            String name = I18n.translate(effect.getEffectType().value().getTranslationKey());
            String level = effect.getAmplifier() > 0 ? " " + (effect.getAmplifier() + 1) : "";
            String time = formatTime(effect.getDuration());
            potions.add(new PotionInfo(name, level, time));
        }

        return potions;
    }

    private String formatTime(int ticks) {
        int seconds = ticks / 20;
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    private float smoothStep(float current, float target, float speed) {
        float delta = target - current;
        return current + delta * speed;
    }

    @Override
    @EventHandler
    public void onRender2D(EventRender2D e) {
        if (fullNullCheck() || closed()) return;

        List<PotionInfo> potions = getPotionInfos();
        boolean chatOpen = mc.currentScreen != null;


        List<PotionInfo> displayPotions;
        boolean shouldShow;

        if (chatOpen) {

            displayPotions = List.of(
                    new PotionInfo("Speed", " II", "2:30"),
                    new PotionInfo("Strength", "", "1:45"),
                    new PotionInfo("Regeneration", " III", "0:45")
            );
            shouldShow = true;
        } else {

            displayPotions = potions;
            shouldShow = !potions.isEmpty();
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
        float minWidth = 90f;


        float titleWidth = Fonts.BOLD.getWidth("Potions", fontSize) + padding * 2;
        float maxContentWidth = Math.max(titleWidth, minWidth);

        for (PotionInfo potion : displayPotions) {
            String nameWithLevel = potion.name + potion.level;
            float nameWidth = Fonts.MEDIUM.getWidth(nameWithLevel, fontSize);
            float timeWidth = Fonts.MEDIUM.getWidth(potion.time, fontSize);
            float requiredWidth = nameWidth + timeWidth + 20f + padding * 2;
            maxContentWidth = Math.max(maxContentWidth, requiredWidth);
        }

        float width = maxContentWidth;
        float height = lineHeight + (displayPotions.size() * lineHeight) + padding * 2;


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
                "Potions",
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
        for (PotionInfo potion : displayPotions) {
            String nameWithLevel = potion.name + potion.level;
            float timeWidth = Fonts.MEDIUM.getWidth(potion.time, fontSize);


            Render2D.drawFont(
                    e.getContext().getMatrices(),
                    Fonts.MEDIUM.getFont(fontSize),
                    nameWithLevel,
                    posX + padding,
                    currentY,
                    new Color(255, 255, 255, textAlpha)
            );


            float timeX = posX + width - padding - timeWidth;
            Color globalColor = ColorUtils.getGlobalColor();
            Render2D.drawFont(
                    e.getContext().getMatrices(),
                    Fonts.MEDIUM.getFont(fontSize),
                    potion.time,
                    timeX,
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