package com.november.mcphone.core.client;

import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * Saves and restores the complete 26.3 GUI matrix stack.
 *
 * <p>{@link Matrix3x2fStack} exposes push/pop but no depth or snapshot API.
 * Its Java serialization form also loses stack depth (verified against the
 * pinned JOML 1.10.9), so this class snapshots the two private fields that
 * implement the stack. A layout mismatch fails loudly instead of silently
 * leaving the renderer in a corrupted state.
 */
public final class PhonePoseScope implements AutoCloseable {

    private static final Field MATS = field("mats");
    private static final Field CURR = field("curr");

    private final Matrix3x2fStack stack;
    private final Matrix3x2f current;
    private final Matrix3x2f[] saved;
    private final int depth;

    public PhonePoseScope(Matrix3x2fStack stack) {
        this.stack = Objects.requireNonNull(stack, "stack");
        this.current = new Matrix3x2f(stack);
        Matrix3x2f[] live = mats(stack);
        this.saved = copy(live);
        try {
            this.depth = CURR.getInt(stack);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Cannot read Matrix3x2fStack depth", e);
        }
        if (depth < 0 || depth > saved.length) {
            throw new IllegalStateException(
                    "Invalid Matrix3x2fStack depth " + depth + " for capacity " + saved.length);
        }
    }

    private void restore() {
        try {
            MATS.set(stack, copy(saved));
            CURR.setInt(stack, depth);
            stack.set(current);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Cannot restore Matrix3x2fStack", e);
        }
    }

    @Override
    public void close() {
        restore();
    }

    private static Matrix3x2f[] mats(Matrix3x2fStack stack) {
        try {
            Object value = MATS.get(stack);
            if (!(value instanceof Matrix3x2f[] entries) || entries.length == 0) {
                throw new IllegalStateException("Matrix3x2fStack has no matrix storage");
            }
            return entries;
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Cannot read Matrix3x2fStack storage", e);
        }
    }

    private static Matrix3x2f[] copy(Matrix3x2f[] source) {
        Matrix3x2f[] copy = new Matrix3x2f[source.length];
        for (int i = 0; i < source.length; i++) {
            copy[i] = new Matrix3x2f(source[i]);
        }
        return copy;
    }

    private static Field field(String name) {
        try {
            Field field = Matrix3x2fStack.class.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(
                    "JOML Matrix3x2fStack layout changed: missing field " + name);
        }
    }
}
