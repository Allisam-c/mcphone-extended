package com.november.mcphone.platform;

import net.minecraft.SharedConstants;

/**
 * 报当前游戏版本名。
 *
 * <p>26.x 的 {@code WorldVersion} 是 record 化的接口，取名从 {@code getName()}
 * 变成 {@code name()}；1.20.1 / 1.21.1 仍用旧名。AboutPage 只想要一个字符串，
 * 差别留在这份每平台文件里。
 */
public final class GameInfo {

    private GameInfo() {}

    public static String versionName() {
        return SharedConstants.getCurrentVersion().name();
    }
}
