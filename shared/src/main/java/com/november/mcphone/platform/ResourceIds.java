package com.november.mcphone.platform;

import net.minecraft.resources.ResourceLocation;

public final class ResourceIds {
    private ResourceIds() {}

    public static ResourceLocation create(String namespace, String path) {
        ResourceLocation id = ResourceLocation.tryBuild(namespace, path);
        if (id == null) throw new IllegalArgumentException("Invalid resource location: " + namespace + ":" + path);
        return id;
    }
}
