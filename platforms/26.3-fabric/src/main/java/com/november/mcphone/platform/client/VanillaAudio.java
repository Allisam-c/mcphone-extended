package com.november.mcphone.platform.client;

import net.minecraft.client.sounds.AudioStream;
import net.minecraft.client.sounds.JOrbisAudioStream;

import java.io.IOException;
import java.io.InputStream;

/** Opens a vanilla Ogg stream on the 26.3 sound API. */
public final class VanillaAudio {
    private VanillaAudio() {}

    public static AudioStream openOgg(InputStream input) throws IOException {
        return new JOrbisAudioStream(input);
    }
}
