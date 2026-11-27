package com.november.mcphone.feature.homes.client;

import com.november.mcphone.api.client.ui.IPhonePage;
import com.november.mcphone.api.client.ui.PhoneCanvas;
import com.november.mcphone.core.client.FontPalette;
import com.november.mcphone.core.client.PhoneTheme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;

import java.util.List;

/**
 * 家园页：数据由 {@link HomesSync} 从服务器实时同步（/listhomes 解析），
 * 动作全部走聊天命令，结果以服务器聊天栏反馈为准。
 *
 * 三个交互约定（实测迭代出来的）：
 * - 光标始终闪烁的竖线，空态也显示 —— 让"这里能输入"一眼可见；
 * - 退格三重保险（键值 / 双扫描码 / 字符事件），见 keyPressed 与 charTyped；
 * - 不做"已发送"之类的成功提示 —— 命令结果服务器会刷在聊天栏。
 */
public final class HomesPage implements IPhonePage {

    private static final int PAD = 4;
    private static final long TOAST_MS = 2500L;
    private static final int MAX_INPUT = HomesSync.MAX_NAME_LENGTH;

    private boolean addMode;
    private final StringBuilder input = new StringBuilder();
    private String toast = "";
    private long toastUntilMs;
    private int scrollOffset;

    private int rowHovered = -1;
    private int rowDelHovered = -1;
    private final java.util.List<int[]> rowRects = new java.util.ArrayList<>();
    private final java.util.List<int[]> delRects = new java.util.ArrayList<>();
    private boolean addHovered;
    private boolean confirmHovered;
    private boolean cancelHovered;
    private int confirmX, confirmY, confirmW, cancelX, cancelY, cancelW;

    @Override
    public void onOpen() {
        addMode = false;
        input.setLength(0);
        toast = "";
        scrollOffset = 0;
        HomesSync.startSync();
    }

    @Override
    public void onClose() {
        rowRects.clear();
        delRects.clear();
        rowHovered = -1;
        rowDelHovered = -1;
    }

    @Override
    public void tick() {
        HomesSync.tick(); // 捕获窗口收口 + 自动 /confirm、延迟重同步
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
        if (!HomesSync.isValidName(name)) {
            showToast("mcphone.homes.name_invalid");
            return;
        }
        if (homes().contains(name)) {
            showToast("mcphone.homes.name_invalid");
            return;
        }
        addMode = false;
        input.setLength(0);
        HomesSync.requestSet(name); // /sethome + 2 秒后重同步
    }

    private void deleteHome(int idx) {
        List<String> homes = homes();
        if (idx < 0 || idx >= homes.size()) return;
        HomesSync.requestDelete(homes.get(idx)); // /delhome + 自动 /confirm + 重同步
    }

    private void teleportHome(int idx) {
        List<String> homes = homes();
        if (idx < 0 || idx >= homes.size()) return;
        sendChatCommand("home " + homes.get(idx));
    }

    private List<String> homes() { return HomesSync.homes(); }

    @Override
    public void render(PhoneCanvas c) {
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

        // 服务器没启用：整页提示
        if (!serverHasHomes()) {
            int ty = y + 10;
            for (String line : Component.translatable("mcphone.homes.no_server").getString().split("\n")) {
                g.drawString(font, line, x + PAD, ty, FontPalette.preview(), false);
                ty += font.lineHeight + 3;
            }
            return;
        }

        // 标题行（带服务器同步来的已用/上限）+ 「+」
        String title = Component.translatable("mcphone.app.homes").getString();
        int limit = HomesSync.limit();
        if (!addMode && limit > 0) {
            title += " " + homes().size() + "/" + limit;
        }
        g.drawString(font, title, x + PAD, y + 2, FontPalette.title(), true);
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

        if (HomesSync.isSyncing() && homes().isEmpty()) {
            g.drawString(font, Component.translatable("mcphone.homes.syncing").getString(),
                    x + PAD, ry, FontPalette.dim(), false);
            ry += font.lineHeight + 4;
        } else if (addMode) {
            ry = renderAddRow(c, font, x, ry, w, mouseX, mouseY);
        } else if (homes().isEmpty()) {
            int ty = ry;
            for (String line : Component.translatable("mcphone.homes.empty").getString().split("\n")) {
                g.drawString(font, line, x + PAD, ty, FontPalette.preview(), false);
                ty += font.lineHeight + 3;
            }
            ry = ty + 4;
        } else {
            int rowH = font.lineHeight + 6;
            int avail = bottom - ry - (toast.isEmpty() ? 0 : font.lineHeight + 2);
            int visible = Math.max(1, avail / rowH);
            int maxOffset = Math.max(0, homes().size() - visible);
            if (scrollOffset > maxOffset) scrollOffset = maxOffset;

            String delLabel = Component.translatable("mcphone.homes.delete").getString();
            for (int i = scrollOffset; i < homes().size(); i++) {
                if (ry + rowH > bottom) break;
                String name = homes().get(i);
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

        if (!toast.isEmpty() && System.currentTimeMillis() <= toastUntilMs) {
            int tw = font.width(toast);
            g.drawString(font, toast, x + Math.max(PAD, (w - tw) / 2), bottom - font.lineHeight,
                    FontPalette.armed(), false);
        }
    }

    private int renderAddRow(PhoneCanvas c, Font font, int x, int y, int w, int mouseX, int mouseY) {
        GuiGraphics g = c.graphics();
        boolean blink = System.currentTimeMillis() / 500 % 2 == 0;
        int boxH = font.lineHeight + 8;
        g.fill(x + PAD, y, x + w - PAD, y + boxH, 0x66000000);
        g.renderOutline(x + PAD, y, x + w - PAD, y + boxH, FontPalette.dim());

        String text = input.toString();
        int tx = x + PAD + 3;
        if (text.isEmpty()) {
            g.drawString(font, Component.translatable("mcphone.homes.add_hint").getString(),
                    tx, y + 4, FontPalette.dim(), false);
        } else {
            g.drawString(font, text, tx, y + 4, FontPalette.title(), false);
        }
        // 闪烁竖线光标：空态在占位文字前，非空在文字末尾
        if (blink) {
            int cx = text.isEmpty() ? tx : tx + font.width(text);
            g.fill(cx, y + 3, cx + 1, y + 3 + font.lineHeight, FontPalette.title());
        }
        y += boxH + 4;

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
            return false;
        }

        if (addHovered) {
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
        if (input.length() >= MAX_INPUT) return true;
        char ch = (char) codePoint;
        if (Character.isWhitespace(ch)) return true;
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
        if (amount < 0 && scrollOffset < homes().size() - 1) {
            scrollOffset++;
            return true;
        }
        return false;
    }

    @Override
    public boolean capturesKeyboard() {
        return addMode;
    }
}
