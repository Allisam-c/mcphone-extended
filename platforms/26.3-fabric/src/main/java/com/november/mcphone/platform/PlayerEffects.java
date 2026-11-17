package com.november.mcphone.platform;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

/**
 * Mob effect lookup and timed grants for 26.3.
 *
 * <p>The instance carries its own duration, so authorization cannot outlive
 * the effect. Only the server intent applier calls this seam.
 */
public final class PlayerEffects {

    private PlayerEffects() {
    }

    /** Whether the current server registry contains this effect. */
    public static boolean available(Identifier effectId) {
        return holder(effectId) != null;
    }

    /** Give one timed effect, or return false when the id is unknown. */
    public static boolean give(ServerPlayer player, Identifier effectId, int durationTicks, int amplifier) {
        Holder<MobEffect> holder = holder(effectId);
        if (holder == null) return false;
        return player.addEffect(new MobEffectInstance(holder, durationTicks, amplifier));
    }

    private static Holder<MobEffect> holder(Identifier id) {
        return BuiltInRegistries.MOB_EFFECT.get(id).orElse(null);
    }
}
