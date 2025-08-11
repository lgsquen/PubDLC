package xyz.pub.modules.impl.misc;

import xyz.pub.api.events.impl.EventKey;
import xyz.pub.api.events.impl.EventMouse;
import xyz.pub.modules.api.Category;
import xyz.pub.modules.api.Module;
import xyz.pub.modules.settings.api.Bind;
import xyz.pub.modules.settings.api.Nameable;
import xyz.pub.modules.settings.impl.BindSetting;
import xyz.pub.modules.settings.impl.BooleanSetting;
import xyz.pub.modules.settings.impl.EnumSetting;
import xyz.pub.modules.settings.impl.NumberSetting;
import xyz.pub.utils.network.ChatUtils;
import xyz.pub.utils.network.NetworkUtils;
import xyz.pub.utils.world.InventoryUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

public class ElytraHelper extends Module {

    public static enum SwapMode implements Nameable {
        Normal("Normal"),
        Bypass("Bypass");

        private final String name;

        SwapMode(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    private final EnumSetting<SwapMode> swapMode = new EnumSetting<>("settings.elytrahelper.swapmode", SwapMode.Normal);
    private final BindSetting bindElytra = new BindSetting("settings.elytrahelper.bindelytra", new Bind(-1, false));
    private final BindSetting bindFirework = new BindSetting("settings.elytrahelper.bindfirework", new Bind(-1, false));
    private final BooleanSetting stopOnSwap = new BooleanSetting("settings.elytrahelper.stoponswap", false);
    private final NumberSetting swapDelay = new NumberSetting("settings.elytrahelper.swapdelay", 150, 50, 500, 10);

    public ElytraHelper() {
        super("ElytraHelper", Category.Misc);
    }

    @EventHandler
    public void onKey(EventKey e) {
        if (fullNullCheck() || mc.currentScreen != null) return;

        if (e.getAction() == 1) {
            if (e.getKey() == bindFirework.getValue().getKey() && !bindFirework.getValue().isMouse()) throwFirework();
            else if (e.getKey() == bindElytra.getValue().getKey() && !bindElytra.getValue().isMouse()) swapElytra();
        }
    }

    @EventHandler
    public void onMouse(EventMouse e) {
        if (fullNullCheck() || mc.currentScreen != null) return;

        if (e.getAction() == 1) {
            if (e.getButton() == bindFirework.getValue().getKey() && bindFirework.getValue().isMouse()) throwFirework();
            else if (e.getButton() == bindElytra.getValue().getKey() && bindElytra.getValue().isMouse()) swapElytra();
        }
    }

    private void throwFirework() {
        if (!mc.player.isGliding()) return;
        int slot = InventoryUtils.find(Items.FIREWORK_ROCKET, 0, 8);
        int previousSlot = mc.player.getInventory().selectedSlot;
        if (slot == -1) return;
        InventoryUtils.switchSlot(InventoryUtils.Switch.Silent, slot, previousSlot);
        NetworkUtils.sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, 0, mc.player.getYaw(), mc.player.getPitch()));
        mc.player.swingHand(Hand.MAIN_HAND);
        InventoryUtils.switchBack(InventoryUtils.Switch.Silent, slot, previousSlot);
        ChatUtils.sendMessage(I18n.translate("modules.elytrahelper.threwfirework"));
    }

    private void swapElytra() {
        boolean elytra = mc.player.getInventory().getArmorStack(2).getItem() == Items.ELYTRA;
        int slot = elytra ? InventoryUtils.findBestChestplate(0, 35) : InventoryUtils.find(Items.ELYTRA);
        if (slot == -1) {
            ChatUtils.sendMessage(I18n.translate(elytra ? "modules.elytrahelper.chestplatenotfound" : "modules.elytrahelper.elytranotfound"));
            return;
        }

        // Сохраняем текущую скорость если включена остановка при свапе
        Vec3d savedVelocity = null;
        if (stopOnSwap.getValue()) {
            savedVelocity = mc.player.getVelocity();
            mc.player.setVelocity(0, mc.player.getVelocity().y, 0);
        }

        // Выбираем режим свапа
        if (swapMode.getValue() == SwapMode.Bypass) {
            performBypassSwap(slot, savedVelocity, elytra);
        } else {
            InventoryUtils.swap(slot, 6);

            // Восстанавливаем скорость после свапа
            if (stopOnSwap.getValue() && savedVelocity != null) {
                mc.player.setVelocity(savedVelocity);
            }

            ChatUtils.sendMessage(I18n.translate(elytra ? "modules.elytrahelper.swappedonchestplate" : "modules.elytrahelper.swappedonelytra"));
        }
    }

    private void performBypassSwap(int slot, Vec3d savedVelocity, boolean elytra) {
        new Thread(() -> {
            try {
                // Имитируем открытие инвентаря
                NetworkUtils.sendPacket(new net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket(mc.player, net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode.OPEN_INVENTORY));

                // Небольшая задержка перед свапом
                Thread.sleep(swapDelay.getValue().longValue() / 3);

                // Выполняем свап с задержками между кликами
                mc.interactionManager.clickSlot(mc.player.playerScreenHandler.syncId, InventoryUtils.indexToSlot(slot), 0, net.minecraft.screen.slot.SlotActionType.PICKUP, mc.player);
                Thread.sleep(swapDelay.getValue().longValue() / 3);

                mc.interactionManager.clickSlot(mc.player.playerScreenHandler.syncId, 6, 0, net.minecraft.screen.slot.SlotActionType.PICKUP, mc.player);
                Thread.sleep(swapDelay.getValue().longValue() / 3);

                mc.interactionManager.clickSlot(mc.player.playerScreenHandler.syncId, InventoryUtils.indexToSlot(slot), 0, net.minecraft.screen.slot.SlotActionType.PICKUP, mc.player);

                // Небольшая задержка перед закрытием
                Thread.sleep(50);

                // Закрываем инвентарь
                NetworkUtils.sendPacket(new net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket(mc.player.playerScreenHandler.syncId));

                // Восстанавливаем скорость после свапа
                if (stopOnSwap.getValue() && savedVelocity != null) {
                    mc.player.setVelocity(savedVelocity);
                }

                ChatUtils.sendMessage(I18n.translate(elytra ? "modules.elytrahelper.swappedonchestplate" : "modules.elytrahelper.swappedonelytra"));

            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
}