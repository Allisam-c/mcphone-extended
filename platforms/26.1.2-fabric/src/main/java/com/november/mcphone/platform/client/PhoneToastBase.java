package com.november.mcphone.platform.client;

import com.november.mcphone.platform.client.port.PhoneGraphics;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;

/** 通知生命周期与内容绘制的版本接缝。 */
public abstract class PhoneToastBase implements Toast {
    private Visibility wanted = Visibility.SHOW;

    @Override
    public void update(ToastManager manager, long fullyVisibleForMs) {
        wanted = shouldStillShow(fullyVisibleForMs, manager.getNotificationDisplayTimeMultiplier())
                ? Visibility.SHOW : Visibility.HIDE;
    }

    @Override
    public Visibility getWantedVisibility() {
        return wanted;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, Font font, long fullyVisibleForMs) {
        drawToast(new PhoneGraphics(g), font, fullyVisibleForMs);
    }

    protected abstract void drawToast(PhoneGraphics g, Font font, long timeSinceLastVisible);

    protected abstract boolean shouldStillShow(long timeSinceLastVisible, double displayMultiplier);
}
