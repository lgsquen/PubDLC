package xyz.pub.modules.settings.impl;

import xyz.pub.modules.settings.Setting;
import xyz.pub.modules.settings.api.Bind;

import java.util.function.Supplier;

public class BindSetting extends Setting<Bind> {

    public BindSetting(String name, Bind defaultValue) {
        super(name, defaultValue);
    }

    public BindSetting(String name, Bind defaultValue, Supplier<Boolean> visible) {
        super(name, defaultValue, visible);
    }
}