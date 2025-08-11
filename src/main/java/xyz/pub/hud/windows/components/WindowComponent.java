package xyz.pub.hud.windows.components;

import xyz.pub.screen.clickgui.components.Component;
import xyz.pub.utils.animations.Animation;
import lombok.*;

@Getter @Setter
public abstract class WindowComponent extends Component {
	protected Animation animation;

	public WindowComponent(String name) {
		super(name);
	}
}