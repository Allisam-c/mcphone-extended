package com.november.mcphone.platform.client;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

/** The 26.3 precipitation query needs the level sea level. */
public final class PhoneWeather {
    private PhoneWeather() {}

    /** 当前世界的游戏时间。26.x 上这个访问器叫 {@code getDefaultClockTime()}。 */
    public static long dayTime(Level level) {
        return level.getDefaultClockTime();
    }

    public static Biome.Precipitation precipitation(Level level, BlockPos pos) {
        return level.getBiome(pos).value().getPrecipitationAt(pos, level.getSeaLevel());
    }
}
