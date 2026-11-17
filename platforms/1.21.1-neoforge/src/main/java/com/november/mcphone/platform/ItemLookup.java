package com.november.mcphone.platform;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

/**
 * 按 id 查物品。
 *
 * <p>26.x 把注册表的取值口从 {@code get(ResourceLocation)} 换成了
 * {@code getValue(Identifier)}。共用代码只认 {@code Item}，查不到时仍拿到
 * {@code Items.AIR}（{@code BuiltInRegistries.ITEM} 是 defaulted registry）。
 */
public final class ItemLookup {

    private ItemLookup() {}

    public static Item get(ResourceLocation id) {
        return BuiltInRegistries.ITEM.get(id);
    }
}
