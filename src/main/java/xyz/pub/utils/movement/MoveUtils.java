package xyz.pub.utils.movement;

import net.minecraft.util.math.MathHelper;
import xyz.pub.api.events.impl.EventInput;
import xyz.pub.utils.Wrapper;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MoveUtils implements Wrapper {

    public boolean isMoving() {
        return mc.player.input.movementForward != 0 || mc.player.input.movementSideways != 0;
    }

}
