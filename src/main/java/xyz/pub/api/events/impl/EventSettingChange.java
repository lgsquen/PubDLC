package xyz.pub.api.events.impl;

import xyz.pub.api.events.Event;
import xyz.pub.modules.settings.Setting;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public class EventSettingChange extends Event {
    private final Setting<?> setting;
}