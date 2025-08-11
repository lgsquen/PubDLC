package xyz.pub.modules.impl.movement;

import xyz.pub.Pub;
import xyz.pub.api.events.impl.EventTick;
import xyz.pub.modules.api.Category;
import xyz.pub.modules.api.Module;
import xyz.pub.modules.impl.combat.Aura;
import meteordevelopment.orbit.EventHandler;

public class Sprint extends Module {
	
    public Sprint() {
        super("Sprint", Category.Movement);
    }

    @EventHandler
    public void onTick(EventTick e) {
        if (fullNullCheck()) return;

        if (Pub.getInstance().getModuleManager().getModule(Aura.class).isToggled()
        		&& Pub.getInstance().getModuleManager().getModule(Aura.class).getTarget() != null
                && Pub.getInstance().getModuleManager().getModule(Aura.class).sprint.getValue() == Aura.Sprint.Legit) {
        	if (mc.player.getAbilities().flying
        			|| mc.player.isRiding()
        			|| Pub.getInstance().getServerManager().getFallDistance() <= 0f
        			&& mc.player.isOnGround()) {
        		mc.options.sprintKey.setPressed(true);
        	} else {
                mc.options.sprintKey.setPressed(false);
                mc.player.setSprinting(false);
        	}
        } else mc.options.sprintKey.setPressed(true);
    }
}