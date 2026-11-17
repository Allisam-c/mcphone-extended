package com.november.mcphone.feature.terminal.integration.refinedstorage;

import com.november.mcphone.feature.terminal.integration.TerminalIntegration;
import com.november.mcphone.feature.terminal.integration.TerminalSource;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/**
 * Refined Storage 的 26.3 占位接入。
 *
 * RS 3 目前没有 26.3 构件，因此这里刻意不引用任何 {@code com.refinedmods} 类型。实现只保留
 * {@code Terminals} 与 {@code TerminalApp} 编译所需的稳定身份：即使运行时检测到旧版
 * {@code refinedstorage}，也不会认领或打开任何物品。
 */
public final class RefinedStorageIntegration implements TerminalIntegration {

    public static final String MODID = "refinedstorage";
    public static final String NAME = "Refined Storage";

    @Override
    public String modId() {
        return MODID;
    }

    @Override
    public String displayName() {
        return NAME;
    }

    @Override
    public boolean claims(ItemStack stack) {
        return false;
    }

    @Override
    public boolean open(ServerPlayer player, ItemStack stack, TerminalSource source) {
        return false;
    }
}
