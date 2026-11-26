package com.november.mcphone.platform.client;

import com.november.mcphone.core.client.RememberedPlayerSkins;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.PlayerFaceExtractor;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.Identifier;

import java.util.UUID;

/**
 * 玩家头像用的皮肤贴图。
 *
 * <h2>这一支与 1.21.1 那份差在哪儿</h2>
 *
 * 门面【要收的东西一样】（拿一个 UUID 换一张头像贴图），差的是取法：
 * 26.x 把皮肤记录整个挪了家并换了形状 —— {@code PlayerSkin} 现在是
 * {@code net.minecraft.world.entity.player.PlayerSkin} 上的一个记录，
 * 第一格叫 {@code body}（一个 {@code ClientAsset.Texture}），贴图路径在它的
 * {@code texturePath()} 上。1.21.1 那边直接叫 {@code texture()}。
 *
 * <p>所以这里是两段：{@code getSkin().body().texturePath()} 与
 * {@code DefaultPlayerSkin.get(player).body().texturePath()}。
 *
 * <h2>为什么返回面还收在贴图路径上</h2>
 *
 * 与 1.21.1 同一个理由：消费端两支同形（头像那个重载两支收的都是贴图的
 * {@code Identifier}，1.21.1 上叫 {@code ResourceLocation}，同一个东西），
 * 而 {@code PlayerSkin} 记录里的披风、鞘翅、模型类型这里一概用不着。
 * 真要用到那几样时这个门面得重新设计，别硬往里塞。
 */
public final class PlayerSkins {

    private PlayerSkins() {}

    private static final RememberedPlayerSkins<Identifier> MEMORY =
            new RememberedPlayerSkins<>(512);
    private static int ticksUntilRefresh;

    private static Identifier textureOf(PlayerInfo info) {
        return info.getSkin().body().texturePath();
    }

    private static void bind(Minecraft mc) {
        if (MEMORY.bindConnection(mc.getConnection())) ticksUntilRefresh = 0;
    }

    /** Remember online profiles once per second so a friend's skin survives logout. */
    public static void tick() {
        Minecraft mc = Minecraft.getInstance();
        bind(mc);
        if (mc.getConnection() == null || ticksUntilRefresh-- > 0) return;
        ticksUntilRefresh = 19;
        for (PlayerInfo info : mc.getConnection().getOnlinePlayers()) {
            MEMORY.remember(info.getProfile().id(), () -> textureOf(info));
        }
    }

    /** 在线的取真皮肤，离线的退回默认皮肤。 */
    public static Identifier faceTexture(UUID player) {
        Minecraft mc = Minecraft.getInstance();
        bind(mc);
        if (mc.getConnection() != null) {
            PlayerInfo info = mc.getConnection().getPlayerInfo(player);
            if (info != null) MEMORY.remember(player, () -> textureOf(info));
        }
        return MEMORY.resolve(player, DefaultPlayerSkin.get(player).body().texturePath());
    }

    /** 26.3 的头像入口叫 extractRenderState；帽子层保持原版默认行为。 */
    public static void drawFace(GuiGraphicsExtractor g, UUID player, int x, int y, int size) {
        PlayerFaceExtractor.extractRenderState(g, faceTexture(player), x, y, size, true, false, -1);
    }
}
