package com.november.mcphone.core.client;

import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;

/** Registers the shared phone keys with 26.3's Identifier-based key category. */
public final class MCphoneKeyBindings {

    private MCphoneKeyBindings() {}

    public static void register(RegisterKeyMappingsEvent event) {
        KeyMapping.Category category = KeyMapping.Category.register(
                Identifier.fromNamespaceAndPath("mcphone", "keys"));

        for (PhoneKeys.Key key : PhoneKeys.ALL) {
            KeyMapping mapping = new KeyMapping(key.id(), key.defaultCode(), category);
            mapping.setKeyConflictContext(KeyConflictContext.IN_GAME);
            key.bind(mapping);
            event.register(mapping);
        }
    }
}
