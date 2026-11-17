package com.november.mcphone.api.client.ui;

import com.november.mcphone.platform.client.EditBoxes;
import com.november.mcphone.platform.client.PhoneScreenBase;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.network.chat.Component;

/**
 * 26.3 的多行输入框门面。
 *
 * <p>26.3 把 {@code MultiLineEditBox} 的构造器收成 builder 且设为私有，控件入口
 * {@code extractRenderState} 又是 final；继续继承会要求打开私有构造器。
 * 这里改成组合，公开 API 仍是原来的构造器与方法名，共用页面不需要知道版本差异。
 */
public final class PhoneMultiLineEditBox {

    private final MultiLineEditBox box;

    public PhoneMultiLineEditBox(Font font, int x, int y, int width, int height,
                                 Component placeholder, Component message) {
        box = MultiLineEditBox.builder()
                .setX(x)
                .setY(y)
                .setPlaceholder(placeholder)
                .build(font, width, height, message);
    }

    public void render(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
        EditBoxes.render(box, g, mouseX, mouseY, partialTick);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return EditBoxes.key(box, keyCode, scanCode, modifiers);
    }

    public boolean charTyped(char c, int modifiers) {
        return EditBoxes.character(box, c, modifiers);
    }

    public boolean mouseClicked(double x, double y, int pageButton) {
        return EditBoxes.click(box, x, y, pageButton);
    }

    public boolean mouseReleased(double x, double y, int pageButton) {
        return box.mouseReleased(mouse(x, y, pageButton));
    }

    public boolean mouseDragged(double x, double y, int pageButton, double dx, double dy) {
        return EditBoxes.drag(box, x, y, pageButton, dx, dy);
    }

    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        return box.mouseScrolled(x, y, scrollX, scrollY);
    }

    public String getValue() { return box.getValue(); }
    public void setValue(String value) { box.setValue(value); }
    public void setCharacterLimit(int limit) { box.setCharacterLimit(limit); }
    public void setFocused(boolean value) { box.setFocused(value); }
    public boolean isFocused() { return box.isFocused(); }
    public void setX(int value) { box.setX(value); }
    public void setY(int value) { box.setY(value); }
    public void setWidth(int value) { box.setWidth(value); }
    public void setHeight(int value) { box.setHeight(value); }

    private static MouseButtonEvent mouse(double x, double y, int pageButton) {
        return new MouseButtonEvent(x, y,
                new MouseButtonInfo(PhoneScreenBase.nativeButton(pageButton), 0));
    }
}
