# Mo' Bends 旧 port 反例审计（C）

日期：2026-09-10。本文是独立静态反例账本，不是构建、运行、视觉或功能完整性 PASS。

## 审计边界

- 当前 port：`D:/Workspaces/Repositories/GitHub/RecRivenVI/MoBends`，以实际工作树为准；开场 HEAD 为 `01d5cff03a188ee3bf0366d4e2c17ed0ff1e85a3`，当前存在大量 native 五 Target dirty 变更，以下结论不把未提交内容误归因于 HEAD。
- 上游只读参考 U：`D:/Workspaces/References/MoBends/Upstream/0508765b3a9066d0eb8124fbe771a0ca4600f637`，即 v1.2.2 / Initial port 父基线；不以最新 upstream 替代 U。
- 本轮只读源码、资源和测试静态链；没有修改实现、资源、依赖或运行配置，没有运行构建/产品测试，没有启动游戏 GUI，没有 stage/commit/push。
- 状态词：`部分实现；静态缺陷已确认` 表示能力/链路存在但源码已证明有反例；括号中的 `上游继承` 或 `当前扩展` 单独说明来源，不把它写成移植新增。`确认缺失` 只用于能力/机制缺少且已排尽迁移、替代、注册接线的情况；`定位未接线` 表示 API/入口存在但关键接线无法成立；`动态待验` 只表示仍需真实客户端确认，不把静态推理写成运行 PASS。

## 第一批反例

### MB-R-001 — 退化 KUMO 动画通过包校验后可在播放线程死循环或异常

- **当前源码/测试符号**：
  - `versions/26.2-fabric/src/main/java/io/github/recrivenvi/mobends/core/pack/PackDataProvider.java:108-122,125-145` 的 `validatePackData` 仅把 `keyframeAnimations` 中存在的 key 视为动画存在；不检查 `KeyframeAnimation.bones`、每个 `Bone` 或 keyframe 数量。
  - `.../core/kumo/state/keyframe/StandardKeyframeNode.java:46-53` 从骨骼 keyframe 列表计算 `animationDuration`；`:79-84` 在循环播放时以 `animationDuration - 1` 为阈值和减数。
  - `.../core/kumo/state/keyframe/MovementKeyframeNode.java:43-49,98-105` 同样不检查有效帧数，并以 `animationDuration - 1` 做 `%`。
  - `versions/26.2-fabric/src/test/java/io/github/recrivenvi/mobends/core/pack/KumoPackCompatibilityTest.java:53-92` 只验证空 `bones` 的嵌入动画可反序列化/校验，没有构造 `KumoAnimatorState.update`；`:162-238` 的负例矩阵也没有退化动画形状。
- **U 行为/继承关系**：U 的 `src/main/java/goblinbob/mobends/core/pack/PackDataProvider.java:91-121` 也只校验模板图；U `.../kumo/state/keyframe/StandardKeyframeNode.java:47-55,85-102` 使用同一 duration 算法。因此这是当前仍继承的上游运行时缺口，而不是把 U 的空壳误报成 port 新缺失。
- **当前链**：本地 `.bends` JSON → `PackDataProvider.validatePackData` → `KumoAnimatorState` → `StandardKeyframeNode`/`MovementKeyframeNode`。例如：

  ```json
  {"targets":{"mobends-player":{"layers":[{"type":"KEYFRAME","nodes":[{"type":"core:standard","animationKey":"a","looping":true}]}]}},"keyframeAnimations":{"a":{"bones":{}}}}
  ```

  这个对象满足当前 `validatePackData`；因为 `bones` 为空，`KeyframeLayerState` 不会取任何帧，随后第一次带正 `deltaTime` 的循环节点更新时 `animationDuration == 0`，`progress >= -1` 永真且每次减 `-1`，线程不会返回。将 `bones` 写为 `null` 则在 `StandardKeyframeNode:48` 的 `animation.bones.values()` 直接 NPE。若改成一帧骨骼，`KeyframeLayerState.applyKeyframeAnimation` 会先在 `.../KeyframeLayerState.java:164-165` 读取不存在的 `frameB`，先得到 `IndexOutOfBoundsException`，不应把该形状笼统写成同一 while 反例。移动节点同样先受取帧边界影响；其零/一帧 duration 算法另有 `% (animationDuration - 1)` 的退化风险，且这是 float remainder（非整数除零异常），应由结构校验在运行前拒绝。
- **最小反例/影响范围**：任何可被本地选择的恶意或损坏 KUMO pack；影响所有五 Target 的共享 `core/pack` 与 `core/kumo` 链，最坏是渲染线程卡死，另一类是客户端异常退出/跳过动画。仅“不抛异常”的校验测试不能证明数值或播放安全。
- **状态**：`部分实现；静态缺陷已确认（上游继承）`。
- **原因证据/下一步**：当前验证边界止于“key 存在”，运行节点未做帧数/骨骼结构防护；应在 `validatePackData` 或节点构造前拒绝 `bones == null`、空骨骼、少于两帧及空/不完整 keyframe，随后补一个真正实例化并执行至少一次更新的恶意包测试。暂未运行动态复现。

  **同一不变量的二进制入口补充**：`versions/26.2-fabric/src/main/java/io/github/recrivenvi/mobends/lib/animation/keyframe/BinaryAnimationLoader.java:19-34,36-90` 读取 header、version、`amountOfKeyframes`、`amountOfBones`，但 `version` 未校验，两个 count 无非负/上限检查；负 count 可构造空/退化 `bones`，极大 count 可在资源加载阶段制造无界循环/内存压力，EOF 只在读到数据时以 IOException 暴露。`PackDataProvider.validatePackData:111-122` 还把 `keyframeAnimations.containsKey(animationKey)` 当作内嵌动画存在，即使 map value 是 `null`；运行时会得到 `animation == null` 的节点并静默不播放。对资源路径则仅将 `AnimationLoader.loadFromPath(key) != null` 当作存在，未对二进制结果做同一结构校验。U `src/main/java/goblinbob/mobends/core/animation/keyframe/BinaryAnimationLoader.java:21-37,39-102` 同样读 version/count 而不校验；这是 R-001 的共享动画结构缺口，不另造一个重复 ID。当前 ZIP 导入由 `versions/26.2-fabric/src/main/java/io/github/recrivenvi/mobends/core/client/gui/BendsPackFileInstaller.java:23-25,55-116` 实现：限制最多 32 个非目录 entry、累计解压最多 16 MiB、拒绝路径穿越/绝对路径/重复文件；但 `:80-82,105-106,128-129` 只提取/写入受支持的 `.bends`/`.bendsmeta`，其他 entry 会跳过，空 ZIP/无支持文件会拒绝。GUI 调用点是 `versions/26.2-fabric/src/main/java/io/github/recrivenvi/mobends/core/client/gui/LocalPacksScreen26.java:208-245`；目录扫描 `PackManager` 只直接消费 `.bends`/`.bendsmeta`，不自动把 ZIP 当已安装 pack。这里仅证明该 installer 对受支持 ZIP 解压有这些边界，不是对所有压缩输入或所有压缩处理路径的笼统安全证明；未发现可据静态证据成立的 ZIP-slip/无界解压反例，二进制资源限额/版本校验仍是待收窄的输入安全缺口。

### MB-R-002 — Procedural 非 additive 半权重仍执行附加旋转，不是稳定 blend

- **当前源码/测试符号**：`versions/26.2-fabric/src/main/java/io/github/recrivenvi/mobends/core/kumo/state/procedural/ProceduralLayerState.java:103-147` 的 `applyBoneTransform`；在 `additive == false` 且 `blendWeight < 1.0F` 时，`:127-130` 直接对当前 `SmoothOrientation` 调用 `rotateInstant*`，没有先恢复/采样本帧基准姿态。`.../core/kumo/state/template/procedural/ProceduralLayerTemplate.java:24-31` 将 `blendWeight` 定义为最终贡献权重，但 `:34-51` 只检查表达式存在。
- **U 行为/对照限制**：U 的 `LayerType`/KUMO state 没有当前 `PROCEDURAL` 层对应实现；这是 current port 新增能力，不能声称有 U parity，也不能把它包装成 U 遗失功能。
- **当前链/最小反例**：一个合法模板包含 `bones.body.rotationX = "10"`, `blendWeight = 0.5`, `additive = false`。如果 controller/vanilla 在 pack 执行前已经把 body 设为稳定 10°，本层第一次更新仍调用 `rotateInstantX(5)`，得到约 15°；与“目标 10°、权重 0.5、基底 10°”的稳定 blend 预期（仍为 10°）不相符。这里不把普通 body 的跨帧累积当作已证事实，因为真实 `Mutator.performAnimations` 每帧先执行 controller；已确认的是非零基底上的单帧错误叠加。`SmoothOrientation.rotateInstant` 的乘当前 end 语义见 `.../lib/math/SmoothOrientation.java:183-189`。
- **测试盲区**：`KumoPackCompatibilityTest:240-267` 只验证 Procedural 模板能校验，未连续调用状态更新、未断言旋转数值；因此其通过不能证明播放正确。
- **影响范围**：使用新增 Procedural 层、且运行时骨骼已有非零基底并采用 `0 < blendWeight < 1` 的 pack；静态可确定该分支的单帧数值偏差，动态画面表现待真实客户端确认。
- **状态**：`部分实现；静态缺陷已确认（当前扩展，U 无可比行为）。`
- **原因证据/下一步**：非 additive 分支的“blend”没有 per-frame base pose 或绝对设置；应明确层叠语义（基准姿态插值或每帧清零后重建），并加入两次以上更新的数值测试。不要用一次 update 通过替代稳定性证明。

### MB-R-003 — 坏 `.bendsmeta` 的 JSON 语法/类型异常逃过 PackManager 捕获

- **当前源码/测试符号**：`versions/26.2-fabric/src/main/java/io/github/recrivenvi/mobends/core/pack/LocalBendsPack.java:56-63` 的 `readFromFile` 只在声明层面抛 `IOException`，但 `new Gson().fromJson(...)` 对 `[]`、字符串或字段类型错误会抛 `JsonParseException`/`JsonSyntaxException`；`.../core/pack/PackManager.java:58-66` 仅 `catch (IOException)`。
- **U 行为/继承关系**：U `src/main/java/goblinbob/mobends/core/pack/LocalBendsPack.java:84-89` 同样直接 Gson 反序列化；U `.../PackManager.java:67-80` 也只捕获 IOException。因此这是已继承的异常恢复缺口，不能归咎于五 Target API 差异。
- **最小反例/当前链**：将 `gameDirectory/bendspacks/bad.bendsmeta` 写为 `[]`。`PackManager.initLocalPacks` 扫描到该文件 → `LocalBendsPack.readFromFile` 抛运行时 Gson 异常 → `initLocalPacks` 外层无对应 catch，阻断后续 pack 扫描及初始化；同目录的其他合法 pack 不会完成选择。另一类 `{}` 虽不抛异常，却留下 `key == null`、`displayName == null` 的 `LocalBendsPack` 并进入 `localPacks`，后续列表/配置选择可见 null ID。当前 `LocalBendsPackTest:8-34` 只构造有 key/displayName 的内存 metadata，不测试文件异常或必填字段；KUMO 数据异常测试 `KumoPackCompatibilityTest:129-152` 也不覆盖 metadata。
- **影响范围**：五 Target 共用 pack manager；恶意/损坏 metadata 可在客户端初始化或 reload 时中断，而不是被报告为单包无效并继续。
- **状态**：`部分实现；静态缺陷已确认（上游继承）。`
- **原因证据/下一步**：metadata 与 data 使用了不同异常边界；应在扫描单文件处捕获 Gson 解析异常并隔离该文件，给出文件名/原因，另补 `[]`、字段错型、缺字段的测试。暂未运行客户端初始化。

### MB-R-004 — Legacy 文本 BendsPack 接受无轴旋转语法却静默不生效

- **当前源码/测试符号**：`versions/26.2-fabric/src/main/java/io/github/recrivenvi/mobends/core/pack/LegacyBendsPackParser.java:24-26` 的 `ACTION` 正则允许 `rot`/`prerot`/`scale` 后的 axis 为空；`:125-132` 将空轴映射为 `Axis.ALL`。但 `.../core/pack/LegacyBendsPackPerformer.java:80-101` 的 `applyRotation` 通过 `axisIndex`; `Axis.ALL` 返回 `-1` 后 `:91-94` 直接 return，导致无轴 `rot`/`prerot` 完全无效。`scale` 走另一路 `:103-118`，全轴则会真正作用，形成同一语法对不同 property 不一致。
- **U 行为/对照限制**：U 的 v1.2.2 `LocalBendsPack` 将 `.bends` 当作 KUMO JSON 数据读取，没有当前这套 legacy 文本 parser；因此这里不能伪称“违背 U 的既有文本行为”，只能记录 current extension 的 grammar/performer 不闭合。
- **最小反例**：

  ```text
  target player {
      anim stand {
          @body:rot: == 45
      }
  }
  ```

  解析器接受并保存 action，但执行时 axis 为 ALL、rotation 分支直接 return，body 不变。现有 `LegacyBendsPackParserTest:18-48,150-173` 只覆盖显式 `x/y/z`，未覆盖空轴；`LegacyBendsPackParserTest:50-71` 的“malformed action”也不是这个已被正则接受的退化语法。
- **影响范围**：仅使用当前 legacy 文本扩展的本地 pack；动作被静默丢失，难以从“pack 已加载”表象定位。
- **状态**：`部分实现；静态缺陷已确认（current extension，无 U parity 结论）。`
- **原因证据/下一步**：parser 把空轴声明为 ALL，performer 却只对 scale 实现 ALL；应明确语法（拒绝无轴 rotation，或实现 X/Y/Z 全轴组合），并补执行层数值测试。

### MB-R-007 — Addon 预览器/外部编辑器 API 是有记录的范围收缩，不是未查明的移植缺失

- **当前源码/测试符号**：当前 `versions/26.2-fabric/src/main/java/io/github/recrivenvi/mobends/core/addon/AddonAnimationRegistry.java:23-91` 只有两种 `registerNewEntity`（基础和自定义 key）以及实体/trigger condition 注册；当前树中没有 `core/bender/IPreviewer`、`AnimationEditorRegistry` 或 `IAnimationEditor`。`AddonApiCompatibilityTest.java:9-29` 只测试 addon 列表和 `getDisplayName`，没有编译/反射断言这些旧 API。
- **U 行为/直接对照**：U `src/main/java/goblinbob/mobends/core/addon/AddonAnimationRegistry.java:28-47,50-81` 提供带 `IPreviewer`、`alterableParts`、`unlocalizedName` 的多组 `registerNewEntity` 重载；U `:125-128` 还提供 `registerAnimationEditor(IAnimationEditor editor)`。这些是实际方法体，不是注释壳。U 的 `EntityBender` 还保留 `getAlterableParts`/`getPreviewer`（`src/main/java/goblinbob/mobends/core/bender/EntityBender.java:64-72`），当前 `EntityBender.java:81-83` 只剩数据/Mutator factory 抽象。
- **当前链/最小反例**：一个按 v1.2.2 Addon API 编写的外部 addon 调用 `registry.registerNewEntity(Entity.class, dataFactory, mutatorFactory, renderer, previewer, "head")` 或 `registry.registerAnimationEditor(editor)` 时，在当前 port 直接编译失败；即使只迁移运行时，预览器和可编辑部件元数据也没有承载字段。当前 DefaultAddon 也不再传递 U 中的 previewer 参数。该问题跨五个 Target 的 AddonAnimationRegistry authored 内容一致，不是某一个 loader 的入口差异。
- **范围/删除原因证据**：`git show 92c5960f9ab4749f9a243d8f4708059c2b706a7a:docs/development/compatibility-policy.md` 明确正式源码级兼容面是 `AddonHelper`/`Addons.getRegistered`、`IAddon` 生命周期、Bender/动画/KUMO 条件注册；同文件明确“外部动画编辑器、Previewer ... 不是正式兼容面”。同提交 `docs/reference/upstream-behavior.md` 也明确外部动画编辑器不进入当前实现。这里有直接的历史范围决策，不能仅由 U 方法存在反推出当前遗漏。
- **影响范围**：若用户要求 U 的完整旧 Addon JAR/API，则会发生源码/ABI 不兼容；在当前已记录的 CleanBreak/正式 API 范围内，预览器与外部编辑器不属于待恢复功能。`AddonApiCompatibilityTest:9-29` 仅验证正式生命周期/显示名的一个最小面，不能证明旧 U API 全兼容，也不需要为被明确排除的面补空壳。
- **状态**：`范围已记录；非确认缺失（静态可对应当前正式 API 边界）`
- **下一步**：不因本条恢复 Previewer/editor；后续报告把“正式 Addon API 行为缺陷”（如 MB-R-005）与“明确排除的 U API”分开。若范围政策改变，再另开授权任务和独立迁移审计。

### MB-R-008 — KUMO 节点的负 playback/start 参数通过校验后把播放推进到非法帧

- **当前源码/测试符号**：`versions/26.2-fabric/src/main/java/io/github/recrivenvi/mobends/core/kumo/state/template/keyframe/StandardKeyframeNodeTemplate.java:17-27` 和 `MovementKeyframeNodeTemplate.java:14-24` 只检查 `animationKey` 存在，不限制 `startFrame` 或 `playbackSpeed`；`.../core/kumo/state/keyframe/StandardKeyframeNode.java:55,77-91` 允许负速度推进，`:101-103` 只按完成条件判断；`MovementKeyframeNode.java:52,95-105` 同样允许负进度。`KeyframeLayerState.java:152-166,198-210` 对帧列表使用直接 `get(frameA/frameB)`，没有边界保护。`ConnectionTemplate.java:25-37` 只拒绝负 transition duration，不拒绝非有限浮点值。
- **U 行为/继承关系**：U 对应模板和节点也没有这些数值约束（`src/main/java/goblinbob/mobends/core/kumo/state/keyframe/StandardKeyframeNode.java:58,83-103`）；这是上游继承的输入验证缺口，不是当前五 Target 独有的 loader 误差。
- **最小反例/当前链**：一个包含至少两帧有效骨骼的标准节点，设置 `playbackSpeed: -1`、`looping: true`，会在第一次更新后把 `progress` 从 0 推到 -1；下一帧 `KeyframeLayerState` 以 `frameA=-1` 直接 `keyframes.get(-1)`，抛 `IndexOutOfBoundsException`。把 `startFrame` 直接设为负数或超过动画长度可在第一次更新触发同一边界错误。移动节点的负进度会沿相同取帧链失去有效 frame；若 JSON 中使用极大数使 float 变为 `Infinity`，标准循环的 `while (progress >= animationDuration - 1)` 还可能永久处理不完。非有限 transition duration 则可能让过渡永不结束。
- **影响范围**：可被本地选择的 KUMO pack；比单纯“不播放”更严重，可能中断实体渲染/客户端帧。现有 KUMO 负例覆盖连接 target、ticks、表达式等结构，但没有 `startFrame`/`playbackSpeed` 数值矩阵；尚未运行反例。
- **状态**：`部分实现；静态缺陷已确认（上游继承）`。
- **原因证据/下一步**：节点模板验证边界没有把数值域传递到运行时安全契约；应校验 `startFrame >= 0`、`playbackSpeed >= 0`，并按实际动画帧长检查起始帧/帧数，或在状态层做显式夹取并记录错误。补模板校验和直接 `update` 的负矩阵测试。

### MB-R-009 — 非法资源标识符可从动画存在性校验逃出 IOException 边界

- **当前源码/测试符号**：`versions/26.2-fabric/src/main/java/io/github/recrivenvi/mobends/core/pack/PackDataProvider.java:108-123` 的 `doesAnimationExist` 只捕获 `IOException`；`.../core/animation/keyframe/AnimationLoader.java:79-87` 对含 `:` 的 key 直接调用 `IdentifierFactory.create(domain, path)`，非法 namespace/path 会由 Minecraft Identifier API 抛 `IllegalArgumentException`/资源标识异常，而不是 IOException。`PackDataProvider.getDataForPack:62-88` 也未捕获这类运行时异常。
- **U 行为/继承关系**：U `src/main/java/goblinbob/mobends/core/animation/keyframe/AnimationLoader.java:82-91` 同样直接 new `ResourceLocation`；U `PackDataProvider.java:102-110` 也只捕获 IOException。这里只证明代码形状相似；本轮没有读取 1.12 `ResourceLocation` 实现来证明其非法字符判定与 26.x `Identifier` 相同，因此“上游继承”程度未确认。
- **最小反例/当前链**：本地 `.bends` 中的标准节点设置 `animationKey: "mobends:bad path"`（任意无法构造的 resource identifier 均可）；Gson 反序列化成功，校验进入 `loadFromPath`，Identifier 构造异常穿过 `doesAnimationExist` 与 `getDataForPack`，而不是转成“missing animation”或 `InvalidPackFormatException`。现有 `KumoPackCompatibilityTest:162-238` 只覆盖合法形状下的 missing key/索引/条件，不覆盖非法 identifier 字符。
- **影响范围**：恶意/手工损坏 KUMO pack 的选择、初始化和 reload；异常恢复链与 MB-R-003 相同地不能隔离单包，但根因是资源 key 解析边界。
- **状态**：`部分实现；静态缺陷已确认（当前 Target；U 来源继承程度未确认）`。
- **原因证据/下一步**：资源标识解析异常没有纳入 pack-format 错误边界；应在 loader/validation 层统一把当前 Target 的非法 identifier 转为“动画不存在/InvalidPackFormatException”，并增加空格、非法 namespace、非法路径字符 key 的负例；不要把 resource identifier 的 `..` 语义当作文件系统 ZipSlip。若要宣称 U 同样受影响，先核对确切 1.12 类实现。未执行客户端加载。

### MB-R-005 — Addon 内容注册抛异常后，addon 被永久标记为已注册且不能重试

- **当前源码/测试符号**：`versions/26.2-fabric/src/main/java/io/github/recrivenvi/mobends/core/addon/Addons.java:18-26` 先在 `:21` 将 addon 放入列表，再在 `:23-25` 调用 `addon.registerContent(...)`；若该调用抛异常，列表保留对象。后续相同对象再次进入 `:19` 的 `contains` 即直接 return，永远不会再次调用 `registerContent`。
- **U 行为/继承关系**：U `src/main/java/goblinbob/mobends/core/addon/Addons.java:22-33` 顺序相同；这是 API 异常恢复的上游继承问题。
- **最小反例**：外部 addon 的 `registerContent` 第一次因暂时未就绪的注册依赖抛异常，第二次调用 `AddonHelper.registerAddon` 本应成功，但被 `contains` 短路；addon 名字仍出现在 `getRegistered`，EntityBender/TriggerCondition 却未完整接入。现有 `AddonApiCompatibilityTest:11-29` 只测试无异常空 `registerContent` 与显示名。
- **影响范围**：所有外部 addon 的注册时序/异常；可能留下“已登记但无内容”的占位状态，后续动画/条件查找静默缺失。
- **状态**：`部分实现；静态缺陷已确认（上游继承限制；当前语义仍未闭合）。`
- **原因证据/下一步**：注册列表写入与内容接线没有事务边界；应决定失败时移除、保留可重试状态或记录失败并提供显式 retry，并加一次抛异常后重试的 API 测试。

### MB-R-006 — Addon 在 Core 创建前注册时只进列表，不会在 Core 创建后重放内容注册

- **当前源码/测试符号**：`versions/26.2-fabric/src/main/java/io/github/recrivenvi/mobends/core/addon/Addons.java:18-26` 仅在调用时 `Core.getInstance() != null` 才执行 `registerContent`；Core 为空时只保存 addon。当前 Core 创建/默认 addon 入口见 `.../fabric/main/MoBendsClient.java:30-34`，但 `CoreClient.createAsClient`、`ClientRuntime.refreshSystems` 或 `Addons.onRefresh` 均没有遍历既有 addon 并补调用 `registerContent`。
- **U 行为/继承关系**：U `src/main/java/goblinbob/mobends/core/addon/Addons.java:22-33` 使用 `CoreClient.getInstance()` 的同一一次性门槛；这是兼容语义的上游继承限制，不应宣称当前 port 已改善。
- **最小反例**：某依赖 mod 在 MoBends 客户端 Core 初始化前执行 `AddonHelper.registerAddon(modId, addon)`；调用成功返回且 `getRegistered` 可见，但 `addon.registerContent` 从未调用，之后该 addon 的 bender/trigger condition 永远不在注册表中。当前测试只在 Core 状态不涉及的空 addon 上验证列表可见性。
- **影响范围**：依赖加载顺序、Fabric/Forge/NeoForge 客户端启动时序及外部 Addon API；表现为“addon 已注册”与“动画内容不可用”的分裂状态。
- **状态**：`定位未接线`（上游继承；是否为公开时序契约仍需 API 文档/真实 mod 加载顺序复核）。
- **原因证据/下一步**：注册函数没有待初始化队列或 Core-ready replay；应明确 API 允许的调用时机，若要兼容任意合法加载顺序则在 Core ready 时重放一次，并防止重复注册。

### MB-R-010 — 自定义 MutatedRenderer 异常时 NeoForge render-state pose/context 没有 finally 恢复

- **当前源码/测试符号**：26.1.2/26.2 NeoForge 的实际有效 authored 文件分别是 `versions/26.1.2-neoforge/src/main/java/io/github/recrivenvi/mobends/neoforge/client/event/RenderingEventHandler.java:193-375` 与 `versions/26.2-neoforge/src/main/java/io/github/recrivenvi/mobends/neoforge/client/event/RenderingEventHandler.java:193-375`；两者 active 分支的 `beforeLivingRender:311-357` 在 `:339` push PoseStack、`:334-337` 写入 render-state context，随后调用可由 Addon 提供的 `bender.beforeRender(...)`。`afterLivingRender:362-371` 读取 context，先调用可覆盖的 `afterRender(...)`，再 `popPose()`；两处都没有 try/finally。到 `MutatedRenderer` 的直接调用边也在 `versions/26.1.2-neoforge/src/main/java/io/github/recrivenvi/mobends/core/bender/EntityBender.java:98-105` 与 `versions/26.2-neoforge/src/main/java/io/github/recrivenvi/mobends/core/bender/EntityBender.java:98-105`，不是 Fabric 条件文件。
- **U 行为/继承关系**：U `src/main/java/goblinbob/mobends/core/client/event/EntityRenderHandler.java:27-55` 同样先 push、调用 before/after，再 pop；这是异常恢复形状的继承风险，不是当前 loader 特有证明。
- **最小反例/影响范围**：外部 addon 的 `MutatedRenderer.beforeRender` 在 `beforeLivingRender` 抛 RuntimeException：该实体本次 PoseStack 没有对应 finally pop，render-state context 仍写在可复用 state 上，后续模型层/下一次 state 生命周期可能读到半初始化上下文。若 `afterRender` 抛异常，则 `:371` 的 pop 永远不执行。默认 renderer 不抛异常时不据此宣称已动态复现；这是扩展 API 的受控异常路径。
- **状态**：`部分实现；静态缺陷已确认（上游继承，动态影响待验）`。
- **原因证据/下一步**：恢复动作分散在 Post 事件且没有 finally；应在不改变正常渲染顺序的前提下设计异常清理/上下文失效策略，并加一个只抛异常的自定义 renderer 受控测试。不要把静态异常路径写成已观测客户端泄漏。

## 已查但暂不立项

- 当前五 Target 的 `BendsPackPerformer`、`PackDataProvider`、`PackManager`、`LegacyBendsPackParser`、`AddonAnimationRegistry`、`ClientRuntime` 关键文件 SHA-256 相同，因此暂未把同一问题重复记为五条跨 Target 差异。
- U 的 `PackDataProvider.createBendsPackData` 在空列表后仍会继续 `PackCombiner.combineData`，current port 已显式 return；这不是当前反例。
- 资源路径 `mobends:bends/animators/wolf.json` 与 `.bendsanim` 引用在各 Target authored resources 中均存在；仅静态存在不能替代运行时资源 reload/播放验证，暂未据此报失联。
- `wolf_run.bendsanim`、`wolf_test.bendsanim`、`player_anim_harvest.json` 在当前 authored resources 中存在，但 `rg` 未找到当前 animator/controller 引用；U 的历史行为清单也记录它们没有默认 animator 引用。它们是“存在但未接线”的历史素材证据，不足以宣称功能缺失或要求恢复，后续只需在资源清单中保持该状态。
- Addon `DefaultAddon` 对 Drowned/GlowSquid 的排除已有专门注册与测试；Husk 等 1.12-era 子类不应仅因现代继承关系被重复报为缺失，需遵循 U 的历史支持语义。

## 第一批覆盖/剩余

已查并落盘：

- 五 Target 共享的 KUMO JSON 链（验证、状态实例化、节点播放）、二进制动画入口及退化输入；
- Procedural 新增层的非 additive 权重语义；
- 本地 pack metadata/data 异常边界、非法资源标识符、Legacy parser→performer；
- Addon 注册异常恢复、Core-ready 时序，以及 U API 与当前正式兼容范围的差异；
- 26.1.2/26.2 NeoForge 实际有效 render-state 异常清理路径；
- ZIP installer 的受支持文件提取、entry/解压量/ZipSlip/重复名边界；资源引用和未接线历史素材的静态证据。

尚未证明或不在本代理范围：

- 没有运行构建、测试、游戏或恶意 pack；上述反例均为静态链推理，不能写成动态复现；
- 没有完成打包后资源索引/实际 reload 验证，也没有外部 addon 编译测试或自定义 renderer 抛异常的运行验证；
- 五 Target loader 入口返回值的完整行为差异、Addon key/trigger 重复注册及 pack 重复 key/孤儿文件未形成独立结论；
- 模型、披风、第一人称、剑轨迹及 `centerRotation` 交互由主代理 P-008 复核，本代理不重复扩展。
