package com.november.mcphone.platform;

import net.minecraft.SharedConstants;

/** Version-name seam for the 26.x record-shaped WorldVersion API. */
public final class GameInfo {
    private GameInfo() {}

    public static String versionName() {
        return SharedConstants.getCurrentVersion().name();
    }
}
