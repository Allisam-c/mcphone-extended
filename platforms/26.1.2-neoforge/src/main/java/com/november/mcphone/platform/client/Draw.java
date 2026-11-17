package com.november.mcphone.platform.client;
import com.november.mcphone.platform.client.port.PhoneGraphics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
/** 26.1 使用原版 GUI 提取管线，不再调用已移除的即时 BufferUploader。 */
public final class Draw {
    private Draw() {}

    /** 26.1 将 PoseStack 的栈底判定从 clear 改名为 isEmpty。 */
    public static boolean poseAtBase(com.mojang.blaze3d.vertex.PoseStack stack) {
        return stack.isEmpty();
    }
    /** 背景已由 Screen.extractBackground 在内容之前提取，不能在内容阶段再次模糊。 */
    public static void screenBackground(Screen s,PhoneGraphics g,int x,int y,float a) {}
    public static boolean scissorLeaked(PhoneGraphics g) { return !g.containsPointInScissor(0,0); }
    public static void browserFrame(PhoneGraphics g,int texture,int x,int y,int w,int h) {
        com.november.mcphone.platform.client.port.PhoneBrowserTexture.draw(g,texture,x,y,w,h);
    }
    /** 26.1 的贴图仍走适配器的颜色状态；保持与旧目标相同的调用形状。 */
    public static void blitTexture(PhoneGraphics g, Identifier tex,
                                   int x, int y, int w, int h,
                                   float u, float v, int srcW, int srcH,
                                   int texW, int texH, int color) {
        boolean tinted = color != -1;
        if (tinted) {
            g.setColor(((color >>> 16) & 255) / 255f, ((color >>> 8) & 255) / 255f,
                    (color & 255) / 255f, ((color >>> 24) & 255) / 255f);
        }
        try {
            g.blit(tex, x, y, w, h, u, v, srcW, srcH, texW, texH);
        } finally {
            if (tinted) g.setColor(1f, 1f, 1f, 1f);
        }
    }
    public static void renderItem(PhoneGraphics g, ItemStack stack, int x, int y) {
        g.renderItem(stack, x, y);
    }
    public static boolean canDrawItemIcon(ItemStack stack) {
        var mc=Minecraft.getInstance();
        var state=new ItemStackRenderState();
        mc.getItemModelResolver().updateForTopItem(state,stack,ItemDisplayContext.GUI,mc.level,mc.player,0);
        // 新版特殊物品也由原版 GUI 提取器处理；先解析成功才提交，保留缺失模型的回退。
        return !state.isEmpty();
    }
}
