package com.november.mcphone.test;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

/** 26.3 的命令源构造器不再收旧版的 level/string/entity 组合。 */
public final class CommandSourceStackFactory {
    private CommandSourceStackFactory() {}

    public static CommandSourceStack create(int level) {
        return new CommandSourceStack(
                CommandSource.NULL, Vec3.ZERO, Vec2.ZERO, null,
                CommandPermissions.level(level), Component.literal("t"), null);
    }
}
