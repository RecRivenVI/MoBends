# MoBends 旧 port 当前五 Target 映射与反向新增静态审计

日期：2026-09-10。状态：进行中；本文件是 B 视角（当前五 Target 正向接线与反向新增）记录，不是构建、运行、视觉或行为等价 PASS。

## 边界、基线与判定

- 审计对象是 `D:/Workspaces/Repositories/GitHub/RecRivenVI/MoBends` 旧 port，不是 `Local/MoBends` Second Implementation。
- 当前 Git 基线为分支 `stonecutter`、HEAD `01d5cff03a188ee3bf0366d4e2c17ed0ff1e85a3`（Update）。五 Target 源码处于未提交工作树；不能把本文件观察到的源码归因于 HEAD 树。
- 上游能力对照固定使用 `D:/Workspaces/References/MoBends/Upstream/0508765b3a9066d0eb8124fbe771a0ca4600f637`（U，Initial port 的父提交/v1.2.2），不以最新上游替代 U。
- 本轮仅静态读取；没有运行 Gradle、单元测试、客户端/服务端、GUI、网络请求或产品进程，没有 stage/commit/push。
- 源码中的 `// ? if ...` 条件块要结合首行和 `/* ... */` 包裹判断：目标不匹配的内容是 comment-only，不是可达实现。文件存在、类可读、测试文件存在都不等于该 Target 编译/运行时接入。
- 状态词：`静态接线已确认` 仅表示从入口/配置/源码调用链可达；`定位但接入未确认` 表示找到类或资源但没有配置/入口边；`行为待动态验证` 表示本文件不替代真实运行。

## MB-M-001：五 Target 注册与版本边界（静态接线已确认）

根 settings 在 `settings.gradle.kts:23-30` 设置项目名、`:version` 映射到 `versions/`，并注册五个原生目标；`settings.gradle.kts:32-37` 在每个目标加载自身 `target.properties`。实际五个目标不是 Fabric 代表其他 loader：

| Target | 声明证据 | Loader/API 与 Java | 源码接线范围 |
|---|---|---|---|
| `1.20.1-forge` | `versions/1.20.1-forge/target.properties:1-8` | Forge `1.20.1-47.4.23`，Java 17 | Forge 入口、Forge 事件、Forge Mixin，`MoBendsForge.java:1-128` active |
| `1.21.1-neoforge` | `versions/1.21.1-neoforge/target.properties:1-8` | NeoForge `21.1.248`，Java 21 | NeoForge 1.21 入口/事件/Mixin 分支，`neoforge/main/MoBends.java:1-105` active |
| `26.1.2-neoforge` | `versions/26.1.2-neoforge/target.properties:1-8` | NeoForge `26.1.2.97`，Java 25 | NeoForge 26.x 入口/RenderState/Mixin 分支，`neoforge/main/MoBends.java:105-215` active |
| `26.2-fabric` | `versions/26.2-fabric/target.properties:1-10` | Fabric Loader `0.19.3` + Fabric API `0.156.0+26.2`，Java 25 | Fabric main/client 入口、Fabric client events、Fabric Mixin，`fabric/main/MoBendsClient.java:1-55` active |
| `26.2-neoforge` | `versions/26.2-neoforge/target.properties:1-8` | NeoForge `26.2.0.66`，Java 25 | NeoForge 26.x 同 `26.1.2-neoforge` 的 modern 分支；目标依赖和条件仍单独声明 |

每个目标的 `src/main`/`src/test`/`build.gradle.kts`/`target.properties` 均实际存在。物理源树还包含其他 loader 的 comment-only 分支；不能按目录存在就把其当作该目标实现。

## MB-M-002：构建、资源过滤与 artifact 处理（静态接线已确认；artifact 内容待验证）

### Forge 1.20.1

- `versions/1.20.1-forge/build.gradle.kts:159-178` 使用 `legacyForge`，将 `sourceSets.main` 注册为 `mobends`，只配置 `client` 与 `clientMultiplayer`；`build.gradle.kts:186-193` 禁用 `runServer`。
- `build.gradle.kts:181-184` 接入 `mobends-forge.mixins.json` 与 refmap；`build.gradle.kts:221-255` 展开 Forge metadata、ServiceLoader、版本属性和 Mixin JSON，并排除 Fabric/NeoForge metadata/Mixin。`build.gradle.kts:257-262` 把 Mixin 配置写入 JAR manifest。
- `build.gradle.kts:264-272` 的 `buildAndCollect` 依赖 `reobfJar`，只复制主 JAR和 sources JAR到根 `build/libs/<mod.version>`；没有安装语义。

### NeoForge 1.21.1 / 26.1.2 / 26.2

- `versions/1.21.1-neoforge/build.gradle.kts:164-183`（三份 NeoForge 构建脚本的相同结构）注册 `mobends` source set 和 `client`/`clientMultiplayer`；`:185-192` 禁用 `runServer`。
- `build.gradle.kts:220-251` 按 `project.name == "1.21.1-neoforge"` 选择 legacy 与 modern `pack.mcmeta` 字段和 Mixin 列表：1.21 使用旧 `AgeableListModel`/`SpiderModel`/`RenderLayer`/泳姿及 armor 分支；26.x 使用 `armor.ModelPartAccessor`、`AvatarRendererMixin`、`ArrowRendererMixin`、`ItemInHandLayerMixin`、`LivingEntityRendererMixin`、`ModelFeatureRendererMixin`、`RenderTypeAccessor`。
- `build.gradle.kts:252-297` 展开 NeoForge metadata、ServiceLoader、版本属性和 `mobends-neoforge.mixins.json`，并按目标选择 `logoFile`/`iconFile`、`icon.png`/`assets/mobends/textures/logo512.png`、legacy refmap；`:306-314` 只复制 JAR/sources JAR到收集目录。

### Fabric 26.2

- `versions/26.2-fabric/build.gradle.kts:125-138` 使用 Minecraft、Fabric Loader/API、Mod Menu（compileOnly + testRuntimeOnly）依赖；`build.gradle.kts:140-180` 仅保留 client 与自定义 `clientMultiplayer` run，`:182-191` 禁用 server/RenderDoc。
- `build.gradle.kts:219-254` 只展开 `fabric.mod.json`、`pack.mcmeta`、ServiceLoader、版本属性和 Fabric Mixin JSON，并排除 Forge/NeoForge 文件；`:263-267` 只收集 JAR/sources JAR。
- `fabric.mod.json:18-33` 声明 Fabric `main`、`client`、`modmenu` 三个入口；Mod Menu 是 GUI 集成，不是 Addon API 或 wearable API。

## MB-M-003：五个目标实际 Loader 入口到 Core/事件（静态接线已确认；行为待动态验证）

### 1.20.1 Forge

- `versions/1.20.1-forge/src/main/java/io/github/recrivenvi/mobends/forge/MoBendsForge.java:36-65` 的 `@Mod` 入口注册 Forge network、common setup、客户端 config extension point 与 Forge event bus；`:78-101` 在客户端设置 `PlatformServices`、skin provider、`ForgeCore`、`Core.onClientSetup()`、`DefaultAddon`、配置应用和 `RenderingEventHandler`。
- `ForgeCore.java:16-57` 初始化 `CoreClientConfig`、`PackManager`，并把配置应用到 `EntityBenderRegistry`。
- `forge/client/event/RenderingEventHandler.java:35-196` active；`:40-80` 接收 client/render tick 更新 `DataUpdateHandler`、`EntityDatabase`、`Addons`，`:85-100` first-person hand，`:103-196` living render 前后 mutation/context/pose 隔离。

### 1.21.1 NeoForge

- `versions/1.21.1-neoforge/src/main/java/io/github/recrivenvi/mobends/neoforge/main/MoBends.java:33-105` active branch：构造函数注册 NeoForge config extension、payload/log-in listener；`:69-102` 设置平台、skin provider、`CoreClient`、默认 Addon、配置和 `RenderingEventHandler`。
- `neoforge/CoreClient.java:17-62` active；配置初始化、`PackManager` 初始化和 bender 配置应用在 `onClientSetup`/`applyConfigurationToEntityBenders`。
- `neoforge/client/event/RenderingEventHandler.java:36-193` active；使用 1.21 `ClientTickEvent`/`RenderFrameEvent`，并沿用主模型 render phase 与 mutation/context 链。

### 26.1.2 NeoForge / 26.2 NeoForge

- 同一 `MoBends.java` 的 modern branch `:105-215` 仅由 `target_26_1_2_neoforge || target_26_2_neoforge` 条件激活：`:146-163` 注册 `RenderingEventHandler::registerRenderStateModifiers` 与 payload listener，`:180-201` 完成平台/Core/Addon/配置/事件接线，`:205-210` 注册 resource reload listener 刷新 `ClientRuntime`。
- 同一 `CoreClient.java:63-120` modern branch 增加 `ModConfig.syncFromConfiguration()`；不得把 1.21 branch 的行号/行为代入 26.x。
- 同一 `RenderingEventHandler.java:193-376` modern branch 改用 RenderState extraction/modifier：`:232-249` 注册 entity/arrow render-state modifier，`:250-280` 更新 ticks，`:286-310` first-person，`:311-376` living render mutation、trail/context 和 after-render。

### 26.2 Fabric

- `fabric/main/MoBends.java:11-21` 只做 `FabricNetwork.registerCommon()` 和 common setup；`fabric/main/MoBendsClient.java:24-55` 设置 `FabricPlatformServices`、Core、默认 Addon、配置、Fabric network、resource reload listener、Fabric rendering registration。
- `fabric/CoreClient.java:15-58` 初始化配置/`ModConfig`/`PackManager`并应用 bender 配置。
- `fabric/client/event/RenderingEventHandler.java:23-90` 通过 `ClientTickEvents.END_CLIENT_TICK` 与 `LevelRenderEvents.START_MAIN` 更新时间；实际实体 mutation/render 由 Fabric Mixin 负责，不可把该类当作 NeoForge/Forge living render event 等价物。
- `fabric/client/MoBendsModMenu.java:9-15` 实现 `ModMenuApi`，仅返回 `MoBendsMenuScreen`。

## MB-M-004：平台服务、版本 Adapter 与 Addon/wearable 边界（静态接线已确认）

- `api/platform/IPlatformServices.java:18-151` 是当前 port 自有的抽象边界，覆盖环境、资源、包裹实体/玩家/pose/buffer/item、render layer/tesselator、shader 等；`api/platform/PlatformServices.java:9-60` 以显式 `set` + `ServiceLoader` 懒加载实现。
- ServiceLoader 资源 `versions/<target>/src/main/resources/META-INF/services/io.github.recrivenvi.mobends.api.platform.IPlatformServices:1` 只有 `${platform_service}` 占位；各构建脚本通过 `processResources` 展开为当前目标实现，源树原文不能直接当作可运行类名。
- Forge active 实现为 `forge/platform/ForgePlatformServices.java:50-211` + `ForgeVersionAdapter.java:16-67`；1.21 NeoForge active 实现为 `neoforge/platform/NeoForgePlatformServices.java:50-211` + `NeoForgeVersionAdapter.java:18-67`；26.x NeoForge active branch 为 `NeoForgePlatformServices.java:257-416` + `NeoForgeVersionAdapter.java:69-136`；Fabric 为 `fabric/platform/FabricPlatformServices.java:48-207` + `FabricVersionAdapter.java:17-68`。
- wearable/armor provider 的实际 loader 接线也已分开：Forge `ForgePlatformServices.java:55-68`、NeoForge 1.21 `NeoForgePlatformServices.java:55-68`、NeoForge 26.x `:263-273`、Fabric `FabricPlatformServices.java:53-64` 分别设置各自 `ArmorModelProvider`、`IArmorTextureProvider`、`IArmorLayerProvider`、`IArmorColorProvider` 与 `IArmorHelper`；不能把 armor provider 的静态 API 存在误报成 Mod Menu 或通用 Addon 接入。
- `platform/VersionAdapter.java:1-148` 将 `Identifier`（26.x）与 `ResourceLocation`（1.x）分成不同 active branch，证明这是版本/API 适配，不是 Fabric 代替其他目标。
- Addon 扩展边界是 `core/addon/IAddon.java`/`AddonHelper.java`/`AddonAnimationRegistry.java:15-90`；Mod Menu 只有 Fabric 的 `MoBendsModMenu`；wearable/armor 则是 `api/armor/*`、`ArmorModelProviderHolder.java:7-16` 与 `standard/client/model/armor/*`。三者不能合并为同一“兼容层”。

## MB-M-005：Mixin 配置、active 分支与实际渲染钩子（静态接线已确认；行为待动态验证）

所有目标的原始资源树都保留三份 Mixin JSON，但构建脚本过滤为单一 loader；下表只列 processResources 选中的列表，不列 comment-only 类：

| Target | 实际打包的配置与 client mixins | active Java 分支 |
|---|---|---|
| 1.20.1 Forge | `mobends-forge.mixins.json:9-13`：`AgeableListModelMixin`、`SpiderModelMixin`、`RenderLayerMixin`、`PlayerRendererSwimMixin` | 各类的 `target_1_20_1_forge` 分支；桥接为 `forge/mixin/MixinBridge.java:1-233` |
| 1.21.1 NeoForge | `mobends-neoforge.mixins.json` 经 `build.gradle.kts:228-240` 展开：Ageable/Spider/RenderLayer/PlayerSwim、`LivingEntityRendererAccessor`、armor trim/elytra/model-part/humanoid | `AgeableListModelMixin.java:59-105`、`SpiderModelMixin.java:48-89`、`RenderLayerMixin.java:69-130`、`LivingEntityRendererAccessor.java:28-50`及 armor 分支 |
| 26.1.2 NeoForge | `build.gradle.kts:241-250` modern 列表：armor.ModelPart、Avatar、Arrow、ItemInHandLayer、LivingEntityRenderer、ModelFeatureRenderer、RenderTypeAccessor | `ArrowRendererMixin.java:48-79`、`LivingEntityRendererMixin.java:184-269`、`ModelFeatureRendererMixin.java:48-102` 等 modern 分支 |
| 26.2 NeoForge | 同上 modern 列表，但 `ModelFeatureRendererMixin.java:103-149` 为 26.2 分支 | 26.2-specific modern branch；入口另接 `registerRenderStateModifiers` |
| 26.2 Fabric | `mobends-fabric.mixins.json:8-16`：armor.ModelPart、Avatar、Arrow、ItemInHandLayer、ItemInHandRenderer、LivingEntityRenderer、ModelFeatureRenderer、RenderTypeAccessor | `AvatarRendererMixin.java:1-105`、`ArrowRendererMixin.java:1-47`、`ItemInHandRendererMixin.java:1-28`、`LivingEntityRendererMixin.java:1-183`、`ModelFeatureRendererMixin.java:1-47` |

共同注意事项：

- `AvatarRendererMixin.java:32-47` redirect swimming root selection，`:49-105` first-person arm；`ItemInHandLayerMixin.java:29-66`为跨 modern loader 的 hand/layer 接线；`RenderTypeAccessor.java:1-17`是现代 RenderType invoker。
- `ArrowRendererMixin.java:1-47` Fabric 先在 `extractRenderState` 保存箭实体并在 `submit` 提交拖尾；NeoForge modern 分支 `:48-79` 使用其 RenderState 存储。这个差异是版本/loader 适配，不能由 Fabric 证成 NeoForge 行为等价。
- `LivingEntityRendererAccessor.java:1-27` 的 modern accessor 分支在源文件中 active，但 26.1.2/26.2 NeoForge 和 Fabric 的实际 JSON 列表均未列它；静态搜索未发现其他 Mixin 配置引用，当前标记为 **定位但接入未确认/疑似不可达**（MB-M-005-R1）。1.21 branch `:28-50` 则被 NeoForge legacy JSON 明确列出。
- Java Mixin 类存在不等于 Mixin 已打包；最终须以 processResources/JAR 内容或真实启动确认，本轮不宣称。

## MB-M-006：入口到资源/动画/本地包刷新链（静态对应已确认；资源打包待动态验证）

- `core/client/ClientRuntime.java:14-31` 是 loader-neutral refresh 汇聚点：重载 `CoreClientConfig`/`ModConfig`，清 `AnimationLoader`/`GsonResources`/`PackDataProvider` cache，reload `PackManager`，refresh entity benders，最后 `Addons.onRefresh()`。
- Fabric 入口 `MoBendsClient.java:37-48`、NeoForge modern 入口 `MoBends.java:205-210` 都注册 `ModStatics.getResource("animation_resources")` reload listener；`ModStatics.java:1-21` modern branch通过 `IdentifierFactory`形成 `mobends:<path>`，1.x branch `:22-42`通过 `ResourceLocationFactory`。
- 五目标 reload 接线不能合并推断：`1.20.1-forge` 只有 `MoBendsForge.java:124-125` 的 public `refreshSystems()` 转发，active Forge 入口/事件未找到资源 reload 注册或其它 active consumer；`1.21.1-neoforge` 同样只有 `MoBends.java:100-102` wrapper，`addClientReloadListeners` 是其 comment-only modern branch；`26.1.2-neoforge`、`26.2-neoforge` 有 active `MoBends.java:153` 注册和 `:207-210` listener；`26.2-fabric` 有 active `MoBendsClient.java:36-48` Fabric listener。静态搜索未把 inactive sibling 的 wrapper/listener 当作目标接线。
- `WolfController.java:24-25`/`:133-134`读取 `mobends:bends/animators/wolf.json`；该文件实际存在于 `assets/mobends/bends/animators/wolf.json`，其 animationKey 去重后引用 6 个 `assets/mobends/bends/animations/*.bendsanim`（idle、walking、sitting_down、sitting、standing_up、breathing），六个文件均存在。`LivingEntityRendererMixin.java:128`/`:236`引用 `textures/entity/wolf_misc.png`，该文件存在。`BendsCapeRenderer.java:25` 的 modern `CAPE_TEXTURE` 常量和 `:355` 的 legacy 常量当前搜索均只有声明；实际 modern cape 提交在 `layers/LayerCustomCape.java:37-70` 使用 `state.skin.cape().texturePath()`，legacy 分支在 `LayerCustomCape.java:112-149`/`BendsCapeRenderer.java:403-409` 使用玩家皮肤 provider。不能把 `textures/cape.png` 常量声明当作实际加载证据。
- 五个 Target 的 `src/main/resources` 当前 32 条路径、内容 hash 静态一致；实际 loader 差异来自各自 `processResources` 的 expand/exclude，不应把 raw source tree 当最终 artifact。
- 与 U 的资源反向核对显示：wolf/harvest/bends animation、`logo512.png`、`stand_block.png`、`wolf_misc.png`、`cape.png` 能在当前资源树找到对应迁移；语言资源由 U 的 `.lang` 改为当前 8 份 JSON，`LocalizationCompletenessTest.java:15-37` 以 `en_us.json` 为基准检查 7 个译文键集合。U 的 `assets/mobends/textures/gui/{addons_window,bends,buttons,default_pack_thumbnail,fonts/bold,icons,node_editor,pack_window,title}.png` 在当前树没有同名路径；当前 GUI 源码使用原版 `Button`/`EditBox`/options list（例如 `MoBendsMenuScreen.java:12-19`、`MoBendsOptionsScreen.java:7-15`），这是资源/GUI替换而非“图片仍然可达”。

## MB-M-007：配置/GUI/Addon 的当前接线与测试保护（静态；测试未执行）

- `core/configuration/CoreClientConfig.java:21-219` 持久化 `mobends-client.json`、实体开关、trail/spin、advanced item/armor/entity patterns；`core/client/gui/MoBendsMenuScreen.java:19-249` 将这些开关接入六个 GUI section，并在 `:98-105` 受 server BendsPack policy 控制本地包按钮。
- 同名 `LocalPacksScreens.java` 已按五目标逐份核对，不能合并成一个“当前实现”：`1.20.1-forge:9-16` 返回 `LocalPacksScreen120`，`1.21.1-neoforge:9-16` 返回 `LocalPacksScreen121`，`26.1.2-neoforge`/`26.2-fabric`/`26.2-neoforge:9-16` 返回 `LocalPacksScreen26`；相邻版本返回均为 comment-only。
- 1.20 Forge 的 config screen extension 在 `forge/MoBendsForge.java:53-63`；1.21/26 NeoForge 在 `neoforge/main/MoBends.java:47-51`/`:155-159`；Fabric 只由 Mod Menu entrypoint 提供 `MoBendsMenuScreen`。这三类入口不是同一兼容承诺。
- 每个目标物理 `src/test/java` 都有 37 个文件，但 comment-only 条件使 active test 数不同：1.20 Forge 27、1.21 NeoForge 27、26.1.2 NeoForge 34、26.2 Fabric 36、26.2 NeoForge 35。静态抽样显示共同覆盖 Addon API、GUI布局/本地包、配置、NetworkConfiguration、pack parser、数学/flux、player/mutator model；现代目标额外可达 `EntityBenderRegistryTest`、`TrailSubmitterTest`、`WolfControllerCompatibilityTest`、armor/player compatibility tests，Fabric 另有 `MoBendsModMenuTest`。这些只是断言文件的静态存在/active 状态，不是执行 PASS。
- 例如 legacy 两目标中 `EntityBenderRegistryTest.java`、`CreatureMutatorModelTest.java` 等 modern 条件文件是 comment-only；modern 两 NeoForge/Fabric 又分别排除 legacy `LegacyGlHelperTest` 或 Fabric-only ModMenu。跨目标 test 文件计数不能当 parity 证据。
- modern `EntityBenderRegistryTest.java:20-58` 只静态断言 Zombie→Husk 继承、Drowned 不被继承、Squid 不扩展到 GlowSquid；`TrailSubmitterTest.java:13-27` 只断言几何提交层；`PlayerRenderPoseCompatibilityTest.java:8-19` 只断言 cape crouch 数值；均不能替代真实 renderer/Mixin/cape 运行。

## MB-M-008：反向新增/继承/版本适配分类（静态来源判断）

### 可从 U 直接追到的继承/迁移能力

U 的 `core/addon/*`、`core/bender/*`、`core/animation/*`、`core/data/*`、`core/kumo/*`、`core/pack/*`、`core/math/*`、`core/flux/*` 与 `standard/*` 提供当前默认动画/bender/pack 基础的来源；当前树以 `io.github.recrivenvi.mobends` namespace、`lib/*`/`core/*` 新分层、Minecraft API 适配后的文件承载。它们可称“来源/能力继承或迁移”，不能仅因相对路径变化称主动新功能。

`standard/DefaultAddon.java:23-68` modern 分支仍把 Player、Zombie、Skeleton、ZombifiedPiglin、Spider、Squid、Wolf 注册进当前 bender registry；同时显式排除 Drowned、GlowSquid 和 baby Squid（`:30`、`:45-47`）。这证明当前七类注册链静态存在，但不证明渲染行为等价。

### 当前 port 的版本适配/主动新增

| 当前区域 | 分类 | 证据与边界 |
|---|---|---|
| `api/*` + `platform/*` + loader `*PlatformServices`/`*VersionAdapter` | 版本/loader 适配层；不在 U 的同名顶层能力中 | `api/platform/IPlatformServices.java:18-151`、`platform/VersionAdapter.java:1-148`、MB-M-004；服务实现各自只在对应 active 条件运行 |
| `forge/*`、`neoforge/*`、`fabric/*`、`mixin/*` | 当前多 loader 接线与 Minecraft API 迁移 | 入口、event、network、render-state、Mixin JSON 见 MB-M-003/005；不能把 inactive sibling 当实现 |
| `core/client/gui/*Screen120/121/26`、`LocalPacksScreens` | GUI 版本适配 | `LocalPacksScreen120.java:1-261`、`LocalPacksScreen121.java:1-275`、`LocalPacksScreen26.java:1-440` 各自条件分支；三者不是同一实现 |
| `ServerNetworkConfig`/各 loader payload/`NetworkConfigMessage` | 当前 port 的 loader transport + 本地服务器策略接线 | `core/network/ServerNetworkConfig.java:13-70`、各 loader transport；与 U 的旧 WebAPI/消息结构不能直接宣称等价（联网行为由主复核另审） |
| `api/armor/*`、`IArmorLayerProvider`、`IArmorHelper` | armor/wearable 版本适配与扩展边界，不是 Mod Menu | `api/armor/IArmorModelProvider.java:1-77`、`IArmorTextureProvider.java:1-123`、`api/rendering/IArmorLayerProvider.java:1-47`；明确把 1.20 单层与 1.21+ 多层差异收口在 provider |
| 26.x RenderState/Mixin/arrow trail/ModMenu | 现代目标主动新增或现代 API 适配；来源未在 U 同名路径中直接存在 | 26.1.2/26.2 NeoForge active `neoforge/client/render/MoBendsRenderState.java:1-187`；26.2 Fabric active `fabric/client/render/MoBendsRenderState.java:1-201`；现代 Mixin 分支及 `fabric.mod.json:18-27`；需后续逐类来源追踪 |

### U 能力未在当前有效树中找到或来源未知

- U 的 `core/WebAPI.java`、`core/connection/*`、`core/supporters/*` 及旧 public remote accessory/animation-editor 访问路径，在当前有效源码中未找到同名实现；当前 `NetworkConfiguration`/loader payload 注释明确指向 active Minecraft connection，不能把旧远程能力默认为保留。
- `lib/*` 是当前分层中大量数学/动画基础的承载位置；其来源可与 U `core/math`/`core/animation/keyframe` 逐文件追踪，但本轮尚未完成逐文件 hash/语义归因，暂标“迁移候选，来源细分未完成”。
- 当前 modern `LivingEntityRendererAccessor` 虽有 active Java 分支，Mixin JSON 接入缺口见 MB-M-005-R1，不能算已生效兼容层。

反向按子系统的证据化对照：

| 子系统 | U 证据 | 当前有效树 | 分类/限制 |
|---|---|---|---|
| Config | `U/core/configuration/CoreClientConfig.java:9-60` 只有 AppliedPacks/Animated 的旧配置对象 | `core/configuration/CoreClientConfig.java:21-219` 为 `mobends-client.json`、patterns、trail/spin/实体/armor 规则 | 继承 applied packs/entity enable 概念 + 当前 port 的 JSON/规则扩展；不能称 U 行为等价 |
| GUI | `U/core/client/gui/GuiBendsMenu.java:23-212` 是旧纹理 GUI，`:140-142` 还调用 `WebAPI` editor URL | `core/client/gui/MoBendsMenuScreen.java:19-249`、`MoBendsOptionsScreen.java:7-149` 使用原版 options/widgets；无当前 `WebAPI`/editor URL 引用 | GUI 版本适配/主动替换；旧远程 editor 入口未保留 |
| Network policy | `U/core/network/NetworkConfiguration.java:9-60` 使用 SharedProperty/SharedConfig，配合 `MessageConfigRequest/Response` | 当前 `core/network/NetworkConfiguration.java:3-39` + `ServerNetworkConfig.java:13-70` + loader payload/message | policy 三字段语义候选继承，transport/config 实现是 port/loader 适配；行为由主复核负责 |
| Remote service/supporter | `U/core/WebAPI.java:9-64`、`U/core/connection/ConnectionManager.java:13-101` 有 HTTP URL、join/ping/player settings | 当前 active Java 搜索未找到 `WebAPI`、`ConnectionManager`、`PlayerSettingsDownloader`、`SupporterContent` | U 能力当前未找到；不可把 absence 解释为兼容保留 |
| Resources | U 有 `assets/mobends/textures/gui/*`、`.lang`、`mcmod.info`/`accesstransformer.cfg` | 当前 loader metadata/Mixin JSON、JSON lang、原版 widget GUI | 资源格式和 loader metadata 已主动迁移；缺失旧 GUI 图片不等于功能已验证删除 |

## MB-M-009：资源/metadata 风险与剩余研究

1. **已确认的静态缺口（需主任务决定是否升级为 review 项）**：源资源没有 `icon.png`，但 `META-INF/mods.toml:13` 固定写 `logoFile = "icon.png"`；NeoForge legacy 1.21 由 `build.gradle.kts:272-276` 也选择 `icon.png`。同一资源树只有 `assets/mobends/textures/logo512.png`。26.1.2/26.2 NeoForge 与 Fabric 选择 logo512 路径；最终 artifact 是否另行生成/重命名尚未验证，故不能宣称 metadata 图标有效。
2. **已确认的接线，未确认的打包**：ServiceLoader、Mixin JSON、`mobends_version.properties` 均为占位并由 processResources 展开；当前只读 source 不能证明展开后 JSON/TOML 合法或 JAR 中没有 inactive 文件。
3. **Forge 资源作用未确认**：`mobends-forge.accesswidener` 在 Forge source/resource 与现存 `build/resources/main` 中存在；Forge `build.gradle.kts:249-254` 没有排除它，但全仓静态引用只见 `legacyForge.validateAccessTransformers=true`（`:159-162`），未见 Forge access-widener 注册。NeoForge/Fabric 构建明确在 `:291-297`/`:248-254` 排除该文件。当前只能记为“Forge artifact 中存在、实际是否被消费未知”，不能把它当作已生效 access 权限。
4. **未执行的保护语义**：没有运行任何 37 文件测试集；没有客户端验证 Mixin 命中、first-person/armor/wearable/trail、resource reload、ModMenu/config screen 或跨目标 parity。
5. **下一步**：继续逐文件追 U→current 的 API/adapter/config/network/gui/compat/tests/resources 来源；检查现代 Mixin accessor 的实际调用边；核对每 Target `LocalPacksScreens`、`ModConfig`、armor/wearable provider 的 active 分支；必要时仅做静态 artifact/source manifest 检查，仍不启动产品。

## MB-M-010：五 Target armor 真实入口、Tier1/Tier2 可达边界与 U 对照

状态：静态差异已确认；覆盖范围和真实样例行为待动态验证。本节不把任何单一 fallback 或未接入路径扩大为“所有模组盔甲损坏”。

### 每目标实际 armor 入口

| Target | active armor 入口 | 静态可达的 renderer/provider 范围 |
|---|---|---|
| `1.20.1-forge` | `standard/mutators/BipedMutator.java:644-1274` active legacy branch，`:743-772` 把 `HumanoidArmorLayer` 替换为 `LayerCustomBipedArmor`；该 Layer 在 `standard/client/renderer/entity/layers/LayerCustomBipedArmor.java:1-579` active。Forge Mixin JSON `mobends-forge.mixins.json:9-13` 没有 `HumanoidArmorLayerMixin`。 | Layer 的 `:164-178` 使用 `ArmorModelProviderHolder`，`:232-310` 用 `IArmorLayerProvider`/`IArmorTextureProvider` 与 `ArmorRenderingFacade`；vanilla/custom model 都有 facade 入口，`:317-360` 另有 rigid capture fallback。 |
| `1.21.1-neoforge` | 同一 legacy `BipedMutator.java:644-1274`/`:743-772` 与 `LayerCustomBipedArmor.java:1-579` active；另有 `mixin/armor/HumanoidArmorLayerMixin.java:1-255` active，NeoForge legacy Mixin 列表由 `build.gradle.kts:228-240` 明确包含 `armor.HumanoidArmorLayerMixin`。 | Layer 与 Mixin 都静态能创建/调用 legacy `ArmorRenderingFacade.java:298-599`；Mixin 在 `:58-160` 注入 `renderArmorPiece`，遍历 armor material layers 并在成功后 cancel vanilla。二者的实际调用顺序/是否存在重复防护需运行确认，不能简单合并为一个入口。 |
| `26.1.2-neoforge` | `BipedMutator.java:1-643` modern branch active；`swapLayer`/`deswapLayer` 为 `:89-103` no-op，不替换原版 armor layer。实际延迟路径为 `neoforge/client/render/MoBendsRenderState.java:26-188`，由 modern `ModelFeatureRendererMixin.java:48-102` 和 `LivingEntityRendererMixin.java:184-269` 接线。 | `LayerCustomBipedArmor.java:1-2` 是 comment-only legacy 分支，`HumanoidArmorLayerMixin.java:1-2` 也 comment-only且不在 modern JSON 列表；`Tier1Renderer.renderDeferred` 是默认可达 renderer。 |
| `26.2-neoforge` | 与 26.1.2 同一 modern 形态：`BipedMutator.java:1-643` no layer swap；`neoforge/client/render/MoBendsRenderState.java:26-188`；`ModelFeatureRendererMixin.java:103-149`、`LivingEntityRendererMixin.java:184-269`。 | modern NeoForge Mixin 列表只包含 `armor.ModelPartAccessor`、Avatar/Arrow/ItemInHand/LivingEntity/ModelFeature/RenderType（`build.gradle.kts:241-250`）；legacy Layer/Mixin 不是默认入口。 |
| `26.2-fabric` | `BipedMutator.java:1-643` no layer swap；`fabric/client/render/MoBendsRenderState.java:27-201` 由 `ModelFeatureRendererMixin.java:15-46`、`LivingEntityRendererMixin.java:1-183` 接线。 | Fabric Mixin JSON `mobends-fabric.mixins.json:8-16` 包含 `ModelFeatureRendererMixin`、`ItemInHandLayerMixin` 等 modern 类；`LayerCustomBipedArmor.java:1-2` 与 `HumanoidArmorLayerMixin.java:1-2` 均是 comment-only legacy 分支。 |

### 26.x 默认延迟路径：实际只直接命中 Tier1

- Fabric `MoBendsRenderState.renderMutatedModel` 的附属模型分支在 `:83-127`：ElytraModel 有专门 body attachment transform；只有 `HumanoidModel`、成功 `inferArmorSlot`、非空 armor stack 且 `!ModConfig.shouldKeepArmorAsVanilla` 时，才在 `:112-123` 调 `DEFERRED_ARMOR_RENDERER.renderDeferred`。NeoForge counterpart 的对应范围是 `neoforge/client/render/MoBendsRenderState.java:70-113`。
- `inferArmorSlot` 使用 `ModelPart.isEmpty()` 判断几何是否存在：Fabric `:163-181`、NeoForge `:150-168`；它没有根据 `ModelPart.visible` 判断。因而“完整 Humanoid model 仅通过可见性隐藏其他 slot”在静态上可能使 head/body/arms/legs 同时被判定存在，最终 `return null`；这是一个明确的覆盖边界候选，不是对所有 armor model 的失败结论。
- `ModelFeatureRendererMixin` 在 `renderMutatedModel` 返回 false 时回调原版 `model.renderToBuffer`：Fabric `:31-45`、Neo 26.1 `:82-100`、Neo 26.2 `:134-147`。因此非 Humanoid 附属模型或 `inferArmorSlot` 无匹配时，静态路径是 vanilla fallback，而不是 Tier2 自动接管。
- modern `Tier1Renderer.java:86-151` 的 `renderDeferred` 直接按已识别的 `EquipmentSlot` 输出 Tier1 几何；其 `Tier2Renderer` 成员仅在 generic facade overload `:157-225` 的 non-Humanoid fallback (`:220-225`) 中使用。`Tier2Renderer.java:32-546` 虽 active，`render(ArmorRenderContext, Model)` `:54-61` 本身还返回 false，真正的带 texture generic `render` `:88-100` 没有来自 26.x RenderState 的调用边。
- `ArmorRenderingFacade.java:1-297`（26.x active）仍实现两-tier selection，但当前源码搜索到的构造/调用者是 legacy `LayerCustomBipedArmor` 与 1.21 `HumanoidArmorLayerMixin`；这两个入口在 26.x 分别 comment-only/未进 JSON。故“Facade/Tier2 类存在”不等于“26.x 默认 armor 已接入 Tier2”。

### provider 作用边界

- 所有 loader platform constructor 仍设置 `ArmorModelProviderHolder`、`IArmorTextureProvider`、`IArmorLayerProvider`、`IArmorColorProvider`（Forge `ForgePlatformServices.java:55-68`；Neo 1.21 `NeoForgePlatformServices.java:55-68`；Neo 26.x `:263-273`；Fabric `FabricPlatformServices.java:53-64`）。
- 但 model/texture/layer providers 的消费点是 legacy Layer `LayerCustomBipedArmor.java:164-168`、`:232-251`、`:444-469`，以及 1.21 Mixin 的 NeoForge armor texture loop `HumanoidArmorLayerMixin.java:88-122`；这些不是 26.x RenderState 默认路径。26.x `ArmorRenderContext.java:154-183` 可达的是 `IArmorColorProvider`，用于颜色/染色，不等于自定义 model/texture/layer provider 已接入默认延迟渲染。

### 与 U ForgeHook → ModelBiped → ArmorWrapper 的差异

- U 的 `standard/mutators/BipedMutator.java:91-100` 直接把 `LayerBipedArmor` 换成 `LayerCustomBipedArmor`；U `standard/client/renderer/entity/layers/LayerCustomBipedArmor.java:69-81` 通过 `ForgeHooksClient.getArmorModel` 取得 suggested `ModelBiped`，再交给 `ArmorModelFactory.getArmorModel`。
- U `standard/client/model/armor/ArmorWrapper.java:19-190` 包装原始 `ModelBiped`，`:103-141` 从 `EntityDatabase` 取 `BipedEntityData`、同步 body/limb wrappers、临时替换原模型部件并调用原模型 render，`:160-186` 负责 apply/deapply；这是一条对用户 armor model 的直接包装/跟随链。
- 当前 1.x port 的 Layer/Mutator 仍保留“替换 armor layer + provider 取 custom model + facade/tier/rigid fallback”结构（`BipedMutator.java:743-772`、`LayerCustomBipedArmor.java:164-178`、`:232-310`），但不是 U 的 `ForgeHooksClient`/`ArmorWrapper` 同一实现。
- 当前 26.x 改成 RenderState/ModelFeature 延迟提交，modern BipedMutator 明确不替换原版层（`BipedMutator.java:89-103`）；这属于静态架构差异。对非 Humanoid、完整模型 slot 判定、custom provider 的能力覆盖必须用真实 armor 样例和 renderer 运行确认，当前不声称 parity 或普遍损坏。

### Armor 测试边界与剩余风险

- 26.x active `Tier1RendererTest.java:9-19` 只断言 baby armor anchor，`Tier1VertexWriterTest.java:8-23` 只断言颜色通道；未找到 `inferArmorSlot`、full-model visibility、non-Humanoid ModelFeature fallback、Tier2 default reachability、provider hook、custom armor texture/model 或 trim 的 active unit assertion。
- 1.20/1.21 的 `Tier1RendererTest` modern 条件为 comment-only；legacy Layer/Facade/Mixin armor chain 当前没有相应的 active end-to-end unit test。没有构建、客户端、GUI 或实际 modded armor 样例验证。
- 当前静态结论：1.x legacy Layer/Facade/Tier1/Tier2/provider 入口存在；26.x 默认路径直接落在 Tier1 + ModelFeature vanilla fallback，Tier2/Facade/provider model-texture-layer 不是已确认默认入口。覆盖缺口与真实行为均待动态验证。

## 已查清单

已读取并遵守 `D:/Workspaces/AGENTS.md`、本仓库 `AGENTS.md`、`documents/development/audit-port-status.md`、`README.md`、`documents/development/guide-development.md`；已静态核对 `settings.gradle.kts`、五份 `target.properties`、五份 build script、五套入口/Core/event/Mixin 条件、资源树、metadata/Mixin JSON、ServiceLoader、Addon/API/GUI/测试文件 active 条件，并参考 U 的 core/standard/network/GUI/armor 目录。本次追加再核对了五目标 armor layer/Mixin/RenderState→Tier1/Tier2/Facade/provider 引用及 U ForgeHook→ArmorWrapper 链。

## 未查清/明确延后

- 主任务负责的动画时序、泳姿/披风、联网高风险行为不在此文宣称完成；本文件只记录它们在目标入口/资源/Mixin 层的接线边界。
- 未运行构建、测试、客户端、GUI、网络或 artifact 解包；所有行为等价、Mixin 实际命中、metadata 最终打包和视觉效果均待后续验证。
