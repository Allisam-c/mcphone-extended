package com.november.mcphone.platform;

import net.minecraft.SharedConstants;

/**
 * 报当前游戏版本名。
 *
 * <p>26.x 把 {@code WorldVersion#getName()} 改成了 {@code name()}，而这一句在
 * 1.20.1 / 1.21.1 上仍是 {@code getName()}。AboutPage 只想知道「这是哪个版本」，
 * 不该为此在共用代码里分叉，所以按版本各留一份。
 */
public final class GameInfo {

    private GameInfo() {}

    public static String versionName() {
        return SharedConstants.getCurrentVersion().getName();
    }
}
