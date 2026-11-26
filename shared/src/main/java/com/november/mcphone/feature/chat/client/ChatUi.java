package com.november.mcphone.feature.chat.client;

import com.november.mcphone.core.client.GuiUtil;
import com.november.mcphone.core.client.FontPalette;
import com.november.mcphone.core.client.PhoneSkin;
import com.november.mcphone.core.client.PhoneTheme;
import com.november.mcphone.platform.client.Transforms;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.FormattedCharSequence;

/** 聊天界面共用的字号与像素图标，沿用手机的字体配色和资源包气泡。 */
public final class ChatUi {
    private ChatUi() {}

    enum Icon { BACK, MORE, VOICE, SMILE, PLUS, ADD, CHECK, CHAT, CONTACTS, DISCOVER, PROFILE, SEARCH, ADD_CONTACT }

    static int accent() {
        return FontPalette.current().darkText() ? 0xFFA64A78 : 0xFFF3ACCE;
    }

    static void glass(GuiGraphics g, ChatGlass.Surface surface,
                      int x, int y, int w, int h, boolean hovered) {
        Transforms.push(g);
        try {
            Transforms.scale(g, 1f / ChatGlass.PRECISION, 1f / ChatGlass.PRECISION);
            // 玩家头部不裁圆，外框也用方角，与 MC 的方块像素保持一致。
            int radius = surface == ChatGlass.Surface.AVATAR ? 0 : 3;
            ChatGlass.paint(g::fill, x, y, w, h, radius,
                    ChatGlass.palette(surface, FontPalette.current().darkText(), hovered));
        } finally {
            Transforms.pop(g);
        }
    }

    static void bubble(GuiGraphics g, boolean self, int x, int y, int w, int h) {
        surface(g, self ? PhoneSkin.Element.CHAT_BUBBLE_SELF : PhoneSkin.Element.CHAT_BUBBLE_PEER,
                x,y,w,h, self ? PhoneTheme.COLOR_CHAT_BUBBLE_SELF : PhoneTheme.COLOR_CHAT_BUBBLE_PEER,
                self ? PhoneTheme.COLOR_CHAT_BUBBLE_SELF_EDGE : PhoneTheme.COLOR_CHAT_BUBBLE_PEER_EDGE);
    }
    static void inputBar(GuiGraphics g, int x, int y, int w, int h) {
        surface(g,PhoneSkin.Element.CHAT_INPUT_BAR,x,y,w,h,PhoneTheme.COLOR_CHAT_INPUT_BG,PhoneTheme.COLOR_CHAT_INPUT_EDGE);
    }
    private static void surface(GuiGraphics g, PhoneSkin.Element element, int x, int y, int w, int h, int fill, int edge) {
        if (PhoneSkin.draw(g,element,x,y,w,h)) return;
        Transforms.push(g);
        try {
            Transforms.scale(g,1f/ChatGlass.PRECISION,1f/ChatGlass.PRECISION);
            ChatGlass.paint(g::fill,x,y,w,h,3,new ChatGlass.Palette(fill,fill,edge,edge));
        } finally { Transforms.pop(g); }
    }

    static int width(Font font, String text, float scale) {
        return ChatLayout.scaledWidth(font.width(text), scale);
    }

    static int lineHeight(Font font, float scale) {
        return ChatLayout.scaledWidth(font.lineHeight, scale);
    }

    static String truncate(Font font, String text, int width, float scale) {
        return GuiUtil.truncate(font, text, ChatLayout.unscaledWidth(width, scale));
    }

    static void text(GuiGraphics g, Font font, String text, float x, float y, float scale, int color) {
        text(g, font, net.minecraft.network.chat.Component.literal(text).getVisualOrderText(),
                x, y, scale, color);
    }

    static void text(GuiGraphics g, Font font, FormattedCharSequence text,
                     float x, float y, float scale, int color) {
        Transforms.push(g);
        try {
            Transforms.translate(g, x, y);
            Transforms.scale(g, scale, scale);
            g.drawString(font, text, 0, 0, color, false);
        } finally {
            Transforms.pop(g);
        }
    }

    /** 用未取整的字宽居中，避免小字号时向左偏半个逻辑像素。 */
    static void centeredText(GuiGraphics g, Font font, String text,
                             float centerX, float y, float scale, int color) {
        Transforms.push(g);
        try {
            Transforms.translate(g, centerX - font.width(text) * scale / 2f, y);
            Transforms.scale(g, scale, scale);
            g.drawString(font, text, 0, 0, color, false);
        } finally {
            Transforms.pop(g);
        }
    }

    /** 行尾字间距不参与水平居中；纵向复用已校准的默认字形指标。 */
    static void buttonText(GuiGraphics g, Font font, String text,
                           float centerX, float centerY, float scale, int color) {
        label(g, font, text, ChatLayout.labelLeft(font.width(text), scale, centerX), centerY, scale, color);
    }

    /** 左对齐的标签与图标共享纵向中心；不改变消息正文的绘制原点。 */
    static void label(GuiGraphics g, Font font, String text,
                      float x, float centerY, float scale, int color) {
        text(g, font, text, x, ChatLayout.labelTop(font.lineHeight, scale, centerY), scale, color);
    }

    static void forwardIcon(GuiGraphics g, float centerX, float centerY, int size, int color) {
        Transforms.push(g);
        try {
            Transforms.translate(g, centerX, centerY);
            Transforms.scale(g, -1, 1);
            sizedIcon(g, Icon.BACK, -size / 2f, -size / 2f, size, color);
        } finally { Transforms.pop(g); }
    }

    static void centeredIcon(GuiGraphics g, Icon icon, float centerX, int y, int color) {
        Transforms.push(g);
        try {
            Transforms.translate(g, centerX - ChatLayout.ICON_SIZE / 2f, y);
            icon(g, icon, 0, 0, color);
        } finally {
            Transforms.pop(g);
        }
    }

    /** 首页摘要始终单行；只处理显示文本，不改变存储的原消息。 */
    static void preview(GuiGraphics g, Font font, String text, int x, float y, int width, int color) {
        if (width <= 0) return;
        String singleLine = text.replace('\r', ' ').replace('\n', ' ').replace('\t', ' ');
        text(g, font, truncate(font, singleLine, width, ChatLayout.META_SCALE), x, y,
                ChatLayout.META_SCALE, color);
    }

    /** 按钮及子包的菜单高亮共用细分圆角，尺寸和命中区保持逻辑像素。 */
    public static void roundedFill(GuiGraphics g, int x, int y, int w, int h, int color) {
        Transforms.push(g);
        try {
            Transforms.scale(g, 1f / ChatGlass.PRECISION, 1f / ChatGlass.PRECISION);
            ChatGlass.paintSolid(g::fill, x, y, w, h, 2, color);
        } finally {
            Transforms.pop(g);
        }
    }

    static void sizedIcon(GuiGraphics g, Icon icon, float x, float y, int size, int color) {
        Transforms.push(g);
        try {
            Transforms.translate(g, x, y);
            Transforms.scale(g, size / (float) ChatLayout.ICON_SIZE, size / (float) ChatLayout.ICON_SIZE);
            icon(g, icon, 0, 0, color);
        } finally {
            Transforms.pop(g);
        }
    }

    /** 优先使用用户提供的透明贴图；没有对应素材或资源包删除贴图时才画几何兜底。 */
    static void icon(GuiGraphics g, Icon icon, int x, int y, int color) {
        PhoneSkin.Element texture = switch (icon) {
            case CHAT -> PhoneSkin.Element.CHAT_TAB_MESSAGE;
            case CONTACTS -> PhoneSkin.Element.CHAT_TAB_CONTACTS;
            case ADD_CONTACT -> PhoneSkin.Element.CHAT_ADD_CONTACT;
            case DISCOVER -> PhoneSkin.Element.CHAT_TAB_DISCOVER;
            case PROFILE -> PhoneSkin.Element.CHAT_TAB_PROFILE;
            case SEARCH -> PhoneSkin.Element.CHAT_SEARCH;
            case PLUS -> PhoneSkin.Element.CHAT_ATTACH;
            case BACK -> PhoneSkin.Element.CHAT_BACK;
            case MORE -> PhoneSkin.Element.CHAT_MORE;
            case SMILE -> PhoneSkin.Element.CHAT_SMILE;
            case VOICE -> PhoneSkin.Element.CHAT_VOICE;
            case ADD, CHECK -> null;
        };
        if (texture != null) {
            boolean drawn = PhoneSkin.draw(g, texture, x, y,
                    ChatLayout.ICON_SIZE, ChatLayout.ICON_SIZE, color);
            if (drawn) return;
        }
        switch (icon) {
            case ADD, CHECK -> {
                // 原生几何标志不依赖字体中的 ✓ 字形，也不复用附件的圆圈加号。
                Transforms.push(g);
                try {
                    Transforms.translate(g, x, y);
                    Transforms.scale(g, ChatLayout.ICON_SIZE / 24f, ChatLayout.ICON_SIZE / 24f);
                    mark(g::fill, icon, color);
                } finally { Transforms.pop(g); }
            }
            case BACK -> {
                // 在 24 像素坐标中画细箭头再缩放，避免旧版 9 像素台阶过粗、重心贴左。
                Transforms.push(g);
                try {
                    Transforms.translate(g, x, y);
                    Transforms.scale(g, ChatLayout.ICON_SIZE / 24f, ChatLayout.ICON_SIZE / 24f);
                    for (int i = 0; i < 9; i++) {
                        g.fill(14 - i, 3 + i, 16 - i, 5 + i, color);
                        g.fill(14 - i, 19 - i, 16 - i, 21 - i, color);
                    }
                } finally {
                    Transforms.pop(g);
                }
            }
            case MORE -> {
                for (int i = 0; i < 3; i++) g.fill(x + i * 3, y + 4, x + i * 3 + 2, y + 6, color);
            }
            case PLUS, SMILE, VOICE, DISCOVER, SEARCH -> {
                ring(g, x, y, color);
                switch (icon) {
                    case PLUS -> {
                        g.fill(x + 2, y + 4, x + 7, y + 5, color);
                        g.fill(x + 4, y + 2, x + 5, y + 7, color);
                    }
                    case SMILE -> {
                        g.fill(x + 2, y + 3, x + 3, y + 4, color);
                        g.fill(x + 6, y + 3, x + 7, y + 4, color);
                        g.fill(x + 3, y + 6, x + 6, y + 7, color);
                    }
                    case VOICE -> {
                        g.fill(x + 3, y + 3, x + 4, y + 6, color);
                        g.fill(x + 5, y + 2, x + 6, y + 7, color);
                    }
                    case DISCOVER -> {
                        g.fill(x + 5, y + 2, x + 6, y + 4, color);
                        g.fill(x + 3, y + 4, x + 5, y + 6, color);
                    }
                    case SEARCH -> g.fill(x + 7, y + 7, x + 9, y + 9, color);
                    default -> { }
                }
            }
            case CHAT -> {
                g.renderOutline(x, y + 1, 9, 6, color);
                g.fill(x + 2, y + 7, x + 3, y + 9, color);
            }
            case CONTACTS, PROFILE, ADD_CONTACT -> {
                g.renderOutline(x + 3, y, 3, 4, color);
                g.renderOutline(x + 1, y + 5, 7, 4, color);
                if (icon == Icon.CONTACTS) g.fill(x + 7, y + 2, x + 9, y + 3, color);
                if (icon == Icon.ADD_CONTACT) {
                    g.fill(x + 6, y + 5, x + 9, y + 6, color);
                    g.fill(x + 7, y + 4, x + 8, y + 7, color);
                }
            }
        }
    }

    /** 24 像素局部坐标；两枚标志的可见轮廓都以 (12,12) 为中心。 */
    static void mark(ChatGlass.Fill fill, Icon icon, int color) {
        if (icon == Icon.ADD) {
            fill.rectangle(3, 11, 21, 13, color);
            fill.rectangle(11, 3, 13, 21, color);
        } else if (icon == Icon.CHECK) {
            for (int i = 0; i < 5; i++) fill.rectangle(3 + i, 13 + i, 5 + i, 15 + i, color);
            for (int i = 0; i < 13; i++) fill.rectangle(7 + i, 17 - i, 9 + i, 19 - i, color);
        }
    }

    private static void ring(GuiGraphics g, int x, int y, int color) {
        g.fill(x + 2, y, x + 7, y + 1, color);
        g.fill(x + 2, y + 8, x + 7, y + 9, color);
        g.fill(x, y + 2, x + 1, y + 7, color);
        g.fill(x + 8, y + 2, x + 9, y + 7, color);
        g.fill(x + 1, y + 1, x + 2, y + 2, color);
        g.fill(x + 7, y + 1, x + 8, y + 2, color);
        g.fill(x + 1, y + 7, x + 2, y + 8, color);
        g.fill(x + 7, y + 7, x + 8, y + 8, color);
    }
}
