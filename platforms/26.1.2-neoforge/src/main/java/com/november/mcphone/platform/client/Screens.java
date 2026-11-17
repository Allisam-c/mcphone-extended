package com.november.mcphone.platform.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

/** 26.1 仍直接暴露 {@code Minecraft.screen}；26.3 改走 Gui 的访问器。 */
public final class Screens {

    private Screens() {}

    public static Screen current(Minecraft mc) {
        return mc == null ? null : mc.screen;
    }
}
