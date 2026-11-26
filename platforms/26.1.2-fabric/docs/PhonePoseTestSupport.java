package com.november.mcphone.test;

import com.mojang.blaze3d.vertex.PoseStack;
import com.november.mcphone.core.client.PhonePoseScope;
import org.joml.Matrix4f;

/** 旧版 PoseStack 的完整矩阵栈恢复断言。 */
public final class PhonePoseTestSupport {
    private PhonePoseTestSupport() {}

    public static int verify() {
        int checks = 0;
        PoseStack pose = new PoseStack();
        pose.translate(5, 7, 0);
        pose.pushPose();
        pose.scale(2, 2, 1);
        Matrix4f expected = new Matrix4f(pose.last().pose());

        try (var scope = new PhonePoseScope(pose)) {
            pose.popPose();
            pose.translate(99, 99, 0);
        }
        require(pose.last().pose().equals(expected), "多 pop 也恢复栈顶矩阵");
        checks++;

        pose.popPose();
        require(com.november.mcphone.platform.client.Draw.poseAtBase(pose)
                && pose.last().pose().m30() == 5, "恢复完整父层和栈深");
        checks++;

        try {
            try (var scope = new PhonePoseScope(pose)) {
                pose.pushPose();
                pose.pushPose();
                pose.translate(99, 99, 0);
                throw new IllegalStateException("render");
            }
        } catch (IllegalStateException expectedFailure) {
            // 异常路径必须由 close() 恢复。
        }
        require(com.november.mcphone.platform.client.Draw.poseAtBase(pose)
                && pose.last().pose().m30() == 5, "异常且少 pop 时恢复");
        checks++;
        return checks;
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
