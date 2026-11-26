package com.november.mcphone.platform;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.List;

/**
 * Loot table lookup and rolling for 26.3.
 *
 * <p>Lookup goes through the server's reloadable registries so data-pack
 * reloads are visible. Rolling stays on the server thread because it uses the
 * level's random source.
 */
public final class LootAccess {

    private LootAccess() {
    }

    /** Whether the current server data knows this loot table. */
    public static boolean exists(ServerLevel level, Identifier id) {
        return table(level, id) != LootTable.EMPTY;
    }

    /**
     * Roll a table with the GIFT parameter set. An unknown table yields an
     * empty list; this method does not hand the result to the player.
     */
    public static List<ItemStack> roll(ServerLevel level, Identifier id, ServerPlayer player) {
        LootTable table = table(level, id);
        if (table == LootTable.EMPTY) return List.of();
        LootParams params = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, player.position())
                .withParameter(LootContextParams.THIS_ENTITY, player)
                .create(LootContextParamSets.GIFT);
        return List.copyOf(table.getRandomItems(params));
    }

    private static LootTable table(ServerLevel level, Identifier id) {
        return level.getServer().reloadableRegistries()
                .getLootTable(ResourceKey.create(Registries.LOOT_TABLE, id));
    }
}
