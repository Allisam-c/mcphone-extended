package com.november.mcphone.platform.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.chat.ChatListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

/** 26.x removed Player#displayClientMessage; route server and local players separately. */
public final class ClientMessages {

    private ClientMessages() {}

    public static void show(Player p, Component msg, boolean actionBar) {
        if (p instanceof ServerPlayer sp) {
            sp.sendSystemMessage(msg, actionBar);
        } else if (p instanceof LocalPlayer) {
            ChatListener listener = Minecraft.getInstance().getChatListener();
            if (actionBar) listener.handleOverlay(msg);
            else listener.handleSystemMessage(msg, true);
        }
    }
}
