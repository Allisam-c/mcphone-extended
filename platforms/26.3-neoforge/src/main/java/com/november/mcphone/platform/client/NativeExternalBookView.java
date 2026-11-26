package com.november.mcphone.platform.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;

/** Rendering and scroll entry points for a hosted book view with deferred rendering. */
public final class NativeExternalBookView {
    private NativeExternalBookView() {}

    public static void init(Screen view, Minecraft client, int width, int height) {
        view.init(width, height);
    }

    public static void render(Screen view, GuiGraphicsExtractor graphics, int x, int y, float partialTick) {
        view.extractRenderState(graphics, x, y, partialTick);
    }

    public static boolean scroll(Screen view, double x, double y, double horizontal, double vertical) {
        return view.mouseScrolled(x, y, horizontal, vertical);
    }
}
