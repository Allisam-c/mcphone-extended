package com.november.mcphone.compat;

import com.november.mcphone.feature.music.NetSong;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

/**
 * NetMusic capability facade for targets without a usable NetMusic build.
 *
 * <p>The direct API implementation is not mounted for these targets. This
 * facade keeps the music call sites stable and explicitly reports the
 * integration as unavailable.</p>
 */
public final class NetMusicCompat {

    public static final String NETMUSIC_MODID = "netmusic";

    private NetMusicCompat() {}

    public static boolean isLoaded() {
        return false;
    }

    public static Optional<NetSong> songOf(ItemStack stack) {
        return Optional.empty();
    }
}
