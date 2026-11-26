package com.november.mcphone.feature.homes.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.november.mcphone.core.client.ClientPaths;
import com.november.mcphone.core.PhonePaths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * 家园列表的客户端持久化。
 *
 * 家的真实数据在服务器侧（NeoEssentials 的存档里），手机这边只记一份
 * 【通过本 App 管理过的名字清单】用于展示：加一个名字就发 /sethome，
 * 删一个就发 /delhome，点一个就发 /home。玩家在聊天栏手动设的家不在这份
 * 清单里 —— 这是 v1 的已知取舍，避免为读服务器数据写一套包协议。
 *
 * 服务器才是权威：sethome 有冷却、有上限、有名字校验，聊天栏的反馈就是
 * 结果。本地清单只是「我发过哪些命令」的账本。
 */
public final class HomesStore {

    /** NeoEssentials 默认的 homeSettings.maxHomes。服务器配置才是权威，这里只是 UI 侧的软上限 */
    public static final int MAX_HOMES = 5;

    /** 家名上限。NeoEssentials 侧的校验更严格，这里先挡掉明显不合法的 */
    public static final int MAX_NAME_LENGTH = 24;

    private static final Gson GSON = new Gson();

    private static final class Data {
        List<String> homes = new ArrayList<>();
    }

    private static Path file() {
        return ClientPaths.resolve(PhonePaths.HOMES);
    }

    /** 读取家名清单（保持设置顺序）。文件不存在或损坏返回空清单 */
    public static List<String> load() {
        Path f = file();
        if (!Files.isRegularFile(f)) return List.of();
        try {
            Data d = GSON.fromJson(Files.readString(f), TypeToken.get(Data.class).getType());
            if (d == null || d.homes == null) return List.of();
            return List.copyOf(d.homes);
        } catch (IOException | RuntimeException e) {
            return List.of();
        }
    }

    /** 覆盖保存整份清单 */
    public static void save(List<String> homes) {
        Path f = file();
        try {
            Files.createDirectories(f.getParent());
            Data d = new Data();
            d.homes = new ArrayList<>(homes);
            Files.writeString(f, GSON.toJson(d));
        } catch (IOException e) {
            // 存不了就只活在本局内存里，下次打开会重新拉空清单 —— 不值得为它崩
        }
    }

    /** 名字合法性：非空、无空白（命令按空格分词，带空格的 /home 需要引号，别让它进门） */
    public static boolean isValidName(String name) {
        if (name == null || name.isBlank()) return false;
        if (name.length() > MAX_NAME_LENGTH) return false;
        return name.chars().noneMatch(Character::isWhitespace);
    }
}
