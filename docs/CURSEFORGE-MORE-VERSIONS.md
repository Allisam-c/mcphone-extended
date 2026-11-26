# MCphone - More Versions

## A working smartphone for modern Minecraft

MCphone adds a complete, interactive smartphone to Minecraft. It is not a static prop
or a menu with a telephone texture: it is a small operating system with apps, local
media, persistent player data, multiplayer chat, customization, and an add-on API.

This CurseForge project is the **unofficial newer-version port** of
[MCphone](https://www.curseforge.com/minecraft/mc-mods/mcphone) by **november521**.
It preserves the original mod's content and add-on API while providing builds for
Minecraft **26.2 and 26.3** on **NeoForge and Fabric**.

The original author and upstream project deserve the credit for the design and
original implementation. This port is not presented as the official upstream release.

## What the player gets

### A real device, not just another GUI

- Craft a Phone or upgrade it into a Tablet.
- Open the device while holding it, or press the rebindable open-phone key while it is
  carried in your inventory.
- Keep the Phone in your off hand to display it as a movable, resizable HUD while you
  continue playing.
- Drag and rearrange home-screen icons, use multiple app pages, scale the interface,
  name the device, choose wallpapers, change font colors, and apply resource-pack
  themes.
- The Phone and Tablet share the same player data, including apps, notes, settings,
  chats, purchases, music, and terminal contents. The Tablet simply provides a wider
  screen and more home-screen space.

This turns the phone into a persistent personal tool instead of a one-off novelty.
Screenshots, notes, conversations, app purchases, and customization remain part of the
player's experience across play sessions.

## Built-in apps and what they change

### Camera

Enter a first-person camera mode with a live viewfinder, shutter flash, and configurable
capture behavior. Photos are saved into Minecraft's normal screenshots folder and can
include an optional coordinate stamp showing where the picture was taken. The result is
an in-game camera whose photos can immediately be viewed, shared, or kept as memories
of a build or adventure.

### Gallery

Browse photos and ordinary F2 screenshots in a thumbnail grid. Open a full-size view,
move between images, delete unwanted shots, or open the folder directly. The Gallery
also feeds the photo picker used by chat, making screenshots useful inside the game
instead of being files the player only sees outside Minecraft.

### Music

Play the player's own music files from `mcphone/music/`, with volume control and repeat,
single-track, or shuffle modes. Music discs can be inserted into the phone's disc tray
and will continue playing as the player walks around, creating a portable music
experience that other nearby players can hear.

### Axolotl chat

Axolotl is a full social app for multiplayer servers:

- Send private messages and create group chats.
- Message players who are currently offline; history is stored with the world or
  server.
- Add friends, search contacts, view player profiles, and use online/offline states.
- Send photos, animated GIFs, imported stickers, coordinates, and replies.
- Favorite text and images, save received images to the Gallery, and keep important
  conversations available for later.
- Optionally teleport to online friends when the server owner enables that feature.

This gives players a private, phone-like communication layer without forcing them to
leave the game or depend on an external chat program.

### Clock and Weather

The Clock shows in-game time, real-world time, the current day, and how long remains
until sunrise or sunset. It continues to give useful surface-time information in the
Nether, where a vanilla clock spins uselessly.

The Weather app reports conditions for the player's actual biome, distinguishes rain,
snow, storms, clear weather, and dry desert weather, and offers practical advice for
the current conditions. It helps players decide whether to travel, fish, build, mine, or
wait out the night.

### Notes

Create text notes that remain attached to the player and survive death. A finished note
can be printed into a book and handed to another player. Notes are useful for shopping
lists, build plans, coordinates, server rules, or roleplay documents without consuming
inventory slots.

### Ender Chest

Open the player's real vanilla Ender Chest from the phone. It shares its contents with
placed Ender Chests and works across dimensions. The app is purchased with one Ender
Chest, so it acts as a portable convenience rather than a free extra storage dimension.

### Reading

Keep `.txt` novels in `mcphone/books/` and read them on a phone-sized or fullscreen
reader with chapter detection, remembered reading progress, keyboard page turning, and
a personal shelf. When Patchouli, GuideME, or Immersive Engineering is installed, the
same app can also collect the pack's guidebooks into one searchable library. Players no
longer have to carry a stack of manuals or leave the game to read a local novel.

### App Store and Settings

The App Store installs and removes apps and remembers purchases and installation state.
It can also discover apps provided by other mods and script-based apps. Settings control
wallpapers, font color, UI scale, device name, the off-hand HUD, per-app hotkeys, the app
manager, and other client preferences.

### Integration apps

When the relevant companion mod is installed and compatible, MCphone can add:

- **Warp Stone:** open the Waystones travel selection screen from the phone.
- **Browser:** use a real web browser inside Minecraft through MCEF.
- **Quests:** open the pack's FTB Quests book from the home screen.
- **Terminal:** open an AE2, Refined Storage, or Tom's Simple Storage terminal from the
  phone, with a dedicated terminal slot in the phone.
- **Reader:** collect guidebooks from Patchouli, GuideME, and Immersive Engineering.
- **Disc tray integration:** accept music discs created by NetMusic or compatible mods.

Optional apps are hidden when their required mod is missing or has no compatible build.
The mod does not leave broken icons that error when tapped.

## Designed for modpacks and add-on authors

MCphone exposes a Java SPI app system. Another mod can implement `IPhoneApp` and register
it without MCphone needing to know that the add-on exists. App authors can provide pages,
storage, notifications, server communication, and their own icons inside the phone.

The newer-version port preserves the upstream add-on API so existing compatible apps can
continue to target the same concepts. API documentation is available in the upstream
project:
[Add-on API](https://github.com/november521/mcphone/blob/main/docs/addon-api.md).

## Supported versions

| Minecraft | Loaders | Notes |
|---|---|---|
| 26.3 | NeoForge, Fabric | Current newer-version build |
| 26.2 | NeoForge, Fabric | Current newer-version build |

Fabric installations require Fabric API. These builds use Java 25. Install MCphone on
both the client and the server, and use the same MCphone version on both sides.

Optional integration mods are not required for the phone itself. Their availability
depends on whether a compatible build exists for the selected Minecraft version and
loader.

For the original supported release lines, see the
[upstream MCphone project](https://www.curseforge.com/minecraft/mc-mods/mcphone).

## Installation

1. Back up the world before changing mods.
2. Install Minecraft 26.2 or 26.3 with the matching NeoForge or Fabric loader.
3. On Fabric, also install a compatible Fabric API.
4. Download the MCphone file whose Minecraft version and loader exactly match the
   instance.
5. Put the same jar and version in the client's and server's `mods` folders.
6. Start the game. Optional integration apps appear only when their companion mods are
   installed.

Do not mix jars from different Minecraft versions or loaders.

## Why download MCphone - More Versions?

MCphone gives Minecraft players one customizable device for photography, media,
messaging, notes, travel, storage access, reading, quests, and server-aware apps. It
adds convenience without removing the game's progression: some apps cost their
in-world equivalent, multiplayer features can be controlled by the server owner, and
optional integrations are installed only when the pack supports them.

It is especially useful for multiplayer worlds and modpacks because it creates a shared
social and utility layer that feels native to Minecraft, while still giving players the
privacy and personalization of a device that carries their own data.

## Source, license, and credits

- Original mod and concept: **november521**
- Upstream source: <https://github.com/november521/mcphone>
- Newer-version port source: <https://github.com/Beijingdragon233/mcphone_fabric>
- License: **MIT**

MCphone - More Versions is an unofficial compatibility port. Please report port-specific
problems to the port repository, and support the original project as well.

---

# MCphone - More Versions（简体中文）

## 把一台真正可用的手机带进新版本 Minecraft

MCphone 不是电话外形的装饰方块，也不是只有一张贴图的菜单。它是一套能长期使用的
手机系统：有 App、有本地媒体、有玩家数据、有多人聊天、有外观定制，也有一套允许其他
模组添加 App 的接口。

本 CurseForge 项目是 **november521 原作 MCphone 的非官方新版本移植**，保留原作内容
与附属 API，并为 **Minecraft 26.2、26.3** 的 **NeoForge 与 Fabric** 提供构建。
原作的创意与基础实现归原作者所有，本项目不冒充官方上游版本。

## 玩家实际能体验什么

- 合成手机，或者把手机升级成屏幕更大的平板。
- 拿在手上右键开机；把手机放在物品栏里也可以按可改绑的快捷键打开。
- 把手机放在副手，可把它作为可移动、可缩放的 HUD 挂在游戏画面上。
- 主屏图标可拖动排序，支持多页；界面可缩放；还能设置设备名、壁纸、字体颜色，
  并通过资源包更换整套界面外观。
- 手机和平板共用同一份玩家数据，包括 App、笔记、聊天、购买记录、音乐和终端卡槽。
  平板不是第二台手机，只是同一台设备有了更宽的屏幕和更多主屏空间。

它不是一次性玩具。拍下的照片、写下的笔记、聊过的内容、买过的 App 和自己的界面
设置都会留在玩家的长期体验中。

## 内建 App

### 相机

进入带实时取景框的拍照模式，可选择快门闪光效果，并把照片保存到 Minecraft 的截图
目录。照片可以写入拍摄坐标，之后能直接在相册里查看、删除或发给好友。它让玩家能在
游戏里记录建筑和旅程，不必切出游戏另行截图。

### 相册

以缩略图网格浏览相机照片与普通 F2 截图，支持分页、大图查看、左右切换和删除，也能
一键打开截图文件夹。聊天选图同样从这里取图，因此截图会真正成为游戏内可使用的图片，
而不是只存在于磁盘上的文件。

### 音乐

从 `mcphone/music/` 播放玩家自己的音乐，支持音量、列表循环、单曲循环和随机播放。
唱片也能放进手机唱片仓，玩家走到哪里音乐就跟到哪里，附近玩家同样能听见。

### 美西螈聊天

- 私聊与群聊。
- 对方离线时也能发送，记录保存在世界或服务器中，下次上线可读。
- 好友申请、联系人搜索、在线状态和玩家资料。
- 发送图片、动态 GIF、自定义表情、坐标和引用回复。
- 收藏文字与图片，把收到的图片保存到相册。
- 服主允许时，可从在线好友列表传送到对方身边。

它把多人服务器的社交体验做成真正的手机聊天，而不是离开游戏使用外部软件。

### 时钟与天气

时钟显示游戏时间、现实时间、天数以及距离天亮或天黑还有多久。在下界里，原版时钟只会
乱转，而这个时钟仍然显示地表时间。天气 App 会按当前生物群系判断晴、雨、雪、雷暴和
沙漠干雨，并根据当前环境给出适合做什么的提示，帮助玩家决定赶路、钓鱼、施工还是回屋。

### 记事本

笔记跟着玩家走，死亡也不会丢。写完可以印成一本成书交给别人。它适合记录材料清单、
建筑计划、坐标、服务器规则或角色扮演文本，而且不占物品栏。

### 末影箱

随时打开玩家自己的原版末影箱，与放置在世界里的末影箱完全互通并支持跨维度。这个 App
需要用 1 个末影箱购买，因此它是便携入口，不会额外创造一套免费仓储空间。

### 阅读

把 `.txt` 小说放进 `mcphone/books/`，即可在手机上阅读。阅读器支持章节识别、阅读进度、
键盘翻页和全屏模式。安装 Patchouli、GuideME 或沉浸工程后，同一个 App 还会把整合包里
的教程手册集中到可搜索书架。玩家不必随身携带一摞手册，也不必为了看小说切出游戏。

### 应用商店与设置

应用商店负责安装、卸载和记录购买状态，也能发现其他模组或脚本提供的 App。设置里可
调整壁纸、字体颜色、界面大小、设备名、副手 HUD、每个 App 的快捷键和 App 管理器。

### 联动 App

安装兼容的对应模组后，还会增加：

- **传送石：** 在手机里打开 Waystones 的传送点选择界面。
- **浏览器：** 通过 MCEF 在 Minecraft 中打开真正的网页浏览器。
- **任务书：** 一键打开整合包的 FTB Quests。
- **终端：** 打开 AE2、Refined Storage 或 Tom's Simple Storage 终端，并可在手机内
  放置专用无线终端。
- **阅读：** 汇总 Patchouli、GuideME 与沉浸工程的教程书。
- **唱片仓联动：** 接收 NetMusic 等模组生成的唱片。

如果缺少对应联动模组，或在当前 Minecraft 版本上没有兼容构建，相关 App 会直接隐藏，
不会留下点了就报错的空图标。

## 为整合包与附属作者准备

MCphone 使用 Java SPI 开放 App 系统。其他模组只要实现 `IPhoneApp` 并完成注册，就不需要
让 MCphone 提前知道这个附属。附属可以提供页面、存储、通知、服务端通信和自己的图标。

新版本移植保留上游附属 API，兼容的现有附属可以继续按同一套概念开发。接口文档见
[Add-on API](https://github.com/november521/mcphone/blob/main/docs/addon-api.md)。

## 支持版本

| Minecraft | 加载器 | 说明 |
|---|---|---|
| 26.3 | NeoForge、Fabric | 当前新版本构建 |
| 26.2 | NeoForge、Fabric | 当前新版本构建 |

Fabric 需要安装 Fabric API。这些构建使用 Java 25。客户端和服务端都必须安装 MCphone，
并且两边使用同一个版本。

手机本体不要求任何联动模组。联动 App 能否出现，取决于对应模组是否具备当前 Minecraft
版本和加载器的兼容构建。原始支持版本请前往
[上游 MCphone 项目](https://www.curseforge.com/minecraft/mc-mods/mcphone)。

## 安装

1. 更换模组前先备份存档。
2. 安装 Minecraft 26.2 或 26.3，以及对应的 NeoForge 或 Fabric。
3. Fabric 玩家同时安装兼容的 Fabric API。
4. 下载文件名中的 Minecraft 版本和加载器都与实例一致的 MCphone。
5. 客户端与服务端的 `mods` 文件夹放入同一个版本。
6. 启动游戏。安装了对应联动模组后，相关 App 才会出现。

不要把不同 Minecraft 版本或不同加载器的 jar 混用。

## 为什么值得下载

MCphone 把拍照、媒体、聊天、笔记、传送、存储、阅读、任务和服务器 App 集中到一台可
定制的设备里。它提供便利，但不会破坏游戏进度：部分 App 需要支付对应的游戏内物品，
多人功能可由服主控制，只有整合包具备相应依赖时才会出现联动 App。

对多人服务器和大型整合包来说，它提供了一层既像 Minecraft 原生功能、又属于玩家自己的
社交与工具入口。每位玩家都能保留自己的壁纸、设备名、App 布局和聊天记录。

## 来源、许可与致谢

- 原作与设计：**november521**
- 上游源码：<https://github.com/november521/mcphone>
- 新版本移植源码：<https://github.com/Beijingdragon233/mcphone_fabric>
- 许可：**MIT**

MCphone - More Versions 是非官方兼容移植。移植相关问题请反馈到移植仓库，同时也请
支持原作项目。
