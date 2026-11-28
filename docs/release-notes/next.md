# MCphone v1.12.1+1.0.0
<!-- date: 2026-11-28 -->

## 简体中文

### 新增

- 家园 App：直接读取服务器真实家园列表（/listhomes 实时同步），点击传送、行内删除、上限显示全部以服务器为准，不再本地记账。
- 家园 App：开局自动获得一部预装家园 App 的手机；新建家园带乐观更新，名字输入即入列表，零等待。
- 家园 App 联动页：支持服务器端 Mod 判定——手机端 Mod 是否"已装"由服务器命令树确认，没装的服务器端联动物不再出现在列表中。

### 改进

- 家园输入框：闪烁竖线光标，空态也始终可见；退格支持键值/双扫描码/字符事件三条路径；输入框旁提供"设置/取消"。
- 家园操作不再弹出"已执行命令"提示，结果以服务器聊天栏为准；同步用的 /listhomes 回包在聊天栏自动隐藏，不刷屏。
- 家园图标重绘为天蓝色圆角底色的小房子，与其他 App 图标同规格（20×20 满格）。

### 修复

- 修复家园图标显示为粉黑棋盘格的问题：手写 PNG 生成器缺少 12 字节导致数据不完整，现已重建并通过解码回读校验。
- 修复新建家园后列表延迟不刷新：同步调度器改挂平台客户端 tick 链，不再依赖页面 tick 送达。

## English

### Added

- Home App: reads the server's real home list (/listhomes sync). Teleport, delete, and the capacity display all follow the server — no more local bookkeeping.
- Home App: a pre-installed phone with the Home App is given at world start; creating a home updates the list optimistically (instant, zero wait).
- Home App integrations: server-side mod detection — a server-side companion mod shows as installed only when the server's command tree confirms it.

### Improved

- Home input box: blinking caret, visible even when empty; backspace works through key code, scan codes, and character events; explicit Set/Cancel buttons.
- No more "command sent" toasts; results come from the server chat. The /listhomes reply is hidden from chat automatically so it does not spam.
- Rebuilt the Home icon with a sky-blue rounded background, matching the other app icons (20×20 full tile).

### Fixed

- Fixed the Home icon rendering as a pink/black checkerboard: the hand-rolled PNG generator omitted 12 bytes, so the texture data was incomplete. The asset was rebuilt and verified by decoding it back.
- Fixed the home list not refreshing after creation: the sync scheduler now rides the platform client tick chain instead of the page tick.
