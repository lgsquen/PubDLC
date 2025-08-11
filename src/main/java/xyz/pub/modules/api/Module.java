package xyz.pub.modules.api;

import xyz.pub.Pub;
import xyz.pub.modules.settings.Setting;
import xyz.pub.modules.settings.api.Bind;
import xyz.pub.utils.Wrapper;
import xyz.pub.utils.notify.Notify;
import xyz.pub.utils.notify.NotifyIcons;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class Module implements Wrapper {
    private final String name, description;
    private final Category category;
    protected boolean toggled;
    @Setter private Bind bind = new Bind(-1, false);
    private final List<Setting<?>> settings = new ArrayList<>();

    public Module(String name, Category category) {
        this.name = name;
        this.category = category;
        this.description = "descriptions" + "." + category.name().toLowerCase() + "." + name.toLowerCase();
    }

    public void onEnable() {
        toggled = true;
        Pub.getInstance().getEventHandler().subscribe(this);
        if (!fullNullCheck()) Pub.getInstance().getNotifyManager().add(new Notify(NotifyIcons.successIcon, "Feature " + name + " was enable", 1000));
    }

    public void onDisable() {
        toggled = false;
        Pub.getInstance().getEventHandler().unsubscribe(this);
        if (!fullNullCheck()) Pub.getInstance().getNotifyManager().add(new Notify(NotifyIcons.failIcon, "Feature " + name + " was disable", 1000));
    }

    public void setToggled(boolean toggled) {
        if (toggled) onEnable();
        else onDisable();
    }

    public void toggle() {
        setToggled(!toggled);
    }

    public static boolean fullNullCheck() {
        return mc.player == null || mc.world == null;
    }
}