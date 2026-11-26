package com.november.mcphone.mixin.client;

import com.november.mcphone.feature.camera.client.CameraHandler;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 26.1.2 的原版 HUD 仍是 {@link Gui}，取景框必须单独挂在整段 HUD 的提取尾部，
 * 否则相机模式隐藏原版 HUD 时，挂在 hotbar 后面的取景框也会一起消失。
 */
@Mixin(Gui.class)
public abstract class CameraHudMixin {

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void mcphone$renderCameraOverlay(GuiGraphicsExtractor graphics,
                                              DeltaTracker deltaTracker,
                                              CallbackInfo ci) {
        CameraHandler.onRenderGui(graphics, deltaTracker);
    }
}
