package com.november.mcphone.platform.client.port;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.DynamicTexture;
import java.util.function.Supplier;
/** 新版要求给 GPU 纹理提供调试标签。像素与释放流程保持原版。 */
public class PhoneDynamicTexture extends DynamicTexture {
    public PhoneDynamicTexture(Supplier<String> label, NativeImage image) { super(label, image); }
    public PhoneDynamicTexture(NativeImage image) { super(() -> "MCphone 图片",image); }
}
