package com.november.mcphone.platform.client;

import com.mojang.blaze3d.platform.NativeImage;
import java.util.function.Supplier;
import net.minecraft.client.renderer.texture.DynamicTexture;

/**
 * 动态纹理与像素写入的版本接缝。
 *
 * <p>26.x 的 {@code DynamicTexture} 构造器要一个纹理标签
 * （{@code (Supplier<String>, NativeImage)}），{@code NativeImage} 的写入口叫
 * {@code setPixelABGR}；1.20.1 / 1.21.1 分别是 {@code (NativeImage)} 与
 * {@code setPixelRGBA}。两代的参数含义都是 ABGR，所以这一层只换名字。
 */
public final class Textures {

    private Textures() {}

    public static DynamicTexture dynamicTexture(Supplier<String> label, NativeImage image) {
        return new DynamicTexture(label, image);
    }

    public static void setPixelABGR(NativeImage image, int x, int y, int abgr) {
        image.setPixelABGR(x, y, abgr);
    }
}
