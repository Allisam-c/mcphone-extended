package com.november.mcphone.platform;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Prediction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Player name, messaging, permissions and teleportation seams for 26.3.
 *
 * <p>This target keeps the upstream {@code PlayerAccess} contract so shared code
 * does not need a second set of profile/message wrappers. The local player
 * implementations of {@code sendSystemMessage} and {@code sendOverlayMessage}
 * are the client-side hooks on this version; the server player sends the same
 * packets as the older targets.
 */
public final class PlayerAccess {
    private PlayerAccess() {}

    public static String name(Player player) {
        return player.getGameProfile().name();
    }

    /**
     * Reads only the in-memory username cache. Do not switch this to
     * {@code profileResolver().fetchById}: that path may perform network I/O.
     */
    public static Optional<String> cachedName(MinecraftServer server, UUID id) {
        var cache = server.services().nameToIdCache();
        return cache == null ? Optional.empty() : cache.get(id).map(profile -> profile.name());
    }

    public static void message(Player player, Component text, boolean overlay) {
        if (overlay) player.sendOverlayMessage(text);
        else player.sendSystemMessage(text);
    }

    public static boolean admin(CommandSourceStack source) {
        return source.permissions().hasPermission(net.minecraft.server.permissions.Permissions.COMMANDS_ADMIN);
    }

    public static Component itemName(ItemLike item) {
        return new net.minecraft.world.item.ItemStack(item).getHoverName();
    }

    public static void teleport(ServerPlayer player, ServerLevel level,
                                double x, double y, double z, float yaw, float pitch) {
        player.teleportTo(level, x, y, z, Set.of(), yaw, pitch, true);
    }

    public static String dimensionId(ServerPlayer player) {
        return player.level().dimension().identifier().toString();
    }

    public static void drop(Player player, ItemStack stack, boolean dropAround) {
        player.drop(stack, dropAround, Prediction.SERVER_ONLY);
    }
}
