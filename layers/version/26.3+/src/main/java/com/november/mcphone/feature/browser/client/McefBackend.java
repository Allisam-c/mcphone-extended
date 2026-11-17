package com.november.mcphone.feature.browser.client;

import net.minecraft.network.chat.Component;

/**
 * 26.3 没有可用的 MCEF 构件。浏览器 App 保持可见，但打开前就会明确显示
 * 后端不可用；本类不引用任何外部 MCEF 类型。
 */
public final class McefBackend implements IBrowserBackend {

    @Override
    public boolean isAvailable() {
        return false;
    }

    @Override
    public Component unavailableReason() {
        return Component.translatable("mcphone.browser.no_backend");
    }

    @Override
    public IBrowser create(String url, int width, int height) {
        return null;
    }
}
