package xyz.pub.modules.impl.render;

import xyz.pub.modules.api.Category;
import xyz.pub.modules.api.Module;
import xyz.pub.modules.settings.impl.BooleanSetting;
import net.minecraft.client.resource.language.I18n;

public class NoRender extends Module {

    public BooleanSetting hurtCam = new BooleanSetting(I18n.translate("settings.norender.hurtcam"), true);
    public BooleanSetting fire = new BooleanSetting(I18n.translate("settings.norender.fire"), true);
    public BooleanSetting totem = new BooleanSetting(I18n.translate("settings.norender.totem"), true);
    public BooleanSetting potions = new BooleanSetting(I18n.translate("settings.norender.potions"), true);
    public BooleanSetting blocks = new BooleanSetting(I18n.translate("settings.norender.blocks"), true);
    public BooleanSetting scoreboard = new BooleanSetting(I18n.translate("settings.norender.scoreboard"), false);
    public BooleanSetting bossBar = new BooleanSetting(I18n.translate("settings.norender.bossbar"), false);
    public BooleanSetting particles = new BooleanSetting(I18n.translate("settings.norender.particles"), true);
    public BooleanSetting armor = new BooleanSetting(I18n.translate("settings.norender.armor"), false);
    public BooleanSetting limbs = new BooleanSetting(I18n.translate("settings.norender.limbs"), false);
    public BooleanSetting clip = new BooleanSetting(I18n.translate("settings.norender.clip"), true);

    public NoRender() {
        super("NoRender", Category.Render);
    }
}