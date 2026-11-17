package com.november.mcphone;

import com.mojang.logging.LogUtils;
import com.november.mcphone.core.ModAttachments;
import com.november.mcphone.core.ModCreativeTabs;
import com.november.mcphone.core.ModDataComponents;
import com.november.mcphone.core.ModItems;
import com.november.mcphone.core.ModSounds;
import com.november.mcphone.core.menu.ModMenus;
import com.november.mcphone.core.net.NetworkHandler;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

/**
 * NeoForge 26.3 entry point.
 *
 * <p>The registration and lifecycle wiring stays in the platform entry point.
 * Shared and layer sources are mounted into this target, but they must not
 * have to know which loader events are available on each Minecraft
 * generation.
 */
@Mod(MCphone.MODID)
public final class MCphone {

    public static final String MODID = "mcphone";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static String version = "";

    public MCphone(IEventBus modEventBus, ModContainer modContainer) {
        version = modContainer.getModInfo().getVersion().toString();

        ModItems.ITEMS.register(modEventBus);
        ModDataComponents.COMPONENTS.register(modEventBus);
        ModCreativeTabs.TABS.register(modEventBus);
        ModMenus.MENUS.register(modEventBus);
        ModAttachments.ATTACHMENT_TYPES.register(modEventBus);
        ModSounds.SOUND_EVENTS.register(modEventBus);
        modEventBus.addListener(NetworkHandler::register);

        // These caches only shrink when the game bus tells them a player left.
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(
                com.november.mcphone.core.net.RequestThrottle::onPlayerLoggedOut);
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(
                com.november.mcphone.feature.music.DiscService::onPlayerLoggedOut);
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(
                com.november.mcphone.feature.chat.ChatImageUploads::onPlayerLoggedOut);

        // The connection epoch is created on login and forgotten on logout.
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(
                (net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent event) -> {
                    if (event.getEntity() instanceof net.minecraft.server.level.ServerPlayer player) {
                        long epoch = com.november.mcphone.core.script.server.ScriptHost.newEpoch(player);
                        com.november.mcphone.core.script.server.HandshakeService.pushTo(player, epoch);
                    }
                });
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(
                (net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent event) -> {
                    if (event.getEntity() instanceof net.minecraft.server.level.ServerPlayer player) {
                        com.november.mcphone.core.script.server.ScriptHost.forget(player.getUUID());
                    }
                });

        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(
                com.november.mcphone.feature.terminal.TerminalCharger::onPlayerTick);
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(
                com.november.mcphone.feature.chat.ChatImageStore::onServerStarted);

        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(
                (net.neoforged.neoforge.event.server.ServerStartedEvent event) -> {
                    com.november.mcphone.core.script.server.economy.EconomyRuntime.start(event.getServer());
                    com.november.mcphone.core.script.server.ScriptWorkers.start();
                    com.november.mcphone.core.script.server.ScriptHost.start(event.getServer());
                });
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(
                (net.neoforged.neoforge.event.server.ServerStoppingEvent event) -> {
                    // Close the currency gateway before stopping workers, then
                    // detach the script pipeline before stopping the executor.
                    com.november.mcphone.core.script.server.economy.EconomyRuntime.stop();
                    com.november.mcphone.core.script.server.ScriptHost.stop();
                    com.november.mcphone.core.script.server.ScriptWorkers.stop();
                });
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(
                (net.neoforged.neoforge.event.tick.ServerTickEvent.Post event) ->
                        com.november.mcphone.core.script.server.economy.EconomyRuntime.tick());
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(
                (net.neoforged.neoforge.event.tick.ServerTickEvent.Post event) ->
                        tickDiscLoop(event.getServer().getPlayerList().getPlayers()));
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(
                (net.neoforged.neoforge.event.RegisterCommandsEvent event) ->
                        com.november.mcphone.core.script.server.economy.EconomyCommand.register(event.getDispatcher()));
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(
                (net.neoforged.neoforge.event.RegisterCommandsEvent event) ->
                        com.november.mcphone.core.script.server.ScriptAdminCommand.register(event.getDispatcher()));

        modContainer.registerConfig(ModConfig.Type.SYNCED,
                com.november.mcphone.core.ServerConfig.SPEC, "mcphone-server.toml");

        modEventBus.addListener(
                com.november.mcphone.feature.terminal.integration.Terminals::onCommonSetup);

        // Compatibility modules may inspect our registrations, so this stays
        // after the registrations above.
        com.november.mcphone.compat.CompatModules.init(modEventBus);

        LOGGER.info("[MCphone] NeoForge 26.3 loaded v{}", version);
    }

    private static void tickDiscLoop(Iterable<net.minecraft.server.level.ServerPlayer> players) {
        for (net.minecraft.server.level.ServerPlayer player : players) {
            try {
                if (com.november.mcphone.feature.music.DiscService.tickLoop(player)) {
                    com.november.mcphone.feature.music.DiscService.syncState(player);
                }
            } catch (VirtualMachineError fatal) {
                throw fatal;
            } catch (Throwable failure) {
                LOGGER.error("[MCphone] disc loop failed player={}", player.getUUID(), failure);
            }
        }
    }

    public static String getVersion() {
        return version;
    }
}
