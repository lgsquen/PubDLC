package xyz.pub.api.events.impl.rotations;

import xyz.pub.api.events.Event;
import lombok.*;

@AllArgsConstructor @Getter @Setter
public class EventJump extends Event {
    private float yaw;
}