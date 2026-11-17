package com.november.mcphone.platform;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

/**
 * 按 id 查物品。
 *
 * <p>26.x 把注册表的取值口从 {@code get(ResourceLocation)} 换成了
 * {@code getValue(Identifier)}。{@code BuiltInRegistries.ITEM} 仍是 defaulted
 * registry，查不到时返回 {@code Items.AIR}。
 */
public final class ItemLookup {

    private ItemLookup() {}

    public static Item get(Identifier id) {
        return BuiltInRegistries.ITEM.getValue(id);
    }
}
