package com.november.mcphone.feature.homes.client;

import com.november.mcphone.api.client.app.RequiredMod;
import com.november.mcphone.core.client.PhoneApp;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;

import java.util.List;

/**
 * 家园 App：配合服务器的 NeoEssentials，在手机上管理自己的家。
 *
 * <h2>为什么是「联动」而不是「前置」</h2>
 *
 * NeoEssentials 装在<b>服务器</b>上，玩家客户端不需要装。MCphone 的联动声明
 * {@link #companionMods()} 只负责在「商店 → 联动App」页和「设置 → 关于」里
 * 告诉玩家它靠谁活着；可用性走 {@link #isAvailable()} 自己判。
 *
 * <h2>怎么判定服务器有没有 NeoEssentials</h2>
 *
 * 客户端拿到的是服务器登录时同步下来的命令树 —— NeoEssentials 注册了
 * {@code /sethome}，命令树里就有 sethome 节点。查它比猜 modid 可靠得多：
 * 换了同类插件（提供同名命令的任何 Essentials 系）本 App 照样可用。
 *
 * 注意目录只在第一次构建时问一次 {@code isAvailable()}（见
 * PhoneScreenRegistry 的 ensureLoaded），那时多半还没连上服务器，所以这里
 * 乐观返回 true，图标恒在；进服后由页面自己渲染「服务器未启用」的兜底状态。
 *
 * <h2>数据与动作</h2>
 *
 * 家的真实数据在服务器存档里，手机侧只记一份「通过本 App 发过的名字」
 * （见 {@link HomesStore}）；动作全部走聊天命令：sethome / home / delhome。
 * 冷却、上限、名字校验都由服务器裁决，聊天栏反馈即结果。
 *
 * 贴图: assets/mcphone/textures/app/homes.png (20×20)
 */
public final class HomesApp extends PhoneApp {

    public HomesApp() {
        super("homes");
    }

    @Override
    public List<RequiredMod> companionMods() {
        return List.of(new RequiredMod("neoessentials", "NeoEssentials"));
    }

    /**
     * 乐观可用：目录构建时多半还没连服务器，命令树查不出来，按「有」处理。
     * 服务器真没装的话，页面上会显示兜底说明 —— 图标消失不了，但点了也不坑。
     */
    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public net.minecraft.network.chat.Component getDisplayName() {
        // 语言键 mcphone.app.homes 已在语言文件里，走基类的翻译键即可
        return super.getDisplayName();
    }

    @Override
    public void onPress() {
        // 覆盖了 openPage() 后这里不会被调到；接口要求实现，留空
    }

    /** 不预装：走应用商店的「联动App」页，用一张床（任意颜色）购买 */
    @Override
    public boolean isPreinstalled() { return false; }

    @Override
    public com.november.mcphone.api.client.ui.IPhonePage openPage() {
        return new HomesPage();
    }

    /**
     * 联动前置满足 = 服务器启用了家园命令。NeoEssentials 是纯服务器端 mod，
     * 客户端的 ModPresence 永远是 false，必须查服务器同步下来的命令树。
     */
    @Override
    public boolean companionModsSatisfied() {
        return serverHasSethome();
    }

    /** 顺手给出一个运行时判定，页面兜底渲染时用同一来源 */
    public static boolean serverHasSethome() {
        ClientPacketListener conn = Minecraft.getInstance().getConnection();
        if (conn == null || conn.getCommands() == null) return true;
        return conn.getCommands().getRoot().getChild("sethome") != null;
    }
}
