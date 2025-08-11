package xyz.pub.modules.impl.misc;

import xyz.pub.modules.api.Category;
import xyz.pub.modules.api.Module;
import xyz.pub.modules.settings.impl.BooleanSetting;

public class NoPush extends Module {

    public BooleanSetting players = new BooleanSetting("settings.nopush.players", true);
    public BooleanSetting blocks = new BooleanSetting("settings.nopush.blocks", true);
    public BooleanSetting water = new BooleanSetting("settings.nopush.water", true);

    public NoPush() {
        super("NoPush", Category.Misc);
    }
}