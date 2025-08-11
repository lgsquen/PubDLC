package xyz.pub.commands.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import xyz.pub.Pub;
import xyz.pub.commands.Command;
import xyz.pub.utils.network.ChatUtils;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.command.CommandSource;

import java.io.File;
import java.util.Arrays;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static com.mojang.brigadier.arguments.StringArgumentType.word;

public class ConfigCommand extends Command {

    public ConfigCommand() {
        super("config");
    }

    @Override
    public void execute(LiteralArgumentBuilder<CommandSource> builder) {
        builder
                .then(literal("save")
                        .then(arg("config", word())
                                .suggests((context, builder1) -> {
                                    if (Pub.getInstance().getConfigsDir().exists() && Pub.getInstance().getConfigsDir().isDirectory()) {
                                        File[] files = Pub.getInstance().getConfigsDir().listFiles((dir, name) -> name.toLowerCase().endsWith(".pub"));

                                        if (files != null) {
                                            Arrays.stream(files)
                                                    .map(File::getName)
                                                    .map(name -> name.replace(".pub", ""))
                                                    .forEach(builder1::suggest);
                                        }
                                    }

                                    return builder1.buildFuture();
                                })
                                .executes(context -> {
                                    String config = context.getArgument("config", String.class);
                                    try {
                                        Pub.getInstance().getConfigManager().saveConfig(config);
                                        ChatUtils.sendMessage(I18n.translate("commands.config.save", config));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    return SINGLE_SUCCESS;
                                })
                        )
                )
                .then(literal("load")
                        .then(arg("config", word())
                                .suggests((context, builder1) -> {
                                    if (Pub.getInstance().getConfigsDir().exists() && Pub.getInstance().getConfigsDir().isDirectory()) {
                                        File[] files = Pub.getInstance().getConfigsDir().listFiles((dir, name) -> name.toLowerCase().endsWith(".pub"));

                                        if (files != null) {
                                            Arrays.stream(files)
                                                    .map(File::getName)
                                                    .map(name -> name.replace(".pub", ""))
                                                    .forEach(builder1::suggest);
                                        }
                                    }

                                    return builder1.buildFuture();
                                })
                                .executes(context -> {
                                    String config = context.getArgument("config", String.class);
                                    try {
                                        Pub.getInstance().getConfigManager().loadConfig(config);
                                        ChatUtils.sendMessage(I18n.translate("commands.config.load", config));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    return SINGLE_SUCCESS;
                                })
                        )
                )
                .then(literal("list")
                        .executes(context -> {
                            StringBuilder builder1 = new StringBuilder();
                            File[] files = Pub.getInstance().getConfigsDir().listFiles((dir, name) -> name.toLowerCase().endsWith(".pub"));

                            if (files == null || files.length == 0) ChatUtils.sendMessage(I18n.translate("commands.config.empty"));
                            else {
                                for (int i = 0; i < files.length; i++) {
                                    String fileName = files[i].getName().replace(".pub", "");
                                    builder1.append(fileName);
                                    if (i < files.length - 1) builder1.append(", ");
                                }

                                builder1.append(".");
                                ChatUtils.sendMessage(I18n.translate("commands.config.list") + builder1);
                            }

                            return SINGLE_SUCCESS;
                        })
                );
    }
}