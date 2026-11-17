package com.november.mcphone.platform.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.ToastManager;

/** 26.x exposes the toast manager through Gui instead of Minecraft. */
public final class Toasts {
    private Toasts() {}

    public static ToastManager manager(Minecraft mc) {
        return mc.getToastManager();
    }
}
