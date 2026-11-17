package com.november.mcphone.platform;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

/**
 * Attribute modifier grants and revocations for 26.3.
 *
 * <p>This layer only translates the script-facing attribute id to a
 * {@code Holder<Attribute>} and maps the persisted operation number to the
 * enum used by this Minecraft version. Business policy stays outside.
 */
public final class PlayerAbilities {

    private PlayerAbilities() {
    }

    /** Whether this server knows the attribute and the player has an instance. */
    public static boolean available(ServerPlayer player, Identifier attributeId) {
        Holder<Attribute> attribute = attr(attributeId);
        return attribute != null && player.getAttribute(attribute) != null;
    }

    /** Add or update our transient modifier without touching any other modifier. */
    public static boolean grant(ServerPlayer player, Identifier attributeId,
                                Identifier modifierId, double amount, int operation) {
        Holder<Attribute> attribute = attr(attributeId);
        if (attribute == null) return false;
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance == null) return false;
        instance.addOrUpdateTransientModifier(
                new AttributeModifier(modifierId, amount, op(operation)));
        return true;
    }

    /** Remove only our modifier. Returns whether one was actually removed. */
    public static boolean revoke(ServerPlayer player, Identifier attributeId, Identifier modifierId) {
        Holder<Attribute> attribute = attr(attributeId);
        if (attribute == null) return false;
        AttributeInstance instance = player.getAttribute(attribute);
        return instance != null && instance.removeModifier(modifierId);
    }

    private static Holder<Attribute> attr(Identifier id) {
        return BuiltInRegistries.ATTRIBUTE.get(id).orElse(null);
    }

    /** Wire values: 0 ADD_VALUE, 1 ADD_MULTIPLIED_BASE, 2 ADD_MULTIPLIED_TOTAL. */
    private static AttributeModifier.Operation op(int operation) {
        return switch (operation) {
            case 1 -> AttributeModifier.Operation.ADD_MULTIPLIED_BASE;
            case 2 -> AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL;
            default -> AttributeModifier.Operation.ADD_VALUE;
        };
    }
}
