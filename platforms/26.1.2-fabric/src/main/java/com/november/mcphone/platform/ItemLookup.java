package com.november.mcphone.platform;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

/** Registry lookup seam for 26.x's getValue(Identifier) API. */
public final class ItemLookup {
    private ItemLookup() {}

    public static Item get(Identifier id) {
        return BuiltInRegistries.ITEM.getValue(id);
    }
}
