package xyz.pub.managers;

import xyz.pub.Pub;
import xyz.pub.api.events.impl.EventMouse;
import xyz.pub.modules.api.Category;
import xyz.pub.modules.api.Module;
import xyz.pub.api.events.impl.EventKey;
import xyz.pub.modules.impl.combat.*;
import xyz.pub.modules.impl.movement.*;
import xyz.pub.modules.impl.render.*;
import xyz.pub.modules.impl.misc.*;
import xyz.pub.modules.impl.client.*;
import xyz.pub.modules.impl.client.Panic;
import xyz.pub.modules.impl.client.RPC;
import xyz.pub.modules.impl.client.Targets;
import xyz.pub.modules.impl.client.UI;
import xyz.pub.modules.impl.combat.*;
import xyz.pub.modules.impl.misc.*;
import xyz.pub.modules.impl.movement.*;
import xyz.pub.modules.impl.render.*;
import xyz.pub.modules.settings.Setting;
import xyz.pub.utils.Wrapper;
import lombok.Getter;
import meteordevelopment.orbit.EventHandler;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class ModuleManager implements Wrapper {

    private final List<Module> modules = new ArrayList<>();

    public ModuleManager() {
        Pub.getInstance().getEventHandler().subscribe(this);
        addModules(
                new MaceTarget(),
                new Trigger(),
                new Pencil(),
                new Sprint(),
                new UI(),
                new NameTags(),
                new GuiMove(),
                new RPC(),
                new MultiTask(),
                new Aura(),
                new NoPush(),
                new NoRender(),
                new FastUse(),
                new MoveFix(),
                new FakeLag(),
                new NoAttackCooldown(),
                new NoSlow(),
                new AutoVault(),
                new WindHop(),
                new TargetEsp(),
                new NoFriendDamage(),
                new NoEntityTrace(),
                new Panic(),
                new NoJumpDelay(),
                new ItemHelper(),
                new AutoBuy(),
                new AuctionHelper(),
                new OffHand(),
                new AntiBot(),
                new DamageParticles(),
                new Fullbright(),
                new ElytraHelper(),
                new ClickPearl(),
                new ElytraForward(),
                new ElytraBooster(),
                new Targets(),
                new Teams(),
                new Scaffold(),
                new FuntimeHelper(),
                new AutoAccept(),
                new PotionTracker(),
                new UseTracker(),
                new ViewModel(),
                new ScoreboardHealth()
        );

        for (Module module : modules) {
            try {
                for (Field field : module.getClass().getDeclaredFields()) {
                    if (!Setting.class.isAssignableFrom(field.getType())) continue;
                    field.setAccessible(true);
                    Setting<?> setting = (Setting<?>) field.get(module);
                    if (setting != null && !module.getSettings().contains(setting)) module.getSettings().add(setting);
                }
            } catch (Exception ignored) {}
        }
    }

    private void addModules(Module... module) {
        this.modules.addAll(List.of(module));
    }

    @EventHandler
    public void onKey(EventKey e) {
        if (Module.fullNullCheck() || mc.currentScreen != null || Pub.getInstance().isPanic()) return;

        if (e.getAction() == 1)
            for (Module module : modules)
                if (module.getBind().getKey() == e.getKey() && !module.getBind().isMouse())
                    module.toggle();
    }

    @EventHandler
    public void onMouse(EventMouse e) {
        if (Module.fullNullCheck() || mc.currentScreen != null || Pub.getInstance().isPanic()) return;

        if (e.getAction() == 1)
            for (Module module : modules)
                if (module.getBind().getKey() == e.getButton() && module.getBind().isMouse())
                    module.toggle();
    }

    public List<Module> getModules(Category category) {
        return modules.stream().filter(m -> m.getCategory() == category).toList();
    }

    public List<Category> getCategories() {
        return Arrays.asList(Category.values());
    }

    public <T extends Module> T getModule(Class<T> clazz) {
        for (Module module : modules) {
            if (!clazz.isInstance(module)) continue;
            return (T) module;
        }
        return null;
    }
}