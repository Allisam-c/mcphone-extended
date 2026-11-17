package com.november.mcphone.core;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.LevelResource;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 存档级数据的基类，把 {@link SavedData} 跨版本变了形状的两处关在这里。
 *
 * 26.3 的存档入口改成 {@link SavedDataType} 加 Codec；同一个文件名必须复用
 * 同一个类型实例，否则连续读取会拿到不同对象并丢掉尚未写盘的修改。
 *
 * 子类只写 {@link #write} 与一个 {@code load(CompoundTag)}，两支同形，可以进共用层。
 * 别把 {@code HolderLookup.Provider} 漏进子类的签名 —— 那一支没有这个类型。
 */
public abstract class PhoneSavedData extends SavedData {

    private static final ConcurrentHashMap<String, SavedDataType<?>> TYPES = new ConcurrentHashMap<>();

    /** 把自己写进 tag。返回值就是传进来的那个 tag。 */
    protected abstract CompoundTag write(CompoundTag tag);

    /** 取或建。{@code load} 收的是裸 tag，注册表那个参数在这里被吃掉。 */
    protected static <T extends PhoneSavedData> T getOrCreate(
            MinecraftServer server, String name, Supplier<T> create, Function<CompoundTag, T> load) {
        Path file;
        try {
            file = prepare(server.getWorldPath(LevelResource.ROOT).resolve("data"), name);
        } catch (IOException failure) {
            throw new IllegalStateException("MCphone 存档迁移失败，保留原件并停止读取：" + name, failure);
        }
        var storage = server.overworld().getDataStorage();
        var dataType = type(name, create, load);
        T existing = storage.get(dataType);
        if (existing != null) return existing;
        if (Files.exists(file)) {
            throw new IllegalStateException("MCphone 存档无法解码，拒绝重建空记录：" + file);
        }
        return storage.computeIfAbsent(dataType);
    }

    @SuppressWarnings("unchecked")
    static <T extends PhoneSavedData> SavedDataType<T> type(
            String name, Supplier<T> create, Function<CompoundTag, T> load) {
        return (SavedDataType<T>) TYPES.computeIfAbsent(name, key -> {
            Codec<T> codec = CompoundTag.CODEC.xmap(load, data -> data.write(new CompoundTag()));
            return new SavedDataType<>(Identifier.fromNamespaceAndPath("mcphone", key), create, codec);
        });
    }

    private static Path current(Path dataDirectory, String name) {
        if (!name.matches("[a-z0-9_]+")) throw new IllegalArgumentException("存档名称不合法：" + name);
        return dataDirectory.resolve("mcphone").resolve(name + ".dat");
    }

    /**
     * 26.3 把存档放到了 data/mcphone/ 下。旧版本留下的裸文件或 data/minecraft/
     * 文件只复制一次，原件保留；两份旧文件内容冲突时拒绝猜测。
     */
    private static synchronized Path prepare(Path dataDirectory, String name) throws IOException {
        Path destination = current(dataDirectory, name);
        if (Files.exists(destination)) return destination;

        List<Path> candidates = List.of(
                dataDirectory.resolve(name + ".dat"),
                dataDirectory.resolve("minecraft").resolve(name + ".dat"));
        Path source = null;
        for (Path candidate : candidates) {
            if (!Files.exists(candidate)) continue;
            if (source != null && Files.mismatch(source, candidate) != -1) {
                throw new IOException("两份遗留 MCphone 存档内容不同，拒绝猜测：" + source + "、" + candidate);
            }
            source = candidate;
        }
        if (source == null) return destination;

        Files.createDirectories(destination.getParent());
        Path temporary = Files.createTempFile(destination.getParent(), name + "-migration-", ".tmp");
        try {
            Files.copy(source, temporary, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            if (Files.mismatch(source, temporary) != -1) {
                throw new IOException("迁移副本与原件不一致：" + source);
            }
            try (FileChannel channel = FileChannel.open(temporary, StandardOpenOption.WRITE)) {
                channel.force(true);
            }
            Files.move(temporary, destination);
        } finally {
            Files.deleteIfExists(temporary);
        }
        return destination;
    }
}
