package xyz.pub.modules.impl.client;

import xyz.pub.api.events.impl.EventTick;
import xyz.pub.modules.api.Category;
import xyz.pub.modules.api.Module;
import xyz.pub.modules.settings.impl.BooleanSetting;
import meteordevelopment.discordipc.DiscordIPC;
import meteordevelopment.discordipc.RichPresence;
import meteordevelopment.orbit.EventHandler;

public class RPC extends Module {

    private final RichPresence rpc = new RichPresence();

    public BooleanSetting uid = new BooleanSetting("settings.rpc.uid", true);
    public BooleanSetting server = new BooleanSetting("settings.rpc.server", true);

    public RPC() {
        super("RPC", Category.Client);
    }

    @EventHandler
    public void onTick(EventTick e) {
        if (server.getValue()) {
            if (mc.getNetworkHandler() != null
                    && mc.getNetworkHandler().getConnection() != null
                    && mc.getCurrentServerEntry() != null
            ) rpc.setDetails("playing: " + mc.getCurrentServerEntry().address);
            else if (mc.isInSingleplayer()) rpc.setDetails("playing: singleplayer");
            else rpc.setDetails("in menu");
        }
        if (uid.getValue()) rpc.setState("uid: " + "1337");
        DiscordIPC.setActivity(rpc);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        DiscordIPC.start(1367135636548747375L, null);
        rpc.setStart(System.currentTimeMillis() / 1000);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        DiscordIPC.stop();
    }
}