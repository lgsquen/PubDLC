package xyz.pub.managers;

import com.mojang.brigadier.CommandDispatcher;
import xyz.pub.commands.Command;
import xyz.pub.commands.impl.*;
import xyz.pub.commands.impl.ConfigCommand;
import xyz.pub.commands.impl.FriendCommand;
import xyz.pub.commands.impl.MacroCommand;
import xyz.pub.commands.impl.WaypointCommand;
import xyz.pub.utils.Wrapper;
import lombok.*;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.command.CommandSource;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CommandManager implements Wrapper {

    private final CommandDispatcher<CommandSource> dispatcher = new CommandDispatcher<>();
    private final CommandSource source = new ClientCommandSource(null, mc);
    private final List<Command> commands = new ArrayList<>();
    @Setter private String prefix = "*";

    public CommandManager() {
        addCommands(
                new FriendCommand(),
                new ConfigCommand(),
                new MacroCommand(),
                new WaypointCommand()
        );
    }

    private void addCommands(Command... command) {
        for (Command cmd : command) {
            cmd.register(dispatcher);
            commands.add(cmd);
        }
    }
}