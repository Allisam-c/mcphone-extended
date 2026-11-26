package com.november.mcphone.platform.client;

import com.mojang.blaze3d.platform.NativeImage;
import java.util.function.Supplier;
import net.minecraft.client.renderer.texture.DynamicTexture;

/**
 * 动态纹理与像素写入的版本接缝。
 *
 * <p>26.x 两处同时换形：{@code DynamicTexture} 多要一个纹理标签
 * （{@code (Supplier<String>, NativeImage)}），{@code NativeImage} 的写入口从
 * {@code setPixelRGBA} 改成 {@code setPixelABGR}。两代的参数含义都是 ABGR
 * （1.21.1 那个方法虽然叫 RGBA，形参名与实现都是 ABGR），所以这一层只负责换名字。
 */
public final class Textures {

    private Textures() {}

    public static DynamicTexture dynamicTexture(Supplier<String> label, NativeImage image) {
        return new DynamicTexture(image);
    }

    public static void setPixelABGR(NativeImage image, int x, int y, int abgr) {
        image.setPixelRGBA(x, y, abgr);
    }
}
