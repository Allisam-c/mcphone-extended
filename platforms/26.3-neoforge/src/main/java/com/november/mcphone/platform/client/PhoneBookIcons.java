package com.november.mcphone.platform.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import vazkii.patchouli.client.book.BookIcon;

/** Patchouli icon rendering seam for the 26.x GUI extraction pipeline. */
public final class PhoneBookIcons {

    private PhoneBookIcons() {
    }

    public static void draw(BookIcon icon, GuiGraphicsExtractor g, int x, int y) {
        icon.extractRenderState(g, x, y);
    }
}
