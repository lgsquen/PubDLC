package xyz.pub.modules.impl.misc;

import xyz.pub.Pub;
import xyz.pub.api.events.impl.EventPacket;
import xyz.pub.modules.api.Category;
import xyz.pub.modules.api.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;

public class AutoAccept extends Module {

    public AutoAccept() {
        super("AutoAccept", Category.Misc);
    }

    @EventHandler
    public void onPacketReceive(EventPacket.Receive e) {
        if (fullNullCheck()) return;

        if (e.getPacket() instanceof GameMessageS2CPacket packet
                && (packet.content().getString().toLowerCase().contains("хочет")
                || packet.content().getString().toLowerCase().contains("телепортироваться")
                || packet.content().getString().toLowerCase().contains("к вам")
                || packet.content().getString().toLowerCase().contains("wants")
                || packet.content().getString().toLowerCase().contains("teleport")
                || packet.content().getString().toLowerCase().contains("to you"))) {
            for (String name : Pub.getInstance().getFriendManager().getFriends()) {
                if (packet.content().getString().contains(name)) {
                    mc.getNetworkHandler().sendChatCommand("tpaccept");
                    break;
                }
            }
        }
    }
}