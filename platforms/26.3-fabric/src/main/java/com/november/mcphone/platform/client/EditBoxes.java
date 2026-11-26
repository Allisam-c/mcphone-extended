package com.november.mcphone.platform.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;

/** 26.3 input event and widget rendering seam. */
public final class EditBoxes {

    private EditBoxes() {}

    public static void moveCursorToEnd(EditBox box) {
        box.moveCursorToEnd(false);
    }

    public static boolean click(AbstractWidget w, double mx, double my, int pageButton) {
        return w.mouseClicked(new MouseButtonEvent(mx, my,
                new MouseButtonInfo(PhoneScreenBase.nativeButton(pageButton), 0)), false);
    }

    public static boolean drag(AbstractWidget w, double mx, double my, int pageButton,
                               double dx, double dy) {
        return w.mouseDragged(new MouseButtonEvent(mx, my,
                new MouseButtonInfo(PhoneScreenBase.nativeButton(pageButton), 0)), dx, dy);
    }

    public static boolean key(AbstractWidget w, int keyCode, int scanCode, int modifiers) {
        return w.keyPressed(new KeyEvent(scanCode, keyCode, modifiers));
    }

    public static boolean character(AbstractWidget w, char c, int modifiers) {
        return w.charTyped(new CharacterEvent(c));
    }

    public static void render(AbstractWidget w, GuiGraphicsExtractor g, int mouseX, int mouseY,
                              float partialTick) {
        w.extractRenderState(g, mouseX, mouseY, partialTick);
    }

    public static void tick(EditBox box) {
    }
}
