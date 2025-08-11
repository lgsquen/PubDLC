package xyz.pub.modules.impl.render;

import xyz.pub.api.events.impl.EventRender3D;
import xyz.pub.modules.api.Category;
import xyz.pub.modules.api.Module;
import xyz.pub.modules.settings.impl.NumberSetting;
import xyz.pub.utils.render.Render3D;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import java.awt.Color;

public class Pencil extends Module {

    private final NumberSetting size = new NumberSetting("Size", 1.0f, 0.1f, 5.0f, 0.1f);
    private final NumberSetting distance = new NumberSetting("Distance", 0.0f, -2.0f, 2.0f, 0.1f);

    // Плавные переменные для анимации
    private float smoothBodyYaw = 0f;

    public Pencil() {
        super("Pencil", Category.Render);
    }

    @EventHandler
    public void onRender3D(EventRender3D.Game e) {
        if (fullNullCheck()) return;

        Vec3d playerPos = mc.player.getPos();
        float scale = size.getValue();
        float dist = distance.getValue();

        // Плавная интерполяция поворота тела
        float targetBodyYaw = mc.player.bodyYaw;
        smoothBodyYaw = MathHelper.lerp(0.15f, smoothBodyYaw, targetBodyYaw);

        // Розовый цвет
        Color pinkColor = new Color(255, 182, 193, 200);

        // Позиция между телом и ногами
        Vec3d centerPos = playerPos.add(dist, 0.9, 0);

        // Вычисляем направление тела (только по горизонтали)
        float bodyYawRad = (float) Math.toRadians(smoothBodyYaw);

        Vec3d bodyDirection = new Vec3d(
                -Math.sin(bodyYawRad),
                0,
                Math.cos(bodyYawRad)
        );

        // Боковое направление (для расположения яичек)
        Vec3d sideDirection = new Vec3d(
                Math.cos(bodyYawRad),
                0,
                Math.sin(bodyYawRad)
        );

        // Основная длинная сфера (направлена по телу вперед)
        Vec3d shaftPos = centerPos.add(bodyDirection.multiply(0.6f * scale));

        // Рисуем основной эллипсоид
        Render3D.drawSphere(
                e.getMatrixStack(),
                shaftPos,
                0.3f * scale, // ширина (X)
                0.3f * scale, // высота (Y)
                1.2f * scale, // длина (Z) - вытянутый вперед
                25,
                pinkColor
        );

        // Расстояние между яичками
        float ballDistance = 0.25f * scale;

        // Левая сфера (яичко)
        Vec3d leftSphere = centerPos.add(sideDirection.multiply(-ballDistance));
        Render3D.drawSphere(
                e.getMatrixStack(),
                leftSphere,
                0.35f * scale,
                0.4f * scale,
                0.35f * scale,
                20,
                pinkColor
        );

        // Правая сфера (яичко)
        Vec3d rightSphere = centerPos.add(sideDirection.multiply(ballDistance));
        Render3D.drawSphere(
                e.getMatrixStack(),
                rightSphere,
                0.35f * scale,
                0.4f * scale,
                0.35f * scale,
                20,
                pinkColor
        );
    }
}