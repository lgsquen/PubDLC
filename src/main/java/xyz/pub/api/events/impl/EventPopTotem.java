package xyz.pub.api.events.impl;

import xyz.pub.api.events.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.entity.player.PlayerEntity;

@AllArgsConstructor @Getter
public class EventPopTotem extends Event {
    private final PlayerEntity player;
}