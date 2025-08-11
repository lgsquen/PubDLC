package xyz.pub.api.mixins;

import xyz.pub.Pub;
import xyz.pub.api.events.impl.EventKeyboardInput;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public abstract class KeyboardInputMixin extends Input {

    @Inject(method = "tick", at = @At("RETURN"))
    public void tick(CallbackInfo ci) {
        EventKeyboardInput event = new EventKeyboardInput(movementForward, movementSideways);
        Pub.getInstance().getEventHandler().post(event);
        movementForward = event.getMovementForward();
        movementSideways = event.getMovementSideways();
    }
}