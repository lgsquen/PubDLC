package xyz.pub.modules.settings.impl;

import xyz.pub.modules.settings.Setting;
import xyz.pub.modules.settings.api.Position;

import java.util.function.Supplier;

public class PositionSetting extends Setting<Position> {

    public PositionSetting(String name, Position defaultValue) {
        super(name, defaultValue);
    }

    public PositionSetting(String name, Position defaultValue, Supplier<Boolean> visible) {
        super(name, defaultValue, visible);
    }
}