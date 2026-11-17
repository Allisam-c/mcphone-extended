package com.november.mcphone.platform.client;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.DynamicTexture;

import java.util.function.Supplier;

/** Dynamic-texture and pixel-writing seam for 26.x. */
public final class Textures {
    private Textures() {}

    public static DynamicTexture dynamicTexture(Supplier<String> label, NativeImage image) {
        return new DynamicTexture(label, image);
    }

    public static void setPixelABGR(NativeImage image, int x, int y, int abgr) {
        image.setPixelABGR(x, y, abgr);
    }
}
