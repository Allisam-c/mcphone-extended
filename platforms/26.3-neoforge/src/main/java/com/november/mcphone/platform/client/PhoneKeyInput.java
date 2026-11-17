package com.november.mcphone.platform.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.input.KeyEvent;

/** Keeps the shared key-code calls on the 26.3 event shape. */
public final class PhoneKeyInput {
    private PhoneKeyInput() {}

    public static InputConstants.Key key(int physicalKey, int virtualKey) {
        return InputConstants.getKey(new KeyEvent(physicalKey, virtualKey, 0));
    }

    public static boolean matches(KeyMapping mapping, int physicalKey, int virtualKey) {
        return mapping.matches(new KeyEvent(physicalKey, virtualKey, 0));
    }
}
