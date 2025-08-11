package xyz.pub.api.events.impl;

import xyz.pub.api.events.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public class EventKey extends Event {
    private int key, action, modifiers;
}