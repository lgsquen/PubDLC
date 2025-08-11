package xyz.pub.hud.impl;

import xyz.pub.Pub;
import xyz.pub.api.events.impl.EventRender2D;
import xyz.pub.hud.HudElement;
import xyz.pub.modules.impl.combat.Aura;
import xyz.pub.utils.animations.Easing;
import xyz.pub.utils.animations.infinity.InfinityAnimation;
import xyz.pub.utils.math.MathUtils;
import xyz.pub.utils.network.Server;
import xyz.pub.utils.render.ColorUtils;
import xyz.pub.utils.render.fonts.Fonts;
import xyz.pub.utils.render.Render2D;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.awt.Color;
import java.util.List;

public class TargetHud extends HudElement {

    public TargetHud() {
        super("TargetHud");
    }

    private final InfinityAnimation healthAnimation = new InfinityAnimation(Easing.LINEAR);
    private final InfinityAnimation gappleAnimation = new InfinityAnimation(Easing.LINEAR);

    @Override
    public void onRender2D(EventRender2D e) {
        if (fullNullCheck() || closed()) return;

        Aura aura = Pub.getInstance().getModuleManager().getModule(Aura.class);
        LivingEntity target = mc.currentScreen instanceof ChatScreen ? mc.player : aura.getTarget();

        // Простая анимация появления/исчезновения
        float targetScale = (target != null) ? 1f : 0f;
        float currentScale = smoothStep(toggledAnimation.getValue(), targetScale, 0.15f);

        if (currentScale < 0.01f) {
            toggledAnimation.setValue(currentScale);
            return;
        }

        toggledAnimation.setValue(currentScale);

        if (target == null) return;

        float posX = getX();
        float posY = getY();
        float width = 120f;
        float fontSize = 7f;
        float headSize = 24f;
        float padding = 4f;
        float round = 4f;

        float hp = MathUtils.round(Server.getHealth(target, false));
        float maxHp = MathUtils.round(target.getMaxHealth());
        float gappleHp = MathUtils.round(target.getAbsorptionAmount());
        float healthPercent = Math.max(0f, Math.min(1f, hp / maxHp));
        float gapplePercent = Math.max(0f, Math.min(1f, gappleHp / maxHp));

        // Плавная анимация полосок здоровья
        float barWidth = width - headSize - padding * 3;
        float healthWidth = healthAnimation.animate(barWidth * healthPercent, 200);
        float gappleWidth = gappleAnimation.animate(barWidth * gapplePercent, 200);

        // Вычисляем высоту на основе содержимого
        float barHeight = 4f;
        float barY = posY + padding + headSize - barHeight - 2f; // Полоска внизу головы
        float height = headSize + padding * 2; // Убираем лишнее пространство

        // Применяем анимацию масштабирования
        e.getContext().getMatrices().push();
        e.getContext().getMatrices().translate(posX + width / 2, posY + height / 2, 0f);
        e.getContext().getMatrices().scale(currentScale, currentScale, 0);
        e.getContext().getMatrices().translate(-(posX + width / 2), -(posY + height / 2), 0f);

        // Рендерим экипировку для игроков
        if (target instanceof PlayerEntity player) {
            float offset = 0f;
            List<ItemStack> armor = player.getInventory().armor;
            for (ItemStack stack : new ItemStack[]{armor.get(3), armor.get(2), armor.get(1), armor.get(0), player.getOffHandStack(), player.getMainHandStack()}) {
                if (stack.isEmpty()) continue;
                e.getContext().getMatrices().push();
                e.getContext().getMatrices().scale(0.6f, 0.6f, 0.6f);
                e.getContext().drawItem(stack, (int) ((posX + width - offset - padding * 2f) / 0.6f), (int) ((posY - padding * 2f) / 0.6f));
                e.getContext().getMatrices().pop();
                offset += 12f;
            }
        }

        // Основной фон
        Render2D.drawStyledRect(
                e.getContext().getMatrices(),
                posX,
                posY,
                width,
                height,
                round,
                new Color(0, 0, 0, 180),
                255
        );

        float headX = posX + padding;
        float headY = posY + padding;

        // Рендерим голову игрока или заглушку
        if (target instanceof PlayerEntity) {
            Render2D.drawTexture(
                    e.getContext().getMatrices(),
                    headX,
                    headY,
                    headSize,
                    headSize,
                    round / 2f,
                    0.125f,
                    0.125f,
                    0.125f,
                    0.125f,
                    ((AbstractClientPlayerEntity) target).getSkinTextures().texture(),
                    Color.WHITE
            );
        } else {
            Render2D.drawRoundedRect(
                    e.getContext().getMatrices(),
                    headX,
                    headY,
                    headSize,
                    headSize,
                    round / 2f,
                    new Color(40, 40, 40)
            );

            Render2D.drawFont(
                    e.getContext().getMatrices(),
                    Fonts.BOLD.getFont(fontSize + 2f),
                    "?",
                    headX + (headSize / 2f) - Fonts.BOLD.getWidth("?", fontSize + 2f) / 2f,
                    headY + (headSize / 2f) - Fonts.BOLD.getHeight(fontSize + 2f) / 2f,
                    new Color(255, 100, 100)
            );
        }

        // Информационная область
        float infoX = headX + headSize + padding;
        float infoY = headY;

        // Имя цели
        if (!target.getName().getString().isEmpty()) {
            Render2D.drawFont(
                    e.getContext().getMatrices(),
                    Fonts.MEDIUM.getFont(fontSize),
                    target.getName().getString(),
                    infoX,
                    infoY,
                    Color.WHITE
            );
        }

        // Информация о здоровье
        String healthText = String.format("%.1f HP", hp);
        if (gappleHp > 0) {
            healthText += String.format(" + %.1f", gappleHp);
        }

        Render2D.drawFont(
                e.getContext().getMatrices(),
                Fonts.MEDIUM.getFont(fontSize - 1f),
                healthText,
                infoX,
                infoY + fontSize + 2f,
                new Color(200, 200, 200)
        );

        // Полоска здоровья - фон с правильным закруглением
        Render2D.drawRoundedRect(
                e.getContext().getMatrices(),
                infoX,
                barY,
                barWidth,
                barHeight,
                2f, // Нормальное закругление
                new Color(30, 30, 30)
        );

        // Полоска здоровья - основная (зеленая) с правильным закруглением
        if (healthWidth > 0) {
            Color healthColor = getHealthColor(healthPercent);
            Render2D.drawRoundedRect(
                    e.getContext().getMatrices(),
                    infoX,
                    barY,
                    healthWidth,
                    barHeight,
                    2f, // Такое же закругление как у фона
                    healthColor
            );
        }

        // Полоска поглощения (желтая) - точно такая же как зеленая
        if (gappleWidth > 0) {
            Render2D.drawRoundedRect(
                    e.getContext().getMatrices(),
                    infoX,
                    barY, // Точно на том же уровне
                    gappleWidth,
                    barHeight, // Точно такая же высота
                    2f, // Точно такое же закругление
                    new Color(255, 215, 0, 200) // Немного более непрозрачная
            );
        }

        e.getContext().getMatrices().pop();
        setBounds(getX(), getY(), width, height);
        super.onRender2D(e);
    }

    private Color getHealthColor(float healthPercent) {
        if (healthPercent >= 0.75f) return new Color(85, 255, 85);
        else if (healthPercent >= 0.5f) return new Color(255, 255, 85);
        else if (healthPercent >= 0.25f) return new Color(255, 170, 85);
        else return new Color(255, 85, 85);
    }

    private float smoothStep(float current, float target, float speed) {
        float delta = target - current;
        return current + delta * speed;
    }
}