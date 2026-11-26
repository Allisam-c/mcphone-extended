# Minecraft 26.1.2 Fabric 移植记录

## 目标

| 项目 | 配置 |
|---|---|
| Minecraft | 26.1.2 |
| Fabric Loader | 0.19.5 |
| Fabric API | 0.155.3+26.1.2 |
| Fabric Loom | 1.19.3 |
| Gradle | 9.8.2 |
| Java | 25 |

## 实现

- 新建 `platforms/26.1.2-fabric`，保留 Fabric 网络、注册、客户端输入、配置同步和资源元数据接缝。
- 26.1.2 的原版 API 差异使用平台层显式适配，包括 `InteractionResult`、`Gui.extractRenderState`、`Minecraft#getMainRenderTarget`、`Minecraft#getToastManager` 和 26.1.2 的世界 HUD 状态。
- `26.1.2-common+` 版本层承载 NetMusic、MCEF 与 Patchouli 的显式不可用门面，供 26.1.2 Fabric 与 26.2/26.3 双端复用；26.1.2 NeoForge 继续使用自己的实际联动源码。
- 真实 Fabric 联动为 Waystones、Balm、Shogi 与 Tom's Simple Storage。Curios、AE2、GuideME、AE2WTLib 只保留编译探测，没有伪造成可运行联动。
- 相机取景框由平台 Mixin 注入 26.1.2 `Gui.extractRenderState` 尾部，原版 HUD 隐藏时仍会绘制。

## 验证

```powershell
cd platforms/26.1.2-fabric
.\gradlew.bat build
```

本地结果：`BUILD SUCCESSFUL`，179 个任务，140 份断言测试全部通过。结构闸覆盖 SPI、dist 隔离、共享层、平台双胞胎、接缝文档、Mixin 包、数据包目录名和私有 Rhino 边界。

产物：`build/libs/mcphone-1.12.0.jar`，4,924,389 字节，SHA-256：

```text
3248EEAC1F42BA322DA12609AB8B19E62D290083BFFF61CF2904340975551D91
```

运行冒烟：

- 专用服务器加载 51 个模组，MCphone 完成初始化并记录“手机已就绪”，随后按预期停在未接受 EULA。
- 客户端加载 59 个模组，完成 OpenGL、字体、贴图与音效初始化；13 个 App 注册成功，浏览器与任务书按缺少对应 Fabric 构件显式跳过，Tom's Simple Storage 终端接通。
- 日志没有 MCphone 异常或崩溃报告。客户端未进入世界做逐页 UI、相机、聊天和附属 API 验收。
