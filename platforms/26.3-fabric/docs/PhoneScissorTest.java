package com.november.mcphone.test;

import com.november.mcphone.core.client.GuiUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;
import sun.misc.Unsafe;
import java.lang.reflect.Field;

/** Exercise vanilla clipping without constructing the GPU-dependent client. */
public final class PhoneScissorTest {
    public static void main(String[] args) throws Exception {
        Field field = Unsafe.class.getDeclaredField("theUnsafe");
        field.setAccessible(true);
        Unsafe unsafe = (Unsafe) field.get(null);
        Class<?> stackType = Class.forName(GuiGraphicsExtractor.class.getName() + "$ScissorStack");
        var constructor = stackType.getDeclaredConstructor(ScreenRectangle.class);
        constructor.setAccessible(true);
        var peek = stackType.getDeclaredMethod("peek");
        peek.setAccessible(true);
        int checks = 0;
        for (float scale : new float[]{0.4F, 0.6F, 1.0F, 1.1F, 1.2F, 1.25F, 1.5F}) {
            var g = (GuiGraphicsExtractor) unsafe.allocateInstance(GuiGraphicsExtractor.class);
            var pose = new Matrix3x2fStack(16);
            unsafe.putObject(g, unsafe.objectFieldOffset(GuiGraphicsExtractor.class.getDeclaredField("pose")), pose);
            Object clipStack = constructor.newInstance(new ScreenRectangle(0, 0, 2000, 2000));
            unsafe.putObject(g, unsafe.objectFieldOffset(GuiGraphicsExtractor.class.getDeclaredField("scissorStack")), clipStack);
            java.util.function.BiPredicate<Integer, Integer> inside = (x, y) -> {
                try {
                    ScreenRectangle clip = (ScreenRectangle) peek.invoke(clipStack);
                    return clip == null || clip.containsPoint(x, y);
                } catch (ReflectiveOperationException error) {
                    throw new AssertionError(error);
                }
            };
            pose.translate(472.25F, 260.5F).scale(scale).translate(-472.25F, -260.5F);
            Matrix3x2f original = new Matrix3x2f(pose);
            int x0 = (int) Math.floor(412 * scale + original.m20());
            int y0 = (int) Math.floor(160 * scale + original.m21());
            int x1 = (int) Math.ceil(532 * scale + original.m20());
            int y1 = (int) Math.ceil(174 * scale + original.m21());
            GuiUtil.clipped(g, 412, 160, 532, 174, () -> {
                require(inside.test(x0, y0), "top-left clipped at " + scale);
                require(inside.test(x1 - 1, y1 - 1), "bottom-right clipped at " + scale);
                require(!inside.test(x1, y1 - 1), "right edge leaked");
                require(!inside.test(x0, y0 - 1), "top edge leaked");
                require(pose.equals(original), "drawing pose changed");
                int bx = Math.round(522 * scale + original.m20());
                int by = Math.round(167 * scale + original.m21());
                require(inside.test(bx, by), "battery missing at " + scale);
                GuiUtil.clipped(g, 516, 161, 528, 173, () -> {
                    require(inside.test(bx, by), "nested battery clipped");
                    require(!inside.test(x0, y0), "nested clip leaked");
                });
                require(inside.test(x0, y0), "parent clip not restored");
            });
            require(inside.test(0, 0), "clip stack leaked");
            try {
                GuiUtil.clipped(g, 412, 160, 532, 174, () -> { throw new IllegalStateException("probe"); });
                throw new AssertionError("exception swallowed");
            } catch (IllegalStateException expected) {
                require(inside.test(0, 0), "exception leaked clip");
                require(pose.equals(original), "exception changed pose");
            }
            pose.pushMatrix();
            pose.translate(1, 2);
            pose.popMatrix();
            require(pose.equals(original), "pose stack depth changed");
            checks += 13;
        }
        System.out.println("PhoneScissorTest: " + checks + " checks passed (40/60/100/110/120/125/150%)");
    }

    private static void require(boolean value, String message) {
        if (!value) throw new AssertionError(message);
    }
}
