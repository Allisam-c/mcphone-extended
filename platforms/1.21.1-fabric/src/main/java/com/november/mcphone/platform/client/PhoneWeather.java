package com.november.mcphone.platform.client;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.core.BlockPos;
/** 原版降水查询新版需要当前维度的海平面参数。 */
public final class PhoneWeather {
    private PhoneWeather() {}
    /** 当前世界的游戏时间。26.x 改叫 {@code getDefaultClockTime()}。 */
    public static long dayTime(Level level) {
        return level.getDayTime();
    }
    public static Biome.Precipitation precipitation(Level level,BlockPos pos) {
        return level.getBiome(pos).value().getPrecipitationAt(pos);
    }
}
