package com.november.testbootstrap;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;

import java.lang.reflect.InvocationTargetException;

/**
 * 26.3 Fabric command assertions need the vanilla registries bootstrapped.
 * Fabric Loom's development classpath already supplies the game classes; this
 * wrapper only establishes the same deterministic headless state as a server.
 */
public final class LoaderAssertionMain {
    private LoaderAssertionMain() {}

    public static void main(String[] args) throws Throwable {
        if (args.length != 1) {
            throw new IllegalArgumentException("必须指定一份完整断言的入口类");
        }
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        try {
            Class.forName(args[0], true, Thread.currentThread().getContextClassLoader())
                    .getMethod("main", String[].class)
                    .invoke(null, (Object) new String[0]);
        } catch (InvocationTargetException failedAssertion) {
            throw failedAssertion.getCause();
        }
    }
}
