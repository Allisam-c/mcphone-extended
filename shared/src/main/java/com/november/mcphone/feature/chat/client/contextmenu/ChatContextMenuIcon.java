package com.november.mcphone.feature.chat.client.contextmenu;

import com.november.mcphone.core.client.PhoneSkin;
import com.november.mcphone.platform.client.Transforms;
import net.minecraft.client.gui.GuiGraphics;

/** 白色透明图标随菜单颜色着色；缺图时以小型几何轮廓兜底。 */
final class ChatContextMenuIcon {
    private ChatContextMenuIcon() {}
    static void draw(GuiGraphics g,ChatContextMenuView.Icon icon,float x,float centerY,int color) {
        PhoneSkin.Element element = switch(icon) {
            case REPLY -> PhoneSkin.Element.CHAT_REPLY;
            case COPY -> PhoneSkin.Element.CHAT_COPY;
            case DELETE -> PhoneSkin.Element.CHAT_DELETE;
            case PASTE -> PhoneSkin.Element.CHAT_PASTE;
            case CREATE_GROUP -> PhoneSkin.Element.CHAT_CREATE_GROUP;
            case ADD_CONTACT -> PhoneSkin.Element.CHAT_ADD_CONTACT;
            case FAVORITE -> PhoneSkin.Element.CHAT_FAVORITE;
        };
        Transforms.push(g);
        try {
            // 96px 源图的可见轮廓中心：复制向右 1/6、粘贴向左 1/6 逻辑像素，删除居中。
            float correction = switch(icon) { case COPY -> 1f/6f; case PASTE -> -1f/6f; default -> 0; };
            Transforms.translate(g,x+correction,centerY-ChatContextMenuLayout.ICON_SIZE/2f);
            boolean drawn = element != null
                    && PhoneSkin.draw(g,element,0,0,ChatContextMenuLayout.ICON_SIZE,
                            ChatContextMenuLayout.ICON_SIZE,color);
            if (drawn) return;
            Transforms.scale(g,ChatContextMenuLayout.ICON_SIZE/24f,ChatContextMenuLayout.ICON_SIZE/24f);
            switch(icon) {
                case REPLY -> { /* 引用只使用已记录的矢量素材，不手画缺图替代。 */ }
                case FAVORITE -> { outline(g,6,3,18,21,color); g.fill(9,15,15,17,color); }
                case COPY -> { outline(g,7,2,20,18,color); g.fill(3,6,5,22,color); g.fill(3,20,16,22,color); }
                case DELETE -> { outline(g,6,7,18,21,color); g.fill(4,4,20,6,color); g.fill(9,2,15,4,color); }
                case PASTE -> { outline(g,4,4,20,22,color); g.fill(8,2,16,6,color); g.fill(10,13,22,15,color); }
                case CREATE_GROUP -> { outline(g,3,3,21,18,color); g.fill(14,18,17,21,color); plus(g,12,10,color); }
                case ADD_CONTACT -> { outline(g,6,3,13,10,color); outline(g,3,14,13,21,color); plus(g,18,18,color); }
            }
        } finally { Transforms.pop(g); }
    }
    private static void outline(GuiGraphics g,int x1,int y1,int x2,int y2,int color) {
        g.fill(x1,y1,x2,y1+2,color); g.fill(x1,y2-2,x2,y2,color);
        g.fill(x1,y1+2,x1+2,y2-2,color); g.fill(x2-2,y1+2,x2,y2-2,color);
    }
    private static void plus(GuiGraphics g,int x,int y,int color) {
        g.fill(x-3,y-1,x+4,y+1,color); g.fill(x-1,y-3,x+1,y+4,color);
    }
}
