package xyz.pub.managers;

import com.google.common.collect.Lists;
import xyz.pub.Pub;
import xyz.pub.api.events.impl.EventRender2D;
import xyz.pub.modules.api.Module;
import xyz.pub.utils.Wrapper;
import xyz.pub.utils.notify.Notify;
import meteordevelopment.orbit.EventHandler;

import java.util.*;

public class NotifyManager implements Wrapper {

    public NotifyManager() {
        Pub.getInstance().getEventHandler().subscribe(this);
    }

    private final List<Notify> notifies = new ArrayList<>();

    public void add(Notify notify) {
        notifies.add(notify);
    }

    @EventHandler
    public void onRender2D(EventRender2D e) {
        if (Module.fullNullCheck() || Pub.getInstance().isPanic()) return;
        if (notifies.isEmpty()) return;
        float startY = mc.getWindow().getScaledHeight() / 2f + 26;
        if (notifies.size() > 10) notifies.removeFirst();
        notifies.removeIf(Notify::expired);

        for (Notify notify : Lists.newArrayList(notifies)) {
            startY = (startY - 16f);
            notify.render(e, startY + (notifies.size() * 16f));
        }
    }
}