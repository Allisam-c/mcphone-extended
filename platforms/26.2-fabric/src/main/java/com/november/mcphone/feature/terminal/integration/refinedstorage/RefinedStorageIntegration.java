package com.november.mcphone.feature.terminal.integration.refinedstorage;

import com.november.mcphone.feature.terminal.integration.TerminalIntegration;
import com.november.mcphone.feature.terminal.integration.TerminalSource;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/** Stable identity for Refined Storage when no 26.2 integration artifact exists. */
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
