package com.november.mcphone.feature.reader.client.source;

import com.november.mcphone.feature.reader.BookRef;
import net.minecraft.client.gui.GuiGraphics;

import java.util.List;

/**
 * Patchouli capability facade for targets without a usable Patchouli build.
 *
 * <p>The direct API source is not mounted for this target. This facade keeps
 * the book-source list intact and explicitly reports Patchouli as unavailable.</p>
 */
public final class PatchouliSource implements BookSource {

    public static final String PATCHOULI_MODID = "patchouli";

    @Override
    public String id() {
        return "patchouli";
    }

    @Override
    public boolean isAvailable() {
        return false;
    }

    @Override
    public List<BookRef> list() {
        return List.of();
    }

    @Override
    public void refresh() {
    }

    @Override
    public void open(BookRef book) {
    }

    @Override
    public boolean renderIcon(GuiGraphics g, BookRef book, int x, int y, int size) {
        return false;
    }
}
