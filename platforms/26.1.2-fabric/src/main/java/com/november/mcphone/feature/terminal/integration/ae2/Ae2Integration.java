package com.november.mcphone.feature.terminal.integration.ae2;

import com.november.mcphone.feature.terminal.integration.TerminalIntegration;
import com.november.mcphone.feature.terminal.integration.TerminalSource;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/** Stable identity for AE2 when this Fabric target has no runnable integration. */
public final class Ae2Integration implements TerminalIntegration {
    public static final String MODID = "ae2";
    public static final String NAME = "Applied Energistics 2";

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
