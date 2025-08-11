package xyz.pub.modules.impl.combat;

import xyz.pub.Pub;
import xyz.pub.api.events.impl.EventPlayerTick;
import xyz.pub.api.events.impl.EventRender3D;
import xyz.pub.modules.api.Category;
import xyz.pub.modules.api.Module;
import xyz.pub.modules.impl.client.Targets;
import xyz.pub.modules.settings.impl.BooleanSetting;
import xyz.pub.modules.settings.impl.NumberSetting;
import xyz.pub.utils.math.TimerUtils;
import xyz.pub.utils.network.NetworkUtils;
import xyz.pub.utils.other.ArmorUtils;
import xyz.pub.utils.render.Render3D;
import xyz.pub.utils.rotations.RotationUtils;
import xyz.pub.utils.world.InventoryUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.item.ArmorItem;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MaceTarget extends Module {

    private final NumberSetting range = new NumberSetting("settings.macetarget.range", 20f, 10f, 30f, 1f);
    private final NumberSetting attackRange = new NumberSetting("settings.macetarget.attackrange", 4f, 3f, 6f, 0.1f);
    private final NumberSetting flyHeight = new NumberSetting("settings.macetarget.flyheight", 15f, 10f, 25f, 1f);
    private final BooleanSetting renderLine = new BooleanSetting("settings.macetarget.renderline", true);
    private final NumberSetting attackCooldown = new NumberSetting("settings.macetarget.attackcooldown", 500f, 100f, 2000f, 50f);
    private final BooleanSetting smoothRotations = new BooleanSetting("settings.macetarget.smoothrotations", true);
    private final NumberSetting rotationSpeed = new NumberSetting("settings.macetarget.rotationspeed", 15f, 5f, 50f, 1f);

    private LivingEntity target;
    private State currentState = State.IDLE;
    private final TimerUtils stateTimer = new TimerUtils();
    private final TimerUtils fireworkTimer = new TimerUtils();
    private final TimerUtils rotationTimer = new TimerUtils();
    private final TimerUtils jumpTimer = new TimerUtils();
    private final TimerUtils groundSpoofTimer = new TimerUtils();
    private Vec3d targetPosition = Vec3d.ZERO;
    private Vec3d lastTargetPosition = Vec3d.ZERO;
    private boolean hasUsedFirstFirework = false;
    private boolean hasUsedSecondFirework = false;
    private boolean hasAttacked = false;
    private boolean isRotating = false;
    private float targetYaw = 0f;
    private float targetPitch = 0f;
    private float currentYaw = 0f;
    private float currentPitch = 0f;
    private int jumpAttempts = 0;
    private boolean hasJumped = false;
    private double lastY = 0;
    private boolean wasOnGround = false;
    private int ticksInAir = 0;

    public MaceTarget() {
        super("MaceTarget", Category.Combat);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        reset();
        sendMessage("§aMaceTarget включен!");

        if (!hasRequiredItems()) {
            sendMessage("§cОтсутствуют необходимые предметы!");
            toggle();
            return;
        }

        if (!hasChestplateEquipped()) {
            sendMessage("§cНаденьте нагрудник!");
        } else {
            sendMessage("§aНагрудник надет");
        }
    }

    private void reset() {
        currentState = State.IDLE;
        target = null;
        hasUsedFirstFirework = false;
        hasUsedSecondFirework = false;
        hasAttacked = false;
        isRotating = false;
        jumpAttempts = 0;
        hasJumped = false;
        lastY = 0;
        wasOnGround = false;
        ticksInAir = 0;
        targetPosition = Vec3d.ZERO;
        lastTargetPosition = Vec3d.ZERO;
        stateTimer.reset();
        fireworkTimer.reset();
        rotationTimer.reset();
        jumpTimer.reset();
        groundSpoofTimer.reset();

        if (mc.player != null) {
            currentYaw = mc.player.getYaw();
            currentPitch = mc.player.getPitch();
            lastY = mc.player.getY();
            wasOnGround = mc.player.isOnGround();
        }
    }

    private void sendMessage(String s) {
        if (mc.player != null) {
            mc.player.sendMessage(net.minecraft.text.Text.literal(s), false);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        reset();
        sendMessage("§cMaceTarget выключен");
    }

    public LivingEntity getTarget() {
        return target;
    }

    @EventHandler
    public void onPlayerTick(EventPlayerTick e) {
        if (fullNullCheck() || !isToggled()) return;

        // Обновляем текущие углы поворота
        if (mc.player != null) {
            currentYaw = mc.player.getYaw();
            currentPitch = mc.player.getPitch();
        }

        // Проверяем наличие необходимых предметов
        if (!hasRequiredItems()) {
            sendMessage("§cНеобходимые предметы отсутствуют!");
            return;
        }

        // Проверяем что на игроке надет нагрудник (кроме состояний полета)
        if (!hasChestplateEquipped()) {
            return;
        }

        // Обновляем информацию о земле для NoFall обхода
        updateGroundState();

        // Безопасное обновление цели
        updateTarget();

        if (target == null) {
            if (currentState != State.IDLE) {
                sendMessage("§eЦель потеряна, возврат в режим ожидания");
                currentState = State.IDLE;
            }
            return;
        }

        // Безопасное обновление позиции цели
        if (target.isAlive() && !target.isRemoved()) {
            targetPosition = target.getPos();
            lastTargetPosition = targetPosition;
        } else {
            target = null;
            return;
        }

        processStateMachine();

        // Управляем полетом и поворотами
        if (mc.player.isGliding() && (currentState == State.TAKING_OFF || currentState == State.FLYING_TO_TARGET)) {
            controlFlight();
        }

        // Обработка плавных поворотов
        handleSmoothRotations();
    }

    private void updateGroundState() {
        if (mc.player == null) return;

        boolean currentOnGround = mc.player.isOnGround();

        // Отслеживаем время в воздухе
        if (!currentOnGround) {
            ticksInAir++;
        } else {
            ticksInAir = 0;
        }

        wasOnGround = currentOnGround;
    }

    private void updateTarget() {
        // Проверяем текущую цель
        if (target != null) {
            if (!target.isAlive() || target.isRemoved() ||
                    mc.player.getPos().distanceTo(target.getPos()) > range.getValue()) {
                target = null;
                hasAttacked = false;
            }
        }

        // Ищем новую цель если нужно
        if (target == null) {
            LivingEntity newTarget = findTarget();
            if (newTarget != null) {
                target = newTarget;
                hasAttacked = false;
                sendMessage("§aНайдена цель: " + target.getName().getString() +
                        " (дистанция: " + String.format("%.1f", mc.player.getPos().distanceTo(target.getPos())) + ")");
            }
        }
    }

    private void processStateMachine() {
        switch (currentState) {
            case IDLE -> {
                if (target != null) {
                    currentState = State.PREPARING;
                    stateTimer.reset();
                    sendMessage("§aПодготовка к атаке на " + target.getName().getString());
                }
            }
            case PREPARING -> {
                if (stateTimer.passed(700)) {
                    equipElytra();
                    currentState = State.TAKING_OFF;
                    stateTimer.reset();
                    fireworkTimer.reset();
                    jumpTimer.reset();
                    groundSpoofTimer.reset();
                    hasUsedFirstFirework = false;
                    jumpAttempts = 0;
                    hasJumped = false;
                    lastY = mc.player.getY();
                    sendMessage("§aПереход к взлету!");
                }
            }
            case TAKING_OFF -> {
                if (!mc.player.isGliding()) {
                    // Безопасная логика прыжка
                    if (mc.player.isOnGround() && jumpAttempts < 2) {
                        if (jumpTimer.passed(200)) {
                            performSafeJump();
                            jumpTimer.reset();
                            jumpAttempts++;
                            hasJumped = true;
                            sendMessage("§aПрыжок #" + jumpAttempts);
                        }
                    }
                    // Активация элитры с проверкой состояния
                    else if (hasJumped && !mc.player.isOnGround() &&
                            mc.player.getVelocity().y < -0.2 && ticksInAir >= 3) {
                        if (stateTimer.passed(400)) {
                            tryActivateElytraSafely();
                            stateTimer.reset();
                        }
                    }
                } else {
                    // Использование фейерверка после активации элитры
                    if (!hasUsedFirstFirework && fireworkTimer.passed(300)) {
                        useFireworkSafely();
                        hasUsedFirstFirework = true;
                        fireworkTimer.reset();
                        sendMessage("§aИспользован фейерверк для взлета");
                    }
                }

                // Проверяем достижение нужной высоты
                if (target != null && target.isAlive() && mc.player.isGliding() &&
                        mc.player.getY() >= target.getY() + flyHeight.getValue()) {
                    currentState = State.FLYING_TO_TARGET;
                    stateTimer.reset();
                    hasUsedSecondFirework = false;
                    sendMessage("§aПолет к цели");
                }

                // Таймаут для взлета
                if (stateTimer.passed(6000)) {
                    sendMessage("§cНе удалось взлететь, повторная попытка");
                    currentState = State.PREPARING;
                    stateTimer.reset();
                }
            }
            case FLYING_TO_TARGET -> {
                if (target == null || !target.isAlive()) {
                    currentState = State.IDLE;
                    return;
                }

                Vec3d playerPos = mc.player.getPos();
                double distanceToTarget = playerPos.distanceTo(targetPosition);

                // Второй фейерверк с большей задержкой
                if (!hasUsedSecondFirework && fireworkTimer.passed(1800)) {
                    useFireworkSafely();
                    hasUsedSecondFirework = true;
                    sendMessage("§aУскорение к цели");
                }

                // Переход к атаке
                if (distanceToTarget <= attackRange.getValue() + 2) {
                    equipChestplate();
                    currentState = State.ATTACKING;
                    stateTimer.reset();
                    sendMessage("§cПодготовка к атаке!");
                }
            }
            case ATTACKING -> {
                if (target == null || !target.isAlive()) {
                    currentState = State.IDLE;
                    return;
                }

                double distanceToTarget = mc.player.getPos().distanceTo(targetPosition);
                double heightDiff = Math.abs(mc.player.getY() - target.getY());

                // Атака с более точными условиями
                if (distanceToTarget <= attackRange.getValue() + 3 && !hasAttacked && heightDiff <= 6.0) {
                    equipMace();
                    attack();
                    hasAttacked = true;
                    sendMessage("§cУдар булавой выполнен!");
                }

                // Переход к следующему циклу
                if (hasAttacked && stateTimer.passed(1200)) {
                    reset();
                    sendMessage("§aПоиск новой цели");
                } else if (distanceToTarget > attackRange.getValue() + 8 ||
                        mc.player.getY() < target.getY() - 3) {
                    if (stateTimer.passed(800)) {
                        reset();
                        sendMessage("§eПромах, повторная попытка");
                    }
                }
            }
        }
    }

    private void performSafeJump() {
        if (mc.player == null || !mc.player.isOnGround()) return;

        // Естественный прыжок без дополнительного движения
        mc.player.jump();

        // Отправляем корректный пакет движения
        sendGroundPacket(true); // На земле перед прыжком
    }

    private void tryActivateElytraSafely() {
        if (mc.player == null) return;

        var equippedChest = mc.player.getInventory().getArmorStack(2);
        if (equippedChest.getItem() != Items.ELYTRA) {
            sendMessage("§cЭлитра не надета!");
            return;
        }

        // Активируем элитру только когда падаем достаточно долго
        if (!mc.player.isOnGround() && ticksInAir >= 3 &&
                mc.player.getVelocity().y < -0.3 && !mc.player.isGliding()) {

            // Отправляем корректный пакет о том, что мы не на земле
            sendGroundPacket(false);

            NetworkUtils.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
            sendMessage("§aЭлитра активирована!");
        }
    }

    private void useFireworkSafely() {
        int fireworkSlot = findItemSlot(Items.FIREWORK_ROCKET);
        if (fireworkSlot == -1) return;

        // Сохраняем текущий слот
        int previousSlot = mc.player.getInventory().selectedSlot;

        try {
            if (fireworkSlot < 9) {
                // Предмет в хотбаре
                mc.player.getInventory().selectedSlot = fireworkSlot;
                NetworkUtils.sendPacket(new UpdateSelectedSlotC2SPacket(fireworkSlot));

                Thread.sleep(80);

                NetworkUtils.sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, 0, mc.player.getYaw(), mc.player.getPitch()));

                Thread.sleep(80);

                mc.player.getInventory().selectedSlot = previousSlot;
                NetworkUtils.sendPacket(new UpdateSelectedSlotC2SPacket(previousSlot));
            } else {
                // Предмет в инвентаре
                int emptySlot = findEmptyHotbarSlot();
                if (emptySlot != -1) {
                    InventoryUtils.bypassSwap(fireworkSlot - 9, emptySlot);

                    Thread.sleep(100);

                    mc.player.getInventory().selectedSlot = emptySlot;
                    NetworkUtils.sendPacket(new UpdateSelectedSlotC2SPacket(emptySlot));

                    Thread.sleep(80);

                    NetworkUtils.sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, 0, mc.player.getYaw(), mc.player.getPitch()));

                    Thread.sleep(80);

                    mc.player.getInventory().selectedSlot = previousSlot;
                    NetworkUtils.sendPacket(new UpdateSelectedSlotC2SPacket(previousSlot));
                }
            }
        } catch (InterruptedException ignored) {}
    }

    private void sendGroundPacket(boolean onGround) {
        if (mc.player == null) return;

        // Отправляем пакет движения с корректным статусом земли
        NetworkUtils.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(
                mc.player.getYaw(),
                mc.player.getPitch(),
                onGround,
                mc.player.horizontalCollision
        ));
    }

    private void controlFlight() {
        if (!mc.player.isGliding() || target == null || !target.isAlive()) return;

        Vec3d targetPos = target.getPos();

        switch (currentState) {
            case TAKING_OFF -> {
                // Плавный взлет с постепенным поворотом к цели
                float[] rotations = RotationUtils.getRotations(targetPos.add(0, flyHeight.getValue(), 0));
                setTargetRotation(rotations[0], Math.max(-60f, rotations[1] - 30f));
            }
            case FLYING_TO_TARGET -> {
                double currentHeight = mc.player.getY();
                double targetHeight = targetPos.y;
                double desiredHeight = targetHeight + flyHeight.getValue();

                if (currentHeight < desiredHeight) {
                    // Набираем высоту, но уже поворачиваемся к цели
                    float[] rotations = RotationUtils.getRotations(targetPos.add(0, flyHeight.getValue() + 3, 0));
                    setTargetRotation(rotations[0], -45f);
                } else {
                    // Пикируем на цель
                    float[] rotations = RotationUtils.getRotations(targetPos.add(0, target.getHeight() / 2, 0));
                    setTargetRotation(rotations[0], Math.min(60f, Math.max(rotations[1], 30f)));
                }
            }
        }
    }

    private void setTargetRotation(float yaw, float pitch) {
        if (smoothRotations.getValue()) {
            targetYaw = yaw;
            targetPitch = pitch;
            isRotating = true;
        } else {
            mc.player.setYaw(yaw);
            mc.player.setPitch(pitch);
        }
    }

    private void handleSmoothRotations() {
        if (!isRotating || !smoothRotations.getValue()) return;

        float yawDiff = targetYaw - currentYaw;
        float pitchDiff = targetPitch - currentPitch;

        // Нормализуем разность углов
        while (yawDiff > 180f) yawDiff -= 360f;
        while (yawDiff < -180f) yawDiff += 360f;

        float maxRotationSpeed = rotationSpeed.getValue();
        float yawStep = Math.max(-maxRotationSpeed, Math.min(maxRotationSpeed, yawDiff));
        float pitchStep = Math.max(-maxRotationSpeed, Math.min(maxRotationSpeed, pitchDiff));

        currentYaw += yawStep;
        currentPitch += pitchStep;

        // Ограничиваем pitch
        currentPitch = Math.max(-90f, Math.min(90f, currentPitch));

        mc.player.setYaw(currentYaw);
        mc.player.setPitch(currentPitch);

        // Проверяем завершение поворота
        if (Math.abs(yawDiff) < 2f && Math.abs(pitchDiff) < 2f) {
            isRotating = false;
        }
    }

    @EventHandler
    public void onRender3D(EventRender3D e) {
        if (!renderLine.getValue() || target == null) return;

        Vec3d playerPos = mc.player.getPos();
        Vec3d targetPos = target.getPos();

        Render3D.drawLine(e.getMatrixStack(), playerPos, targetPos, Color.RED);
    }

    private boolean hasRequiredItems() {
        boolean hasMace = false;
        boolean hasElytra = false;
        boolean hasChestplate = false;
        boolean hasFireworks = false;

        // Проверяем инвентарь
        for (int i = 0; i < 36; i++) {
            var stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() == Items.MACE) hasMace = true;
            if (stack.getItem() == Items.ELYTRA) hasElytra = true;
            if (ArmorUtils.isChestplate(stack.getItem())) hasChestplate = true;
            if (stack.getItem() == Items.FIREWORK_ROCKET) hasFireworks = true;
        }

        // Проверяем надетую броню
        var equippedChest = mc.player.getInventory().getArmorStack(2);
        if (!equippedChest.isEmpty()) {
            if (equippedChest.getItem() == Items.ELYTRA) hasElytra = true;
            if (ArmorUtils.isChestplate(equippedChest.getItem())) hasChestplate = true;
        }

        return hasMace && hasElytra && hasChestplate && hasFireworks;
    }

    private boolean hasChestplateEquipped() {
        // Во время полета не требуем нагрудник
        if (currentState == State.TAKING_OFF || currentState == State.FLYING_TO_TARGET) {
            return true;
        }

        var equippedChest = mc.player.getInventory().getArmorStack(2);
        return !equippedChest.isEmpty() && ArmorUtils.isChestplate(equippedChest.getItem());
    }

    private LivingEntity findTarget() {
        if (mc.world == null || mc.player == null) return null;

        List<LivingEntity> entities = new ArrayList<>();

        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof LivingEntity living)) continue;
            if (!Pub.getInstance().getModuleManager().getModule(Targets.class).isValid(living)) continue;
            if (mc.player.getPos().distanceTo(living.getPos()) > range.getValue()) continue;

            entities.add(living);
        }

        return entities.stream()
                .min(Comparator.comparingDouble(e -> mc.player.getPos().distanceTo(e.getPos())))
                .orElse(null);
    }

    private void equipElytra() {
        int elytraSlot = findItemSlot(Items.ELYTRA);
        if (elytraSlot == -1) {
            sendMessage("§cЭлитра не найдена!");
            return;
        }

        try {
            if (elytraSlot < 9) {
                int previousSlot = mc.player.getInventory().selectedSlot;
                mc.player.getInventory().selectedSlot = elytraSlot;
                NetworkUtils.sendPacket(new UpdateSelectedSlotC2SPacket(elytraSlot));

                Thread.sleep(120);

                NetworkUtils.sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, 0, mc.player.getYaw(), mc.player.getPitch()));

                Thread.sleep(120);

                mc.player.getInventory().selectedSlot = previousSlot;
                NetworkUtils.sendPacket(new UpdateSelectedSlotC2SPacket(previousSlot));
            } else {
                InventoryUtils.bypassSwap(elytraSlot - 9, 38);
            }
        } catch (InterruptedException ignored) {}

        sendMessage("§aЭлитра экипирована");
    }

    private void equipChestplate() {
        int chestplateSlot = findChestplateSlot();
        if (chestplateSlot == -1) return;

        try {
            if (chestplateSlot < 9) {
                int previousSlot = mc.player.getInventory().selectedSlot;
                mc.player.getInventory().selectedSlot = chestplateSlot;
                NetworkUtils.sendPacket(new UpdateSelectedSlotC2SPacket(chestplateSlot));

                Thread.sleep(80);

                NetworkUtils.sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, 0, mc.player.getYaw(), mc.player.getPitch()));

                Thread.sleep(80);

                mc.player.getInventory().selectedSlot = previousSlot;
                NetworkUtils.sendPacket(new UpdateSelectedSlotC2SPacket(previousSlot));
            } else {
                InventoryUtils.bypassSwap(chestplateSlot - 9, 38);
            }
        } catch (InterruptedException ignored) {}
    }

    private void equipMace() {
        int maceSlot = findItemSlot(Items.MACE);
        if (maceSlot == -1) return;

        if (maceSlot < 9) {
            mc.player.getInventory().selectedSlot = maceSlot;
            NetworkUtils.sendPacket(new UpdateSelectedSlotC2SPacket(maceSlot));
        } else {
            int emptyHotbarSlot = findEmptyHotbarSlot();
            if (emptyHotbarSlot != -1) {
                InventoryUtils.bypassSwap(maceSlot - 9, emptyHotbarSlot);
                mc.player.getInventory().selectedSlot = emptyHotbarSlot;
                NetworkUtils.sendPacket(new UpdateSelectedSlotC2SPacket(emptyHotbarSlot));
            }
        }
    }

    private void useFirework() {
        useFireworkSafely(); // Используем безопасную версию
    }

    private void attack() {
        if (target == null || !target.isAlive()) {
            sendMessage("§cЦель недоступна для атаки");
            return;
        }

        sendMessage("§eВыполняется атака...");

        // Точное наведение на цель
        float[] rotations = RotationUtils.getRotations(target.getPos().add(0, target.getHeight() / 2, 0));
        mc.player.setYaw(rotations[0]);
        mc.player.setPitch(rotations[1]);

        try {
            Thread.sleep(80);
        } catch (InterruptedException ignored) {}

        // Атакуем
        mc.interactionManager.attackEntity(mc.player, target);
        InventoryUtils.swing(InventoryUtils.Swing.MainHand);

        sendMessage("§aАтака отправлена!");
    }

    private int findItemSlot(net.minecraft.item.Item item) {
        // Сначала ищем в хотбаре
        for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getStack(i).getItem() == item) {
                return i;
            }
        }

        // Потом в основном инвентаре
        for (int i = 9; i < 36; i++) {
            if (mc.player.getInventory().getStack(i).getItem() == item) {
                return i;
            }
        }

        return -1;
    }

    private int findChestplateSlot() {
        // Сначала ищем в хотбаре
        for (int i = 0; i < 9; i++) {
            var stack = mc.player.getInventory().getStack(i);
            if (ArmorUtils.isChestplate(stack.getItem())) {
                return i;
            }
        }

        // Потом в основном инвентаре
        for (int i = 9; i < 36; i++) {
            var stack = mc.player.getInventory().getStack(i);
            if (ArmorUtils.isChestplate(stack.getItem())) {
                return i;
            }
        }

        return -1;
    }

    private int findEmptyHotbarSlot() {
        for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getStack(i).isEmpty()) {
                return i;
            }
        }
        return -1;
    }

    private enum State {
        IDLE,
        PREPARING,
        TAKING_OFF,
        FLYING_TO_TARGET,
        ATTACKING
    }
}