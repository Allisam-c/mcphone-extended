package com.november.mcphone.platform.client;

import com.november.mcphone.platform.client.port.PhoneGraphics;
import org.joml.Vector3f;

/** 26.1 的 GUI 适配器仍使用 PoseStack，提供与 1.21.1 相同的变换入口。 */
public final class Transforms {

    private Transforms() {}

    public static void push(PhoneGraphics g) {
        g.pose().pushPose();
    }

    public static void pop(PhoneGraphics g) {
        g.pose().popPose();
    }

    public static void translate(PhoneGraphics g, double x, double y) {
        g.pose().translate(x, y, 0.0D);
    }

    public static void scale(PhoneGraphics g, double sx, double sy) {
        g.pose().scale((float) sx, (float) sy, 1.0F);
    }

    public static void translateAboveItemModel(PhoneGraphics g, double x, double y) {
        g.pose().translate(x, y, 200.0D);
    }

    public static float mapX(PhoneGraphics g, float x, float y) {
        return g.pose().last().pose().transformPosition(x, y, 0.0F, new Vector3f()).x;
    }

    public static float mapY(PhoneGraphics g, float x, float y) {
        return g.pose().last().pose().transformPosition(x, y, 0.0F, new Vector3f()).y;
    }

    /** PhoneGraphics 已负责避免原版对窗口坐标再次应用矩阵。 */
    public static void scissor(PhoneGraphics g, int x0, int y0, int x1, int y1) {
        g.enableScissor(x0, y0, x1, y1);
    }
}
