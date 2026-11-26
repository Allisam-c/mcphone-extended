package com.november.mcphone.core.net.client;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

/** 26.3 moved the client-to-server send entry into the client-only network package. */
public final class ClientPacketSender {
    private ClientPacketSender() {}

    public static void send(CustomPacketPayload packet) {
        ClientPacketDistributor.sendToServer(packet);
    }
}
