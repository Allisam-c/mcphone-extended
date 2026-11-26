package com.november.mcphone.core.client;

import com.november.mcphone.api.client.app.IPhoneApp;
import net.minecraft.client.gui.GuiGraphics;

final class HomeDragIcon {
    private HomeDragIcon() {
    }

    static void draw(GuiGraphics g, IPhoneApp app, int x, int y, int size, float partialTick, float opacity) {
        // 静态贴图和逐帧截取图集的 App 共用原有绘制方式，保留绘制区域和动画帧。
        app.renderIcon(g, x, y, size, partialTick);
    }
}
