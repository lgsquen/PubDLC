package xyz.pub.utils.macro;

import xyz.pub.modules.settings.api.Bind;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public class Macro {
    private String name, command;
    private Bind bind;
}