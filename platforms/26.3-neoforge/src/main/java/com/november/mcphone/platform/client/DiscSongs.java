package com.november.mcphone.platform.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.JukeboxSong;

/** Resolves a disc title through the 26.3 jukebox-song registry. */
public final class DiscSongs {
    private DiscSongs() {}

    public static String title(ItemStack disc) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level != null) {
            var song = JukeboxSong.fromStack(disc);
            if (song.isPresent()) return song.get().value().description().getString();
        }
        return disc.getHoverName().getString();
    }
}
