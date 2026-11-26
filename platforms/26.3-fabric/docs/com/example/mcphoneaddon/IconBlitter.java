package com.example.mcphoneaddon;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

/** Platform copy of the API example's dynamic icon draw. */
final class IconBlitter {

    private IconBlitter() {}

    static void blit(GuiGraphicsExtractor g, Identifier texture,
                     int x, int y, int size, int frame, int frames) {
        g.blit(RenderPipelines.GUI_TEXTURED, texture, x, y,
                frame * size, 0, size, size, size, size, size * frames, size, -1);
    }
}
