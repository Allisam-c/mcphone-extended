package com.november.mcphone.platform;

import com.november.mcphone.MCphone;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.Optional;

/** Data-pack predicate lookup and evaluation for 26.3. */
public final class Predicates {

    private Predicates() {
    }

    public static boolean exists(ServerPlayer player, Identifier id) {
        return condition(player, id) != null;
    }

    public static Boolean test(ServerPlayer player, Identifier id) {
        LootItemCondition condition = condition(player, id);
        if (condition == null) return null;
        try {
            LootParams params = new LootParams.Builder((ServerLevel) player.level())
                    .withParameter(LootContextParams.ORIGIN, player.position())
                    .withParameter(LootContextParams.THIS_ENTITY, player)
                    .create(LootContextParamSets.GIFT);
            LootContext context = new LootContext.Builder(params)
                    .withOptionalRandomSource(RandomSource.create())
                    .create(Optional.empty());
            return condition.test(context);
        } catch (RuntimeException e) {
            MCphone.LOGGER.warn("[MCphone] predicate evaluation failed (treated as false): {} - {}",
                    id, e.toString());
            return Boolean.FALSE;
        }
    }

    private static LootItemCondition condition(ServerPlayer player, Identifier id) {
        ResourceKey<LootItemCondition> key = ResourceKey.create(Registries.PREDICATE, id);
        return player.level().getServer().reloadableRegistries().lookup()
                .lookup(Registries.PREDICATE)
                .flatMap(registry -> registry.get(key))
                .map(Holder::value)
                .orElse(null);
    }
}
