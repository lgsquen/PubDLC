package xyz.pub.api.events.impl;

import xyz.pub.api.events.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.network.packet.Packet;

@AllArgsConstructor @Getter
public class EventPacket extends Event {
    private final Packet<?> packet;

    public static class Receive extends EventPacket {
        public Receive(Packet<?> packet) {
            super(packet);
        }
    }

    public static class Send extends EventPacket {
        public Send(Packet<?> packet) {
            super(packet);
        }
    }

    public static class All extends EventPacket {
        public All(Packet<?> packet) {
            super(packet);
        }
    }
}