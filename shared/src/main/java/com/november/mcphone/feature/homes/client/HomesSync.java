package com.november.mcphone.feature.homes.client;

import net.minecraft.client.Minecraft;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 家园列表的服务器同步器 —— 这个功能的数据权威在服务器（NeoEssentials），本地不记账。
 *
 * <h2>怎么拿到真实列表</h2>
 *
 * 打开页面时发 {@code /listhomes}，NeoEssentials 会把 "Your homes (3/100):"
 * 加每行 "家名: 维度 (坐标)" 刷进聊天栏。{@link #onChat(String)} 在捕获窗口
 * （3 秒）内解析这些行：括号里的 N/M 是已用数与上限，每行的名字进清单。
 * 窗口一过就停止解析，聊天栏恢复正常用途。
 *
 * <h2>删除的两段式确认</h2>
 *
 * NeoEssentials 的 {@code /delhome <名字>} 是两段式：服务器回
 * "Are you sure... [Confirm] [Deny]"，要用 {@code /confirm} 命令（聊天栏里
 * 点 [Confirm] 等价）才算真删。本类 {@link #requestDelete} 会自动补发
 * {@code /confirm}，并排一次重同步 —— 就算自动确认失败（换插件、改权限），
 * 重同步也会把服务器的真实状态拉回来，清单永远不会说谎。
 *
 * <h2>聊天钩子在哪接</h2>
 *
 * {@code onChat} 由平台侧的 ClientChatReceivedEvent 监听转发（见 26.3 平台的
 * MCphoneClient）——本类只做解析，不碰事件总线。
 */
public final class HomesSync {

    private static final long CAPTURE_WINDOW_MS = 3000L;
    private static final Pattern HOMES_COUNT =
            Pattern.compile("\\((\\d+)\\s*/\\s*(\\d+)\\)");
    private static final Pattern HOME_LINE =
            Pattern.compile("^\\s*([A-Za-z0-9_\\u4e00-\\u9fa5\\-]+)\\s*:.*\\(.*\\)\\s*$");

    private static final List<String> homes = new ArrayList<>();
    private static int limit = -1;
    private static boolean syncing;
    private static long captureUntilMs;

    /** tick 驱动的延迟动作（自动补 /confirm、延迟重同步），页面开着才执行 */
    private static final ArrayDeque<long[]> timers = new ArrayDeque<>();   // [dueMs]
    private static final ArrayDeque<Runnable> actions = new ArrayDeque<>();

    /** 家名长度上限（服务器校验更严格，这里先挡明显不合法的） */
    public static final int MAX_NAME_LENGTH = 24;

    private HomesSync() {}

    /** 名字合法性：非空、无空白（命令按空格分词，带空格的 /home 需要引号） */
    public static boolean isValidName(String name) {
        if (name == null || name.isBlank()) return false;
        if (name.length() > MAX_NAME_LENGTH) return false;
        return name.chars().noneMatch(Character::isWhitespace);
    }

    public static List<String> homes() { return List.copyOf(homes); }

    /** 服务器侧的家园上限；还没同步到时返回 -1（UI 不显示上限） */
    public static int limit() { return limit; }

    public static boolean isSyncing() { return syncing; }

    /** 打开页面 / 需要刷新时调用：清空缓存、开捕获窗口、发 /listhomes */
    public static void startSync() {
        homes.clear();
        limit = -1;
        syncing = true;
        captureUntilMs = System.currentTimeMillis() + CAPTURE_WINDOW_MS;
        send("listhomes");
    }

    /**
     * 删除家：/delhome 触发服务器的两段式确认，这里自动补 /confirm，
     * 再排一次重同步。自动确认若没生效，用户点聊天栏的 [Confirm] 后，
     * 重同步照样把真实状态拉回来。
     */
    public static void requestDelete(String name) {
        send("delhome " + name);
        homes.remove(name); // 乐观移除；玩家没点 [Confirm] 的话，15 秒后的重同步会把它带回来
        schedule(15000, HomesSync::startSync);
    }

    /**
     * 新建家：乐观更新 —— 名字立刻进清单（零延迟），服务器若拒绝
     * （重名/冷却），聊天栏会刷拒绝原因，下次同步自动纠正。
     */
    public static void requestSet(String name) {
        send("sethome " + name);
        if (!homes.contains(name)) homes.add(name);
    }

    /**
     * 平台侧聊天事件转发进来（只在捕获窗口内解析）。
     *
     * @return true = 这条消息是 /listhomes 的回包，调用方应把聊天事件取消掉，
     *         不让它刷进聊天栏 —— 同步在后台拿数据，玩家眼前保持干净
     */
    public static boolean onChat(String text) {
        if (!syncing || text == null) return false;
        boolean hide = false;
        for (String line : text.split("\n")) {
            String t = line.replace("[NE]", "").trim();
            if (t.isEmpty()) continue;
            Matcher m = HOMES_COUNT.matcher(t);
            if (m.find()) {
                limit = Integer.parseInt(m.group(2));
                hide = true;
                continue;
            }
            Matcher r = HOME_LINE.matcher(t);
            if (r.matches()) {
                if (!homes.contains(r.group(1))) homes.add(r.group(1));
                hide = true;
            }
        }
        return hide;
    }

    /** 捕获窗口到期就收工；到期的延迟动作执行。页面每 tick 调一次 */
    public static void tick() {
        if (syncing && System.currentTimeMillis() > captureUntilMs) syncing = false;
        while (!timers.isEmpty()) {
            long[] due = timers.peek();
            if (due == null || System.currentTimeMillis() < due[0]) break;
            timers.poll();
            Runnable r = actions.poll();
            if (r != null) r.run();
        }
    }

    private static void schedule(long delayMs, Runnable action) {
        timers.add(new long[]{System.currentTimeMillis() + delayMs});
        actions.add(action);
    }

    private static void send(String cmd) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) mc.player.connection.sendCommand(cmd);
    }
}
