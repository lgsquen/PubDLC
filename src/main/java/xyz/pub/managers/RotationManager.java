package xyz.pub.managers;

import xyz.pub.Pub;
import xyz.pub.api.events.impl.*;
import xyz.pub.api.events.impl.EventKeyboardInput;
import xyz.pub.api.events.impl.rotations.EventJump;
import xyz.pub.api.events.impl.rotations.EventMotion;
import xyz.pub.api.events.impl.rotations.EventTrace;
import xyz.pub.api.events.impl.rotations.EventTravel;
import xyz.pub.modules.api.Module;
import xyz.pub.modules.impl.movement.MoveFix;
import xyz.pub.utils.Wrapper;
import xyz.pub.utils.network.NetworkUtils;
import xyz.pub.utils.rotations.RotationChanger;
import xyz.pub.utils.rotations.RotationData;
import lombok.Getter;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RotationManager implements Wrapper {

    private final List<RotationChanger> changers = new ArrayList<>();
    @Getter private final RotationData rotationData = new RotationData();

    public RotationManager() {
        Pub.getInstance().getEventHandler().subscribe(this);
    }

    public void addRotation(RotationChanger changer) {
        if (Module.fullNullCheck()) return;

        if (!changers.contains(changer)) {
            changers.add(changer);
            sortRotations();
        }

        rotationData.setRotation(changers.getFirst().rotations().get()[0], changers.getFirst().rotations().get()[1]);
    }

    public void removeRotation(RotationChanger changer) {
        if (Module.fullNullCheck()) return;

        changers.remove(changer);
        sortRotations();
    }

    public void addPacketRotation(float[] rotations) {
        if (Module.fullNullCheck()
                || rotations[0] == Pub.getInstance().getServerManager().getServerYaw()
                || rotations[1] == Pub.getInstance().getServerManager().getServerPitch()
        ) return;

        NetworkUtils.sendPacket(new PlayerMoveC2SPacket.Full(
                Pub.getInstance().getServerManager().getServerX(),
                Pub.getInstance().getServerManager().getServerY(),
                Pub.getInstance().getServerManager().getServerZ(),
                rotations[0],
                rotations[1],
                Pub.getInstance().getServerManager().isServerOnGround(),
                Pub.getInstance().getServerManager().isServerHorizontalCollision()
        ));
    }

    public boolean isEmpty() {
        return changers.isEmpty();
    }

    private void sortRotations() {
        changers.sort(Comparator.comparing(RotationChanger::priority));
        Collections.reverse(changers);
    }

    @EventHandler
    public void onTrace(EventTrace e) {
    	if (Module.fullNullCheck()|| changers.isEmpty()|| !Pub.getInstance().getModuleManager().getModule(MoveFix.class).isToggled()) return;
    	
    	if (changers.getFirst().remove().get()) removeRotation(changers.getFirst());

        e.setYaw(rotationData.getYaw());
        e.setPitch(rotationData.getPitch());
        e.cancel();
    }

    @EventHandler
    public void onMotion(EventMotion e) {
        if (Module.fullNullCheck() || changers.isEmpty()) return;

    	if (changers.getFirst().remove().get()) removeRotation(changers.getFirst());
        
        e.setYaw(rotationData.getYaw());
        e.setPitch(rotationData.getPitch());
        mc.player.setHeadYaw(rotationData.getYaw());
        mc.player.setBodyYaw(rotationData.getYaw());
    }

    @EventHandler
    public void onTravel(EventTravel e) {
        if (Module.fullNullCheck() || changers.isEmpty() || !Pub.getInstance().getModuleManager().getModule(MoveFix.class).isToggled()) return;

    	if (changers.getFirst().remove().get()) removeRotation(changers.getFirst());
        
        e.setYaw(rotationData.getYaw());
        e.setPitch(rotationData.getPitch());
    }

    @EventHandler
    public void onKeyboardInput(EventKeyboardInput e) {
        if (Module.fullNullCheck()|| changers.isEmpty() || !Pub.getInstance().getModuleManager().getModule(MoveFix.class).isToggled()) return;
        
    	if (changers.getFirst().remove().get()) removeRotation(changers.getFirst());

        e.setYaw(rotationData.getYaw(), mc.player.getYaw());
    }

    @EventHandler
    public void onJump(EventJump e) {
        if (Module.fullNullCheck() || changers.isEmpty()) return;
        
    	if (changers.getFirst().remove().get()) removeRotation(changers.getFirst());

        e.setYaw(rotationData.getYaw());
    }
}