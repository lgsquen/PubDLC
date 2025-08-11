package xyz.pub.api.events.impl.rotations;

import xyz.pub.api.events.Event;
import lombok.*;

@AllArgsConstructor @Getter @Setter
public class EventTrace extends Event {
    private float yaw, pitch;
}