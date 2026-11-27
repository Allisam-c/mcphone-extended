package com.november.mcphone.feature.homes.client;

import com.november.mcphone.api.client.ui.IPhonePage;
import com.november.mcphone.core.client.FontPalette;
import com.november.mcphone.core.client.GuiUtil;
import com.november.mcphone.core.client.PhoneTheme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;

import java.util.List;

/**
 * 家园页：家名清单 + 右上角「+」新增 + 每行「删除」。
 *
 * 数据来自 {@link HomesStore}（客户端账本），动作全部走聊天命令：
 *   点家名   -> /home <名字>
 *   点删除   -> /delhome <名字>
 *   确认新增 -> /sethome <名字>
 *
 * 服务器（NeoEssentials）才是权威：冷却、上限、名字校验都以它的反馈为准，
 * 手机这边只负责把命令敲出去，并把「我发过的」记进清单。
 */
public final class HomesPage implements IPhonePage {

    private static final int PAD = 4;
    private static final long TOAST_MS = 2500L;
    private static final int MAX_INPUT = HomesStore.MAX_NAME_LENGTH;

    private final List<String> homes = new java.util.ArrayList<>();
    private boolean addMode;
    private final StringBuilder input = new StringBuilder();
    private String toast = "";
    private long toastUntilMs;
    private int scrollOffset;

    /** 命中区域（render 时记录，click 时消费）。行：[x,y,w,h] + 删除键 [x,y,w,h] */
    private int rowHovered = -1;
    private int rowDelHovered = -1;
    private final java.util.List<int[]> rowRects = new java.util.ArrayList<>();
    private final java.util.List<int[]> delRects = new java.util.ArrayList<>();
    private boolean addHovered;
    private boolean confirmHovered;
    private boolean cancelHovered;
    private boolean bspHovered;
    private int confirmX, confirmY, confirmW, cancelX, cancelY, cancelW;
    private int bspX, bspY, bspW = 12;

    @Override
    public void onOpen() {
        homes.clear();
        homes.addAll(HomesStore.load());
        addMode = false;
        input.setLength(0);
        toast = "";
        scrollOffset = 0;
    }

    @Override
    public void onClose() {
        rowRects.clear();
        delRects.clear();
        rowHovered = -1;
        rowDelHovered = -1;
    }

    /** 服务器有没有启用家园命令：登录时服务器会把命令树同步给客户端，直接查 */
    private static boolean serverHasHomes() {
        ClientPacketListener conn = Minecraft.getInstance().getConnection();
        if (conn == null || conn.getCommands() == null) return true; // 未连接：乐观
        return conn.getCommands().getRoot().getChild("sethome") != null;
    }

    private static void sendChatCommand(String cmd) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) mc.player.connection.sendCommand(cmd);
    }

    private void showToast(String key, Object... args) {
        toast = Component.translatable(key, args).getString();
        toastUntilMs = System.currentTimeMillis() + TOAST_MS;
    }

    private void deleteLastChar() {
        if (!input.isEmpty()) input.deleteCharAt(input.length() - 1);
    }

    private void confirmAdd() {
        String name = input.toString().trim();
        if (!HomesStore.isValidName(name)) {
            showToast("mcphone.homes.name_invalid");
            return;
        }
        if (homes.size() >= HomesStore.MAX_HOMES) {
            showToast("mcphone.homes.limit", HomesStore.MAX_HOMES);
            return;
        }
        if (homes.contains(name)) {
            showToast("mcphone.homes.name_invalid");
            return;
        }
        sendChatCommand("sethome " + name);
        homes.add(name);
        HomesStore.save(homes);
        addMode = false;
        input.setLength(0);
    }

    private void deleteHome(int idx) {
        if (idx < 0 || idx >= homes.size()) return;
        String name = homes.get(idx);
        sendChatCommand("delhome " + name);
        homes.remove(idx);
        HomesStore.save(homes);
    }

    private void teleportHome(int idx) {
        if (idx < 0 || idx >= homes.size()) return;
        sendChatCommand("home " + homes.get(idx));
    }

    @Override
    public void render(com.november.mcphone.api.client.ui.PhoneCanvas c) {
        GuiGraphics g = c.graphics();
        Font font = c.font();
        int x = c.x();
        int y = c.y();
        int w = c.width();
        int bottom = c.y() + c.height();
        int mouseX = c.mouseX();
        int mouseY = c.mouseY();

        rowRects.clear();
        delRects.clear();
        rowHovered = -1;
        rowDelHovered = -1;

        // 服务器没启用：整页提示。文案按 \n 手动分行 —— font.split 对中英混排
        // 的断行会在行尾留大段空白，很难看，短句一行一行画反而整齐
        if (!serverHasHomes()) {
            int ty = y + 10;
            for (String line : Component.translatable("mcphone.homes.no_server").getString().split("\n")) {
                g.drawString(font, line, x + PAD, ty, FontPalette.preview(), false);
                ty += font.lineHeight + 3;
            }
            return;
        }

        // 标题行 + 「+」
        g.drawString(font, Component.translatable("mcphone.app.homes").getString(),
                x + PAD, y + 2, FontPalette.title(), true);
        if (!addMode) {
            String plus = "+";
            int plusW = font.width(plus);
            int plusX = x + w - plusW - PAD - 2;
            addHovered = c.hovered(plusX - 3, y - 2, plusW + 6, font.lineHeight + 4);
            g.drawString(font, plus, plusX, y + 2,
                    addHovered ? FontPalette.title() : FontPalette.link(), true);
        }
        int dividerY = y + 2 + font.lineHeight + 4;
        g.fill(x + PAD, dividerY, x + w - PAD, dividerY + 1, PhoneTheme.COLOR_DIVIDER);
        int ry = dividerY + 4;

        if (addMode) {
            ry = renderAddRow(c, font, x, ry, w, mouseX, mouseY);
        } else if (homes.isEmpty()) {
            int ty = ry;
            for (var line : font.split(Component.translatable("mcphone.homes.empty"), w - PAD * 2)) {
                g.drawString(font, line, x + PAD, ty, FontPalette.preview(), false);
                ty += font.lineHeight + 1;
            }
            ry = ty + 4;
        } else {
            // 清单行（带滚动夹紧）
            int rowH = font.lineHeight + 6;
            int avail = bottom - ry - (toast.isEmpty() ? 0 : font.lineHeight + 2);
            int visible = Math.max(1, avail / rowH);
            int maxOffset = Math.max(0, homes.size() - visible);
            if (scrollOffset > maxOffset) scrollOffset = maxOffset;

            String delLabel = Component.translatable("mcphone.homes.delete").getString();
            for (int i = scrollOffset; i < homes.size(); i++) {
                if (ry + rowH > bottom) break;
                String name = homes.get(i);
                boolean hovered = mouseX >= x + PAD && mouseX <= x + w - PAD
                        && mouseY >= ry && mouseY < ry + rowH;
                if (hovered) {
                    g.fill(x + PAD, ry, x + w - PAD, ry + rowH, PhoneTheme.COLOR_ROW_HOVER);
                    rowHovered = i;
                }
                g.drawString(font, name, x + PAD + 2, ry + 3, FontPalette.title(), false);

                int delW = font.width(delLabel);
                int delX = x + w - PAD - delW - 2;
                boolean delHover = mouseX >= delX - 2 && mouseX <= x + w - PAD
                        && mouseY >= ry && mouseY < ry + rowH;
                if (delHover) {
                    rowDelHovered = i;
                    rowHovered = -1;
                }
                g.drawString(font, delLabel, delX, ry + 3,
                        delHover ? FontPalette.danger() : FontPalette.dim(), false);
                rowRects.add(new int[]{x + PAD, ry, w - PAD * 2, rowH});
                delRects.add(new int[]{delX - 2, ry, delW + 4 + PAD, rowH});
                ry += rowH;
            }
        }

        // 提示条
        if (!toast.isEmpty() && System.currentTimeMillis() <= toastUntilMs) {
            int tw = font.width(toast);
            g.drawString(font, toast, x + Math.max(PAD, (w - tw) / 2), bottom - font.lineHeight,
                    FontPalette.armed(), false);
        }
    }

    private int renderAddRow(com.november.mcphone.api.client.ui.PhoneCanvas c,
                             Font font, int x, int y, int w, int mouseX, int mouseY) {
        GuiGraphics g = c.graphics();
        // 输入框：占位提示 + 已输入内容 + 光标闪烁
        int boxH = font.lineHeight + 8;
        g.fill(x + PAD, y, x + w - PAD, y + boxH, 0x66000000);
        g.renderOutline(x + PAD, y, x + w - PAD, y + boxH, FontPalette.dim());

        boolean blink = System.currentTimeMillis() / 500 % 2 == 0;
        String text = input.toString();
        int tx = x + PAD + 3;
        if (text.isEmpty()) {
            g.drawString(font, Component.translatable("mcphone.homes.add_hint").getString(),
                    tx, y + 4, FontPalette.dim(), false);
        } else {
            g.drawString(font, GuiUtil.truncate(font, text, w - PAD * 2 - bspW - 8),
                    tx, y + 4, FontPalette.title(), false);
        }
        // 闪烁竖线光标：空态在占位文字前，非空在文字末尾 —— 让"这里能输入"一眼可见
        if (blink) {
            int cx = text.isEmpty() ? tx : tx + Math.min(font.width(text), w - PAD * 2 - bspW - 6);
            g.fill(cx, y + 3, cx + 1, y + 3 + font.lineHeight, FontPalette.title());
        }
        // ⌫ 按钮：退格键万一失效时的兜底删除入口（鼠标点击路径已被实测可用）
        String bspLabel = "⌫";
        bspW = font.width(bspLabel) + 6;
        bspX = x + w - PAD - bspW - 2;
        bspY = y;
        bspHovered = mouseX >= bspX && mouseX <= bspX + bspW
                && mouseY >= bspY && mouseY <= bspY + boxH;
        g.fill(bspX, bspY, bspX + bspW, bspY + boxH,
                bspHovered ? PhoneTheme.COLOR_ROW_HOVER : 0x33000000);
        g.drawString(font, bspLabel, bspX + 3, y + 4,
                bspHovered ? FontPalette.danger() : FontPalette.body(), false);
        y += boxH + 4;

        // 确认 / 取消
        String confirm = Component.translatable("mcphone.homes.confirm").getString();
        String cancel = Component.translatable("mcphone.homes.cancel").getString();
        confirmW = font.width(confirm) + 8;
        cancelW = font.width(cancel) + 8;
        cancelX = x + w - PAD - cancelW;
        confirmX = cancelX - confirmW - 6;
        confirmY = y;
        cancelY = y;
        int bh = font.lineHeight + 6;
        confirmHovered = mouseX >= confirmX && mouseX <= confirmX + confirmW
                && mouseY >= confirmY && mouseY <= confirmY + bh;
        cancelHovered = mouseX >= cancelX && mouseX <= cancelX + cancelW
                && mouseY >= cancelY && mouseY <= cancelY + bh;
        g.fill(confirmX, confirmY, confirmX + confirmW, confirmY + bh,
                confirmHovered ? PhoneTheme.COLOR_ROW_HOVER : 0x44000000);
        g.fill(cancelX, cancelY, cancelX + cancelW, cancelY + bh,
                cancelHovered ? PhoneTheme.COLOR_ROW_HOVER : 0x44000000);
        g.drawString(font, confirm, confirmX + 4, confirmY + 3,
                confirmHovered ? FontPalette.confirm() : FontPalette.body(), false);
        g.drawString(font, cancel, cancelX + 4, cancelY + 3,
                cancelHovered ? FontPalette.body() : FontPalette.dim(), false);
        return y + bh + 4;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != BUTTON_LEFT) return false;

        if (addMode) {
            if (confirmHovered) {
                confirmAdd();
                return true;
            }
            if (cancelHovered) {
                addMode = false;
                input.setLength(0);
                return true;
            }
            if (bspHovered) {
                deleteLastChar();
                return true;
            }
            return false; // 输入态：点空白不算，保留输入框焦点
        }

        if (addHovered && homes.size() < HomesStore.MAX_HOMES) {
            addMode = true;
            input.setLength(0);
            return true;
        }
        if (rowDelHovered >= 0) {
            deleteHome(rowDelHovered);
            return true;
        }
        if (rowHovered >= 0) {
            teleportHome(rowHovered);
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!addMode) return false;
        // 回车 = 确认，退格 = 删一个字符。键值走 Mojang 的 InputConstants 抽象，
        // 26.x 从 GLFW 换成 SDL 后键编号会变，不能用 GLFW 的常量。
        if (keyCode == com.mojang.blaze3d.platform.InputConstants.KEY_RETURN
                || keyCode == com.mojang.blaze3d.platform.InputConstants.KEY_NUMPADENTER) {
            confirmAdd();
            return true;
        }
        // 退格：键值之外把两种扫描码（GLFW 259 / SDL 42）也认上，防键值体系切换失效
        if (keyCode == com.mojang.blaze3d.platform.InputConstants.KEY_BACKSPACE
                || scanCode == 259 || scanCode == 42) {
            deleteLastChar();
            return true;
        }
        if (keyCode == com.mojang.blaze3d.platform.InputConstants.KEY_ESCAPE) {
            addMode = false;
            input.setLength(0);
            return true;
        }
        return false;
    }

    @Override
    public boolean charTyped(int codePoint, int modifiers) {
        if (!addMode) return false;
        // 有些环境退格走字符事件（\b=8 / DEL=127），在这里也兜一手
        if (codePoint == 8 || codePoint == 127) {
            deleteLastChar();
            return true;
        }
        if (input.length() >= MAX_INPUT) return true; // 吃掉但不加：到上限了
        char ch = (char) codePoint;
        if (Character.isWhitespace(ch)) return true; // 家名不允许空白
        input.append(ch);
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (addMode) return false;
        if (amount > 0 && scrollOffset > 0) {
            scrollOffset--;
            return true;
        }
        if (amount < 0 && scrollOffset < homes.size() - 1) {
            scrollOffset++;
            return true;
        }
        return false;
    }

    @Override
    public boolean capturesKeyboard() {
        return addMode; // 输入家名时吃掉背包键（玩家打拼音必然按到 e）
    }
}
