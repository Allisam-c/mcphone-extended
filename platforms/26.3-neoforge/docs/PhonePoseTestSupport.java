package com.november.mcphone.test;

import com.november.mcphone.core.client.PhonePoseScope;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;

import java.lang.reflect.Field;

/** 26.3 的二维 GUI 矩阵栈没有公开深度接口，用实现字段验证完整快照恢复。 */
public final class PhonePoseTestSupport {
    private static final Field CURRENT = depthField();

    private PhonePoseTestSupport() {}

    public static int verify() {
        int checks = 0;
        Matrix3x2fStack pose = new Matrix3x2fStack(16);
        pose.translate(5.0F, 7.0F);
        pose.pushMatrix();
        pose.scale(2.0F, 2.0F);
        Matrix3x2f expected = new Matrix3x2f(pose);

        try (var scope = new PhonePoseScope(pose)) {
            pose.popMatrix();
            pose.translate(99.0F, 99.0F);
        }
        require(pose.equals(expected), "多 pop 也恢复栈顶矩阵");
        checks++;

        pose.popMatrix();
        require(depth(pose) == 0 && pose.m20() == 5.0F && pose.m21() == 7.0F,
                "恢复完整父层和栈深");
        checks++;

        try {
            try (var scope = new PhonePoseScope(pose)) {
                pose.pushMatrix();
                pose.pushMatrix();
                pose.translate(99.0F, 99.0F);
                throw new IllegalStateException("render");
            }
        } catch (IllegalStateException expectedFailure) {
            // 异常路径必须由 close() 恢复。
        }
        require(depth(pose) == 0 && pose.m20() == 5.0F && pose.m21() == 7.0F,
                "异常且少 pop 时恢复");
        checks++;
        return checks;
    }

    private static int depth(Matrix3x2fStack stack) {
        try {
            return CURRENT.getInt(stack);
        } catch (IllegalAccessException error) {
            throw new AssertionError("无法读取 Matrix3x2fStack 深度", error);
        }
    }

    private static Field depthField() {
        try {
            Field field = Matrix3x2fStack.class.getDeclaredField("curr");
            field.setAccessible(true);
            return field;
        } catch (ReflectiveOperationException error) {
            throw new ExceptionInInitializerError(error);
        }
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
