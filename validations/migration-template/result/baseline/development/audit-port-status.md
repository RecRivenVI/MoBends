# MoBends 旧 port 移植现状静态审计

日期：2026-09-10。状态：本轮静态研究交付，具有下列明确的覆盖和未决边界；不是“全源码逐行穷尽审计”，更不是构建、运行、视觉或功能完整性 PASS。
本轮仅审计及记录，不改变源码、资源、依赖、运行配置或 Git 暂存区。

## 身份、内容绑定与基线

- 旧 port：`D:/Workspaces/Repositories/GitHub/RecRivenVI/MoBends`，不是 Local Second Implementation。
- 分支 `stonecutter`；HEAD `01d5cff03a188ee3bf0366d4e2c17ed0ff1e85a3`，标题 Update。
- origin `https://github.com/RecRivenVI/MoBends.git`；upstream `https://github.com/mobends/MoBends.git`；upstream push `DISABLED`。
- 开场工作树：6 个 tracked 修改、583 个 tracked 删除、2786 个 untracked 路径；暂存 0。当前源码是未提交工作树，不能归因于 HEAD 树。
- 本轮内容清单：`D:/Workspaces/Artifacts/MoBends/PortStatusAudit/20260910-152656/before-seal.json`，SHA-256 `FE0527BD905E4E47F1D187D60AD3D8474636871AB94F96F9D5B0A2FBF0C6966F`。2784 个存在的 authored/可审阅配置与证据文件，完整 Git status/index/raw diff；排除 runtime、instance、build、cache、local.properties 与 .env 内容。缺失 tracked 路径保留在 status/index 中。审计结束重采，审计文档是预期新增，其他变化必须披露。
- 总协调独立封存：`D:/Workspaces/Artifacts/OldPortAudit/20260910/MoBends-start.json`，报告的 SHA-256 `b7e737876af7400b1bc252884b57568f828c6fafb30c85a8c06a288ca749dc13`，sourceSeal `d57c118c2a2a36ceb13d131badcddeaa01ee8223d5c1415846ca9f67e664f979`；其排除范围以该清单为准。
- 真实移植基线 U：`0508765b3a9066d0eb8124fbe771a0ca4600f637`，树 `7d31d5ef2ada79d039dce4b69dcb468cf4576d2f`。证据：唯一 Initial port `92c5960f9ab4749f9a243d8f4708059c2b706a7a` 的直接父提交，同时为本地 `v1.2.2` 和 `upstream/1.X/forge-1.12` 所指；提交标题 bump: Version 1.2.2。不以最新上游替代此基线。
- U 的独立只读参考目录：`D:/Workspaces/References/MoBends/Upstream/0508765b3a9066d0eb8124fbe771a0ca4600f637`，由本地 Git 对象 archive 提取，无构建或 checkout 写入 port。归档 `D:/Workspaces/Artifacts/MoBends/PortStatusAudit/20260910-152656/upstream-0508765.tar` SHA-256 `B9265A9F79D6683FD932318E7C0448DC20720B9DC8DBD649E3F9E699EE1B5A90`。

## 范围、状态与分工

覆盖单位是能力/子系统/接口/资源集合，不是文件相似率。生产实现、自动断言、历史运行记录分开。
状态采用：未检查、尚未找到对应、定位但接入未确认、部分实现、空壳或不可达、确认缺失、静态对应已确认、行为待动态验证。
实现存在不等价于行为等价；资源存在不等价于引用和打包正确。

- A：上游独立清单，`audit-port-upstream.md`，ID `MB-U-*`。
- B：当前五 Target 正向映射与反向新增，`audit-port-mapping.md`，ID `MB-M-*`。
- C：反例、缺失及关键等价复核，`audit-port-review.md`，ID `MB-R-*`。
- 主任务：本入口、快照、关键动画/联网/跨目标独立复核，ID `MB-P-*`。总协调文档不在本线程写范围内。

## 当前目标事实

`settings.gradle.kts:29` 起注册原生 `:version:<target>`；每个 Target 自有 src/main、src/test、build.gradle.kts 和 target.properties。旧 Stonecutter 注释不是当前条件编译执行器。

| Target | 声明 | Java | Loader/API | 本轮状态 |
|---|---|---|---|---|
| 1.20.1-forge | implemented | 17 | Forge 47.4.23 | 源码接线研究中 |
| 1.21.1-neoforge | implemented | 21 | NeoForge 21.1.248 | 源码接线研究中 |
| 26.1.2-neoforge | implemented | 25 | NeoForge 26.1.2.97 | 源码接线研究中 |
| 26.2-fabric | implemented | 25 | Loader 0.19.3 / Fabric API 0.156.0+26.2 | 源码接线研究中 |
| 26.2-neoforge | implemented | 25 | NeoForge 26.2.0.66 | 源码接线研究中 |

事实来源为每个 `versions/<target>/target.properties`。角色仅 client/client-multiplayer；可选服务端业务代码另行查，不据此宣称 server 测试角色。未执行构建或任何产品测试。

## 研究账本与恢复点

第一批待核对：U 的七类 Bender 注册 → 当前各 Target ClientRuntime/Loader 事件/Mixin；联网服务拆分 → 本地包与 Minecraft 连接内策略；动画状态与装备/轨迹坐标链。随后合并资源、Addon、包解析、配置、测试断言及反向新增。

实际三子代理：`/root/upstream_inventory`、`/root/current_mapping`、`/root/adversarial_review`。均由 `collaboration.spawn_agent(agent_type="luna_worker", fork_turns="none")` 创建；该角色固定 GPT-5.6 Luna/max，工具返回 canonical task ID，不另行虚构 UUID。主模型已由总协调读取本轮 turn_context 确认且本线程已回读最小证据：`D:/Workspaces/Artifacts/OldPortAudit/20260910/main-task-models.json` 的MoBends项为 `gpt-6-astra` / `max`、turn `01a08a35-3d37-7692-939b-c7391715dfd3`；不是仅请求被接受。

## 主任务独立代码复核（持续更新）

路径缩写：U 为上游参考 `src/main/java/goblinbob/mobends/`；C(T) 为当前 `versions/<T>/src/main/java/io/github/recrivenvi/mobends/`。完整目录和内容 hash 见上述封存。行号均指原文件，不对注释删除后的文本重新编号。

### MB-P-001 — 七类实体与动作层入口

- 上游入口 U `standard/DefaultAddon.java:28` 的 PlayerBender 与其余六类注册；当前 C(26.2-fabric) `standard/DefaultAddon.java:25-52` 注册 player/zombie/skeleton/zombified piglin/spider/squid/wolf；`standard/PlayerBender.java:14-25` 绑定 PlayerData/PlayerMutator/PlayerRenderer。其他 Target 的 Loader 入口与资源接线由 B 独立逐个核查，不因本条直接外推。
- U `standard/animation/controller/PlayerController.java:81-190` 与 C(T) 同文件 `81-154`：睡眠优先于骑乘；随后鞘翅、攀爬、游泳、离地/飞行/坠落/跳跃、站立/疾跑/行走；层执行顺序 base→sneak→torch→item actions→cape；每轮先归零左右持物旋转。
- 当前增加 Drowned/GlowSquid/baby Squid 排除（DefaultAddon:30,45-47），不是 1.12.2 上游遗漏；排除原因属于现代实体适配线索，逐实体视觉接受仍不可由静态推出。
- 变化：继承并适配；静态动作入口对应已定位，实际多实体注册可达范围仍结合 B 账本。不能据此声称所有动画数值或输出相等。

### MB-P-002 — 游泳触发和根旋转所有权已改变

- U PlayerController:119 使用 `player.isInWater()`；C(T) PlayerController:105 使用 `SwimmingPosePolicy.shouldAnimate(player.isVisuallySwimming())`。五份 Controller/SwimmingPolicy/SwimmingAnimationBit 均逐目标检索及哈希对照，游泳策略/动画正文一致。
- C(T) `standard/animation/SwimmingPosePolicy.java:12-39` 保留原版视觉游泳触发；动画根基础俯仰 80 度、视角俯仰绕身体中心、头部逆补偿。`SwimmingAnimationBit.java:45-78,120-136` 仍含原来的静止/拉弓/攻击十 tick 内/未完全水下的踩水式分支。现代姿态触发并不等于一直水平游泳，尤其攻击和水面边界需实测。
- 26.x C(T) `mixin/AvatarRendererMixin.java:32-45` 在原版 setupRotations 的 swimAmount 读取处，只有实体映射存在且 Bender animated 才返回零；禁用时返回原 swimAmount。1.x 对应 `mixin/PlayerRendererSwimMixin.java:15-27` redirect `getSwimAmount`，但 `require=0` 允许注入不匹配时不报错。后者必须验证实际 Mixin 匹配，不能仅以存在为成功。
- 原因：本任务历史用户明确要求“没触发原版游泳动作之前，即使水下走路也不要触发”及“枢轴放身体附近”，属明确产品行为变更，不是纯搬迁；是否已达到视觉要求仍为行为待动态验证。
- C(T) `src/test/.../standard/animation/SwimmingPosePolicyTest.java:12-93` 断言布尔/角度/端点与标量相加；没有启动 renderer/Mixin，也没有断言真实矩阵组合、入水多帧或异常恢复。测试名称中的 endpoint 不等于实际退出姿态验证。
- 结论：静态接管策略已确认，不宣称与上游等价；跨 Target 注入和水面/攻击组合为高价值后续动态任务。

### MB-P-003 — 连接内服务端策略保留；不能与公共 HTTP 删减合并

- U `core/network/NetworkConfiguration.java:19-42` 默认 singleplayer=(scale=true,packs=true,movementLimited=false)，multiplayer=(false,true,true)。C(T) 同类:16-26 对应并新增 synchronized setter；五份文件 SHA-256 相同。
- 当前接收/发送：C(26.2-fabric) `fabric/network/FabricNetwork.java:19-42` clientbound payload、JOIN 初始化、canSend 检查；C(1.20.1-forge) `forge/network/ForgeNetwork.java:17-48` acceptMissingOr、PLAY_TO_CLIENT、isRemotePresent；三个 NeoForge `neoforge/network/NeoForgeNetwork.java:19-35` optional/playToClient/hasChannel。主代理已分别阅读，不以 Fabric 代替。
- 消费者 C(26.x) `core/mutators/Mutator.java:228-242`（1.x 有效分支:517-531）：先执行内置动画，再按允许标志执行 BendsPack；移动限制只把包产生的 global/local offset 限到与内置结果距离 10。不是修改真实玩家速度/碰撞/位置。菜单也按 packsAllowed 禁用包入口。
- `modelScalingAllowed` 上游和当前均有字段/getter，但在上游完整源码搜索无模型缩放执行消费者；当前只有传输/配置/测试读取。不能把“允许缩放”当已有效缩放限制，亦不能误报为本次漏移植。
- 上述“无消费者”限于本轮内置源码，不否定外部Addon可能自行使用公开getter。当前新增Legacy SCALE执行可缩放part，而内置代码仍不读此flag，因此也不能向玩家承诺新包缩放受该字段限制。
- 当前 `ServerNetworkConfig.java:34-53` 的本地 JSON 读取仅 catch IOException，不捕获 Gson 语法/类型异常；配置损坏时“safe defaults”日志承诺不能覆盖该异常路径。五份相同，属于当前容错缺口，不是断言已发生崩溃。
- `ServerNetworkConfigTest.java:15-59` 仅测正常默认值、显式三字段和缺字段；`NetworkConfigurationTest` 测对象赋值，未覆盖畸形 payload、真实握手顺序、断线重连。当前 onWorldJoin 重置可见，未找到专用断线策略清理调用；不据此断言下一次连接必定泄漏，仍需事件顺序验证。

### MB-P-004 — 公共联网删减的逐项边界

主代理独立逐读下列 U 入口，并检查五目标源目录、Loader 初始化、资源刷新与对应消费者。C(T) 不存在旧 connection/supporters/asset 公共模块；当前生产 Java 检索 HTTP/URL/openConnection 不见旧传输链。构建下载和 Required Test Mod 安装不属于此产品网络清单。

| 子项 | U 直接行为 | 当前对应及状态 | 原因证据与限制 |
|---|---|---|---|
| MB-P-004a 活动上报 | CoreClient:31-34 注册 ConnectionManager；ConnectionManager:38-69 POST activity/join 并起 ping/settings daemon；PingTask:55 POST activity/ping | Loader 初始化无替代活动服务；确认删除 | 92c5960 的本地-only/远程服务边界间接解释活动上报移除；未找到逐 endpoint 的独立用户决定，不能称逐项已验收 |
| MB-P-004b 支持者远程身份/饰品 | SupporterContent:36-44 拉 details；PlayerSettingsDownloader:66-73 拉玩家 accessory；LayerPlayerAccessories:50-79 选择饰品并取 AssetModels | 远程饰品和身份整链删除；普通皮肤/披风、持物/盔甲与通用 wearable 链仍在，分别审计 | 历史已提交 `92c5960:docs/reference/upstream-behavior.md` 的“不属于当前功能”明确列支持者/远程饰品/玩家设置；不等于对所有第三方 wearable 的删除授权 |
| MB-P-004c 按玩家剑轨颜色 | U BipedEntityData:57 → SupporterContent.getTrailColorFor:103-113，默认白色，远程配置可改色 | C(T) BipedEntityData:54 固定白色 supplier；基础剑轨保留，远程个性颜色删除 | 支持者服务移除的可观察连带效果，不能笼统写“剑轨完全等价”；无新本地改色入口证据 |
| MB-P-004d 远程资源清单、下载、模型纹理缓存 | AssetsModule:34-43,66-73,108-158；AssetReloadListener:26-67 下载/注册外部模型纹理；LayerPlayerAccessories 消费 | 旧资源服务与饰品资源加载删除；当前 ClientRuntime:18-30 刷新内置/本地包缓存，不访问该服务 | 历史远程饰品/服务排除可解释，但其离线缓存消费能力也随之丢失；不宣称旧缓存仍兼容 |
| MB-P-004e 本地包远程缩略图 | U LocalBendsPack → PackManager:157-159 → ThumbnailProvider:21-37 ThreadDownloadImageData | 当前 LocalBendsPack:60-66 只本地 metadata/data；旧缩略图传输不在；本地包仍能读取 | 历史“本机读取、不恢复下载”明确；metadata 保留某字段不等于加载该缩略图 |
| MB-P-004f 公共包数据库 | U PublicDatabase:17-34 有下载方法，但完整 U 搜索只见定义；不应证明原版 UI 已实际接线在线搜索 | 类和接口删除；尚不能称删掉了完整可达的在线搜索产品 | 历史文档明确排除公共数据库；基线自身可达性不足须保留，不虚增功能损失 |
| MB-P-004g 外部动画编辑器链接 | U GuiBendsMenu:140 调 WebAPI.getOfficialAnimationEditorUrl；WebAPI:16-57 拉 static-api.json | 当前设置/包屏无该 WebAPI 或外部编辑器入口；确认删除 | 92c5960 同份上游行为参考及 compatibility-policy 明确排除 external editor |

上述历史文件通过 `git show 92c5960:<path>` 读取，未恢复到工作树；其中旧 planned 矩阵已失效，只引用当时功能边界，不把它视为本轮五目标声明或用户新接受。公开服务是否仍在线未联网测试，也与迁移静态差异无关。

### MB-P-005 — 剑连段/使用动作的保留与实质分歧

- U `standard/animation/bit/biped/item/SwordAction.java:23-49,64-97` 五动作轮转、20 tick 清连段、10 tick 后至 60 tick 地面警戒/疾跑架势；当前 26.x 同文件:25-94 基本保留这些分支，spin 配置决定第四或第五动作，增加 action/attack_N 标签。架势持续不是自动证明剑轨残留合理，轨迹生命周期另审。
- 两个 1.x 有效 SwordAction:133-156 对非本地实体改用 `(tickCount / 6) % moveCount`，三个 26.x:35-50 对所有实体按观测攻击顺序递增。五文件的有效分支主代理逐个核对；1.x PlayerData:296-340 也按 tickCount 推算非本地拳/剑序列，26.x PlayerData:119-150 顺序推进。不是映射签名差异；代码注释称 deterministic sync，但没有证明实际跨客户端同步，更无证据称 API 强制这样做。状态：跨目标语义分歧已确认，原因仅间接线索，行为待动态验证。
- 当前 `BipedActionController.java:84-130,160-232` 新增弩/三叉戟/长矛/望远镜/角/刷子原版手臂语义桥；未知 use 不再默认 FOOD。它仍把 use 和 attack 写入同一个 HardAnimationLayer，需按组合条件测，不应只因各 enum 映射测试就认为所有并发动作优先级正确。
- 继承风险：U BipedActionController:172-175 的 clearAction 只清层，当前:235-239 另清 use 缓存但未清 currentAttackActionType。PlayerController:66-68 在睡眠时调用；若睡前/醒后攻击类别不变且没有新的 use 转换，:218 比较相等就不重新播攻击层，HardAnimationLayer:34-35 已为空。静态状态追踪提示醒后攻击动画可能停用直到类型/使用态变化。上游也存在相同核心缓存模式，不能定性为本次新增遗漏；当前测试只测 use 替换谓词/枚举，不测睡眠恢复。

### MB-P-006 — 披风与装备坐标链不是单常量测试可证明

- U `LayerCustomCape.java:35-65` 检查玩家信息、可见性、CAPE 开关、纹理、非鞘翅；潜行平移 4 模型像素后 body→(0,-12,2.2)→cape→Y180；CapeAnimationBit:45-47 另加25度。
- 26.x C(T) `LayerCustomCape.java:37-71` 改从 AvatarRenderState 取纹理/开关/胸部装备；从 RenderStateEntityContext 找实体与 PlayerData；以一次性 BendsCapeRenderer 拍下披风阶段再 submitCustomGeometry。`PlayerData.java:43-50` 潜行附加 pitch/Y 都为0；`mutated/PlayerRenderer.java:14-31` 接地补偿2像素、飞行4像素。1.x 有效 LayerCustomCape:124-152 走 IPlayerSkinProvider+即时缓冲，无额外披风层Y；PlayerData:217-218 仍加25度，PlayerRenderer 的接地补偿5像素。
- `PlayerMutator.java:109-131` 真实交换/恢复 CapeLayer；鞘翅层未交换，走已有姿态同步/Mixin适配，不能误记“披风替代覆盖鞘翅”。`BendsCapeRenderer.java:20-59,88-181,194-231` 保留16 slab、10×16×1尺寸、波动/铰链/UV集合，改为矩阵/顶点输出。
- 变化：版本适配+历史用户针对潜行披风问题的修正。存在和被绑定已确认；1.x/26.x最终几何表现等价未证明。C(26.x) `PlayerRenderPoseCompatibilityTest.java:12-16` 只断言两个 getter=0，没有胸背遮挡、UV朝向、转身/潜行多帧、鞘翅/无披风视觉断言。`MutatedRendererTest.java:13-36` 实际测 body yaw 向量和绕±180，而非整个披风矩阵链。
- 异常边界：披风层 pushPose 后的 submit/正文到 pop 不在 finally，slab递归同样如此；正常路径平衡不等于异常路径恢复。由 C 独立反例账本补充调用方与影响，不将潜在异常写成已观察到的画面。

### MB-P-007 — 帧时钟与旧空壳的反证

- U `core/client/event/FluxHandler.java:10-19` 调 reevaluateDirty；当前同名类空不代表能力删除：Fabric RenderingEventHandler:41-60、NeoForge1.21.1 RenderingEventHandler:51-75 真实调用迁到 `lib.flux.ComputedDependencyHelper`。
- 当前 DataUpdateHandler.update:28-31 仍 clamp每帧增量到0..1，暂停归零；EntityDatabase.updateClient:59-75 清掉实体ID同但对象不同时的旧data。updateRender:78-80在渲染时更新所有已知实体，刷新时clear。存在→Loader调用映射由B逐节点补齐。
- 剩余：菜单/断线不渲染世界时的缓存寿命、世界替换第一帧顺序、实体递归渲染的上下文隔离，不从普通tick路径推断已经安全。

### MB-P-008 — 轨迹基本生命期保留，中心俯仰的坐标链存在缺证

- U `standard/client/renderer/entity/RenderBendsArrow.java:19-25` 先渲染箭轨再 super；当前26.2 Fabric `mixin/ArrowRendererMixin.java:20-44` 捕获箭实体→submit轨迹；NeoForge与1.x的对应接入见B。当前 ArrowTrail:18-51 保留10节点、1tick采样、长间隔重置；SwordTrail:26-27,62-99,198-205 每次动作可reset、5tick淡出、20tick删除。不能把删除时长20tick当20tick可见；淡出更早。
- 当前 26.2 两 Loader `core/client/TrailPhaseBridge.java:18-53` 对 SubmitNodeStorage 将自定义轨迹放 afterTerrain 并选 translucentTarget，非世界collector降级普通submit；26.1.2该类有效尾分支只有普通submitCustomGeometry。不是所有26.x都具备同一透明阶段。默认/改进透明水玻璃前后遮挡仍需各目标动态验收，不宣称透明排序等价。
- **坐标链风险（已定位，行为待验证）**：C(26.x) SwordTrail.add:172-189 只保存body/arm/foreArm/item、globalOffset、renderRotation；TrailPart.getPoints:77-90 无centerRotation/localOffset。Fabric LivingEntityRendererMixin:83-95 在bender.beforeRender前，只补body yaw/scale即提交剑轨；而MutatedRenderer.beforeRender:43-52对身体另施中心俯仰和localOffset。SwimmingAnimationBit:120-132明确把视角俯仰写centerRotation；AttackSlashUp:47-49在攻击早期仍可产生trail。故“游泳过渡t>0 + 非零视角俯仰 + 挥剑”是具体坐标链不一致候选，不能以yaw单测解释为已覆盖。
- U MutatedRenderer:43-57 已有centerRotation/localOffset，U SwordTrail:173-177同样未采样它们；这是继承的不完整轨迹变换，被现代游泳视角实际消费centerRotation放大，不应笼统说全是本次新bug。C独立复核结果再并入风险表。
- 清理限制：ArrowTrail.shouldBeRemoved:120-121 有level=null或arrow removed，但DefaultAddon:61-62只在showArrowTrails=true才调manager，Fabric updateRenderTick:43-44无world直接return；因此有清理谓词≠退出世界必执行它。不据此声称稳定内存泄漏大小；需要退出/禁用/重入的调用时序验证。

### MB-P-009 — 游泳 root 接管条件不等于 mutation 成功

- 26.x AvatarRendererMixin:40-45 与1.x PlayerRendererSwimMixin:23-27 只检查 bender存在/animated；没有检查本次实际applyMutation成功或render context。C(26.x) LivingEntityRendererMixin:70-71允许applyMutation=false而继续原版submit；EntityBender:116-122和Mutator:127-129确实提供false路径（无模型/不符合模型）；PlayerMutator:552-553限制PlayerModel。
- 标准原版PlayerModel正常时不能据此推导必然失败；但自定义/替换模型拒绝mutation时，配置flag仍可使原版swim root被抑制。状态：接管guard部分覆盖，替换模型反例需要动态或后续受控契约测试；本轮不引入兼容目标、不修实现。
- 原因：现代root适配，U无1.13+游泳根动作可直接照搬。没有证据说明用户接受“拒绝mutation但也不保留原版根动作”。

## 主复核与冲突修正记录

主任务不直接照搬A/B/C结论。已回读 U EntityRenderHandler、EntityBender、EntityDatabase、DefaultAddon、RenderBendsArrow，要求A纠正初稿：Post仅afterRender+pop，非每帧demutate；Bender键用连字符；preview豁免在updateClient，refresh全clear；专用注册注释不排除父类Bender继承；箭条带不能直接称camera-facing。这些纠正是研究中的证据收敛，不删除旧数据证据。

进一步主复核：U PackCombiner 倒序 putAll 使首项优先，非后项优先；LayerPlayerAccessories:66 明确检查 unlocked/hidden，不能说没有检查；C 的 float 模零为NaN，非整数除零异常；Procedural连续两次update的孤立反例不能忽略每帧内置controller重置基底。已反馈各自文档所有者修正，最终结论以精确代码链为准。

### MB-P-010 — 主任务回源确认的包输入/扩展风险

- **退化KUMO循环，R-001，优先级P1**：主已完整回读 C(T) PackDataProvider:94-145 → BendsPackData.getAnimation:21-34 → pack/state/PackAnimationState:14-46 → KumoAnimatorState:28-44 → KeyframeLayerState.update:53起 → StandardKeyframeNode:46-55,77-84。空bones的已声明动画可过validate；restPose和keyframe遍历空集合无异常，继而update按duration=0反复减-1，静态非终止成立。含单帧真实骨骼则可能先在frameB取值越界，不能把所有退化输入归为同一种死循环。U也保留同一duration/while形式，属继承缺陷；不是已执行恶意包。
- **metadata隔离，R-003，P1**：主已读 LocalBendsPack:60-66、PackManager:58-66与initialize/reload外层窄catch；Gson错误不转IOException，坏metadata会越过单包捕获。U也如此。对应服务端JSON同类问题见P-003。已有本地包能力是部分保护，不应归为“整项本地包缺失”。
- **新Procedural非additive权重，R-002，P2**：主已读 ProceduralLayerState:103-145及centerRotation:168-195；weight<1与additive共用rotateInstant*，不是两姿态插值。但普通body每帧可能由controller重设，持续累积程度需要具体基底/骨骼证明。应记静态blend语义缺陷与条件限定，不断言所有玩家身体不断自旋。U无此实现，属于当前新增能力的边界。
- **Legacy无轴rotation，R-004，P2**：主读 LegacyBendsPackPerformer:80-118,172-177，Axis.ALL旋转直接return、scale则全轴生效；parser允许无轴，故语法/执行闭合不足。U锁定版本本身是JSON KUMO，不把当前历史文本扩展问题误报成U已完成语义遗漏。
- **Addon，R-005/006/007**：主读当前Addons:18-42、AddonAnimationRegistry:23-91与U对应实现，确认列表先登记/无失败撤销/无Core-ready回放；默认Addon在Core完成后注册并不受早注册反例影响。预览器/编辑器重载和元数据确实删减，但 `92c5960:docs/development/compatibility-policy.md` 明确排除Previewer/外部编辑器；这属于有记录的API范围收缩，不自动建议恢复，也不把“基本Addon能登记”称完整U API兼容。
- 目标范围证明：主对上述 PackDataProvider、StandardKeyframeNode、KeyframeLayerState、ProceduralLayerState、Legacy parser/performer、PackManager、Addons、AddonAnimationRegistry、BinaryAnimationLoader 各五份文件分别计算SHA-256，每组均只有1个不同hash。结合M-003逐目标真实初始化链，风险适用五目标；并未用相似百分比代替行为追踪。具体每文件hash可由before-seal复查。

## 第一轮风险排序（合并前；不是修复授权）

| 级别 | 条目 | 直接影响与判断范围 |
|---|---|---|
| P1 | P-012 | 两个1.x鱿鱼控制器把身体俯仰角用于触手周期公式；目标MC依赖源码已证两字段不同 |
| P1 | R-001/R-008 | 本地KUMO退化/非法帧输入可非终止或越界；上游继承，输入隔离未覆盖 |
| P1 | R-003/P-003 | 坏包metadata、坏服务器配置越过各自恢复catch；既有本地读链的容错缺口 |
| P1候选 | P-008/P-009 | 游泳中心俯仰与剑轨坐标链/拒绝mutation后原版root保留不足；静态链有差异，画面待验 |
| P1条件性 | R-010/P-006 | 扩展renderer/披风抛异常时缺少finally配对恢复；正常渲染不据此判失败 |
| P2 | P-005 | 两个1.x远端连段与26.x观测攻击序列不同，原因不足；睡眠缓存恢复为继承风险 |
| P2 | R-002/R-004 | 当前新增Procedural blend、legacy语法分支不闭合 |
| P2 | R-005/R-006 | Addon早注册与失败登记限制；默认Addon可达不证明异常隔离 |
| P2 | M-009 | 1.20.1/1.21.1 metadata指icon.png；authored资源仅logo512且未找到生成icon路径，静态资源指向缺口 |
| P2覆盖差异 | M-010 | 26.x默认只走延迟Tier1与vanilla fallback；不能承诺与1.x generic模型/provider/Tier2覆盖相同 |

没有本轮已验证的P0。风险不等于上游能力确认缺失；删除/新增/继承缺陷/未知均独立标识。

### MB-P-011 — 盔甲与刷新跨目标闭合

主代理已回读当前Fabric MoBendsRenderState:27,83-127,163-198、ModelFeatureRendererMixin:17-45、两个NeoForge RenderingEventHandler:329-373（后两文件hash一致）、ArmorRenderingFacade:62-114、TierClassifier:108-140以及U的ForgeHook/ArmorWrapper入口，并与B补查结果交叉核对：

- 1.x当前层替换→provider→Facade/两Tier路径与26.x的RenderState→Tier1.renderDeferred/vanilla fallback不同；26.x非Humanoid或full-model slot无法由空几何模式识别，确实可以走原版回退；没有证据说明所有外部盔甲都会坏，也没有证据说明两路覆盖相等。
- 26.x的Tier2/Facade类存在但没有从默认model提交链来的调用，不计为默认已接入的能力；历史注释百分比也不是支持覆盖统计。范围/精确每Target入口见M-010。
- 正常before/after配对不等于异常finally保证：两个NeoForge active事件中Addon beforeRender/afterRender可抛异常，pop在普通顺序之后；R-010主回源确认。Fabric:80,95,176-178亦为普通push/callback/pop形状，用户未授权运行异常fixture，本轮仅记录静态恢复缺口。
- 资源reload矩阵由B逐目标扫描：Fabric/两个Neo26.x有真实listener；Forge1.20.1/Neo1.21.1只找到refreshSystems转发但无active资源reload注册。U的reload主要处理在线AssetsModule，故不能直接说当前两个1.x漏掉了原版内置狼动画热重载；可以确认当前五目标自动刷新接线不对等。

### MB-P-012 — 两个1.x鱿鱼触手的输入映射缺陷（独立依赖源码补证）

- A的U-027-E发现差异后，主任务回读 U `standard/animation/controller/SquidController.java:26-51` 的 `/PI`、`<PI`、周期sin公式；当前 C(1.20.1-forge/1.21.1-neoforge) 同文件有效:80-108将输入改成 `xBodyRotO/xBodyRot`。三个26.x有效:23-51则用 `oldTentacleMovement/tentacleMovement`。不把1.x的“字段重命名”注释当权威。
- 主只读现有目标依赖源码，无构建/下载：`versions/1.20.1-forge/build/moddev/artifacts/forge-1.20.1-47.4.23-sources.jar` 内 `net/minecraft/world/entity/animal/Squid.java:30-37` 同时定义两组独立字段；:94-103 的 `tentacleMovement` 按speed递增并循环2π，:137 的 `xBodyRot` 由速度atan2乘180/π驱动身体俯仰，:151出水趋近-90度。现有 Minecraft1.21.1 immutable参考同类:27-33,100-109,143,157证明同一区别。
- **结论**：两个1.x输入不是同一触手周期的等价字段，而把角度制身体俯仰交给弧度周期公式，静态映射缺陷已确认。1.x结构/测试能存在并不证明此动画正确；三个26.x使用实际周期字段，不受这一具体映射错误影响。画面幅度/暂停程度仍未动态复现。
- 完整依赖证据及hash：`D:/Workspaces/Artifacts/MoBends/PortStatusAudit/20260910-152656/minecraft-squid-evidence.json`。1.20.1 sources JAR SHA-256 `A84ACF0AA784714AFD24EAA215E74E2B33244B56091191D885B1F8BAA45521B4`；1.21.1 `D:/Workspaces/References/minecraft-references/minecraft-1.21.1/src/net/minecraft/world/entity/animal/Squid.java` SHA-256 `3397E37C34DE8BFAF2CEA387D570330F547ABFBB41721C29CD44AFB6CA9F42F0`，来源描述见邻接REFERENCE.json。上述为已存在的研究参考，不是本轮新产品产物，也未改写Reference。
- 来源/原因：port版本字段选择；未知为何选择body字段，注释已被依赖代码反驳。应优先用后续修复授权补正确周期映射与控制器数值/实体验证；本轮不修改。

## 双向覆盖账本（U逐项归并）

这里的“闭合”是本轮明确找到实现、删减、反例或静态未知的归属，不是行为等价证明。A/B/C中较早的“待主复核”以本表和P节为最终归并入口。每项的U commit/path/symbol/line见A对应编号，当前完整源码定位见M/P/R对应编号；不以一行概述替代分项证据。

| 上游ID | 能力/当前实现入口 | 变化及本轮有限结论 | 当前证据与复核 |
|---|---|---|---|
| MB-U-001 | Mod/Core生命周期 → 各Loader main/CoreClient/ForgeCore | 重写Loader接线；五目标静态注册确认，生命周期线程/时序非等价结论 | M-001..003，P-003 |
| MB-U-002 | 默认七Bender → DefaultAddon/EntityBenderRegistry | 保留并适配；新增排除独立说明，不把注释专用变体等同无继承动画 | M-003/008，P-001 |
| MB-U-003 | Addon注册/工厂 → core/addon | 核心能力保留；早注册/异常有限；preview/editor明确删减 | M-004，R-005..007，P-010 |
| MB-U-004 | MutatedRenderer/Mutator → 1.x事件、26.x提交/消费Mixin | 渲染桥替换/适配；正常路径确认，异常恢复未完整闭合 | M-005，P-002/006/009 |
| MB-U-005 | refresh/config → ClientRuntime与Loader监听 | 整理并扩展缓存刷新；两个1.x未找到自动资源reload接线，与26.x不对等 | M-006，P-007/011 |
| MB-U-006 | EntityData/数据库/帧时间 → core/data及各render tick handler | 继承并映射现代tick；0..1帧增量/暂停路径确认，世界生命周期余项待验证 | P-007，M-003 |
| MB-U-007 | 玩家优先级/第一人称/预览 → PlayerController/PlayerMutator/Mixin | 主要动作链保留；游泳/披风主动修正；预览被删除 | P-001/002/005/006，R-007 |
| MB-U-008 | 通用双足/持物动作 → BipedActionController及bits | 保留、现代use适配；未知use不默认FOOD；组合动作和睡醒缓存未充分覆盖 | P-005，M-008 |
| MB-U-009 | 六类非玩家控制器 → standard/controller/data/mutators | 均找到真实默认注册；六类控制器逐Target对照已完成有限结论，1.x鱿鱼映射有缺陷 | P-001/012，M-003，A的MB-U-027 |
| MB-U-010 | 骨骼/数学/IK → core/client/model和lib/math | 类型/算法族迁移并替换底层MC绘制API；没有逐行证明全部数学和碰撞算法等价 | M-004/008，P-006/008；精确全库等价仍未完成 |
| MB-U-011 | 七Mutator及层交换 → standard/mutators | 分段模型路径可追；1.x即时和26.x延迟消费不同 | M-005，P-001/006/009 |
| MB-U-012 | 盔甲切片/回退 → Layer/Facade或RenderState/Tier1 | 显著替换；26.x默认链并非完整两Tier/provider路，覆盖未证明 | M-004/010，P-011；非普通编译通过可闭合 |
| MB-U-013 | 持物/披风/鞘翅/狼附件 → Layer/Mixin/PlayerMutator | 本地附件适配；支持者附件明确删减，普通wearable不受此豁免 | P-004b/006，M-005 |
| MB-U-014 | 箭/剑轨 → ArrowTrailManager/SwordTrail/各钩子 | 核心保留、26.2透明阶段新增、个性颜色移除；中心旋转风险待验 | P-004c/005/008，M-005 |
| MB-U-015 | KUMO模板/节点/条件 → core/kumo | KEYFRAME继承；DRIVER原为空壳，当前去除；PROCEDURAL为新增非等价替身 | R-001/002/008，P-010 |
| MB-U-016 | 狼资源状态机 → WolfController→wolf.json→6个实际animationKey | 资源保留与引用可追；run/test/harvest不能冒充默认已启用 | M-006，A-016/027 |
| MB-U-017 | JSON/BENDSANIM → core AnimationLoader + lib BinaryAnimationLoader | 迁移/资源读取适配；退化帧/非法数值的保护部分实现 | R-001/008/009，P-010 |
| MB-U-018 | 本地metadata/data/优先级 → PackManager/Combiner/Performer | 保留首项优先并新增跨格式文本路径；坏包隔离不完整 | R-001/003/004，P-010 |
| MB-U-019 | PublicDatabase/thumbnail/assets | 已删；public浏览上游自身未接通；其他服务可达性分开，缩略图默认调用证据有限 | P-004d/e/f，A-019 |
| MB-U-020 | 服务器三布尔策略 → 五目标transport+Mutator | 同名字段保留、协议替换；仅两字段有行为消费，不能声称三项限制均有效 | P-003（主+总协调独立反证） |
| MB-U-021 | 活动/玩家设置/支持者 → 无当前公共服务替代 | 确認删减；按子项有历史边界/间接原因，不能当所有网络功能豁免 | P-004a/b/c/d |
| MB-U-022 | config/G/F10/设置 → CoreClientConfig/ModConfig/原版式页面 | GUI重写、快捷键/手动重载移除；有本任务明确用户要求；没有要求恢复旧页面 | M-007，P-001/003 |
| MB-U-023 | 编辑器/Addon GUI/占位UI → 当前无对应旧入口 | 已删减/上游不可达两类分开；editor/preview历史明确不属当前面 | P-004g，R-007，A-023 |
| MB-U-024 | 运行资源/美术/语言 → 每目标resources | 默认狼与必要纹理保留；旧GUI图替换为原版控件；1.x icon指向缺口 | M-006/009，P-006 |
| MB-U-025 | 1.12 Forge/Kotlin/Shadow → 原生各Loader/JDK/Java库 | 构建/版本适配；Java/lib重组无更换GeckoLib证据；不执行构建证明 | M-001..004/008 |
| MB-U-026 | 原始少量测试 → 每目标src/test | 新增断言而非上游功能；本轮仅静读保护范围，不使用历史PASS代本轮 | M-007，P-002/006/010 |
| MB-U-027（补充） | U-009的六非玩家状态/资源细化，不另计新能力 | Zombie/Skeleton/Pig主体优先级有限对应；Spider climb API待证；Wolf图对应但baby/tail适配；Squid1.x输入缺陷 | A-027-A..G；主P-012闭合原先鱿鱼“可疑”项 |

### 当前反向新增清单

| 当前新增/替换能力 | 来源判断与理由 | 证据/边界 |
|---|---|---|
| 五Loader/版本API、RenderState延迟模型消费、平台provider | 目标MC/Loader适配；原版1.12没有这些接口 | M-001..005；具体动画输出不由API存在证成 |
| 现代游泳视觉态/根所有权/身体中心俯仰 | 本任务用户明确要求，主动动作适配 | P-002/008/009 |
| 弩/三叉戟/长矛/望远镜/角/刷子原版use姿态 | 当前版本新物品兼容；本任务历史明确委托决策 | P-005；不对未识别第三方use一概承诺 |
| Legacy文本解析、本地目录扫描与ZIP文件导入、watcher、跨格式优先级 | 比U锁定JSON KUMO能力扩展；历史本地包兼容任务的产物 | R-004、M-007/008；不是任意目录包或直接运行ZIP；不能把老年份文件名当U parser语义 |
| PROCEDURAL/表达式求值 | 当前额外层；不是已完成DRIVER的直接迁移 | R-002、C表达式检查；未完整建立早期来源提交的逐类归因 |
| 原版式配置、Mod Menu入口、四输入框tooltip、移除G/F10与手动刷新 | 本任务明确用户交互设计变更 | M-007；Mod Menu仅既有Fabric集成，CSL测试安装不自动增加产品兼容目标 |
| 盔甲Tier1/Tier2/模型捕获、幼年几何与同步策略 | 对旧wrapper的大幅实现替换/MC适配 | M-010追加；所有模组模型等价未确认 |
| 八份JSON本地化、现代资源metadata/ServiceLoader | 资源格式与维护扩展 | M-006/009；键集合测试不是翻译质量或UI测量 |
| 更多JVM断言和本机验收资产 | port新增保障层，不是可见玩法本身 | 只引用快照绑定的历史记录；本轮测试执行=0 |

## 后续验证任务（本轮未执行）

所有任务均需新的实施/运行授权；不得为了审计复现而在用户世界安装坏包或改物理状态。

| 任务 | 前置 | 观察点/判定 |
|---|---|---|
| DYN-01 游泳与轨迹坐标链 | 各目标独立测试世界，非零视角俯仰，玩家Bender启用 | 站→游→攻击→退出，剑、身体、剑轨同坐标；无双根旋转/旧姿态；关闭Bender完整原版 |
| DYN-02 披风/皮肤/装备 | wide/slim真实皮肤+披风，站立/行走/潜行/鞘翅 | 肩部锚点、UV正反、身体遮挡、层恢复；无披风不触发替代错误；每版本独立记录 |
| DYN-03 非玩家六Bender | 对应MC可生成实体与适合状态场景 | A补充矩阵中的状态优先级、狼资源转换/幼年、蜘蛛爬墙与死亡、鱿鱼水陆等 |
| DYN-04 多人/连段/策略 | 两个独立客户端；有/无mod服务端协商场景，不创建未经授权的产品server角色 | 1.x与26.x远端连段、无mod连接、策略到达/重连默认、包禁用；不把本地对象setter测试当握手PASS |
| CHECK-05 包输入及生命周期 | 后续单独受控测试夹具，不是当前instance | 退化bones/帧/start/speed、坏meta、未知资源/条件、错误包不拖垮其他包；非终止反例需超时隔离 |
| CHECK-06 Addon时序/失败 | 针对当前正式API的独立fixture | Core前后注册、抛异常、回调隔离、重复对象/键；不要求恢复已排除的Previewer |
| DYN-07 盔甲矩阵 | 每目标原版多材质/trim/glint/幼年及已授权wearable fixture | 切片、可见性、slot推断、回退和异常后模型/PoseStack无污染；当前active Tier边界见M-010 |
| CHECK-08 资源重载/产物 | 后续构建与客户端授权 | Mixin真实命中、1.x icon、各Target reload监听、resource展开/包内容；历史JAR不作本轮产物 |

## 两个独立结论

### 本轮静态覆盖范围

已经交付上游26项能力/子系统清单（U-001..026）、六非玩家逐Target补充（U-027-A..G）、五目标原生输入/Loader/事件/Mixin/资源链（M-001..010）、十项反例/范围记录（R-001..010）、十二项主任务独立复核（P-001..012）。编号数量不是独立bug数或完成百分比，部分记录审计的是同一能力不同保障层。

源码对应、注册可达、资源引用/选择、历史决策证据已分开；主任务复核包括动画优先级、泳姿根/中心变换、披风、剑/箭轨迹、服务端策略/逐项HTTP删减、KUMO死循环和异常、Addon边界、两个1.x鱿鱼字段及五目标盔甲实际入口。A/B/C初稿中错误的继承/首项优先/生命周期/权限判断已回源修正，不将初稿直接当最终结论。

**确实未完成的静态深度**：

1. 未逐一穷尽所有生产类方法、所有数学/IK/骨骼切片算法与全部当前测试方法；现有断言只按能力入口/关键反例读取和分类，不能宣称“全部测试均逐方法审查”。
2. 未完整审计任意自定义Addon重复key/trigger注册组合、所有pack重名/孤儿文件及各异常组合；已有已查反例不覆盖其余输入。
3. 未对1.12与五个现代MC的所有映射/API语义逐项回读MC实现；本轮特意闭合了鱿鱼字段问题，Spider climbing谓词、Wolf baby/tail等保留明确未知。
4. 未逐文件为全部lib类建立历史提交溯源证明；其数学/flux/keyframe来源为有路径与接口依据的迁移，不是全库行为等价。
5. 未重建/解包本轮产物、验证真实Mixin命中或资源reload；未执行任何构建/产品测试/动态验收。

因此本轮覆盖的是**有证据、有反例、有目标边界的移植现状研究**，而非以阅读数量包装“所有功能完整移植”。上述未查项已明确列出，不用“未发现问题”代替检查。

### 移植功能完整性

**不能认定已完整或五目标行为等价。** 七类默认Bender、主要动画/本地包/配置与连接内策略可以追到当前入口，但：

- 两个1.x鱿鱼周期字段存在已证静态映射缺陷；
- 本地包仍继承退化帧/循环和异常隔离缺口；现代Procedural/Legacy扩展有自身边界；
- 盔甲默认分派、远端连段、自动reload和部分帧变换有实质跨目标差异，不能由Fabric历史验收覆盖；
- 支持者/远程饰品/编辑器等属于已记录删减，PublicDatabase等还须区分上游本就未接通；它们不是“全功能等价”，也不自动成为恢复需求。

本轮没有证明“新遗漏的整项内置Bender为零”或“FUNCTIONAL_CAPABILITY_LOSS=0”。类/资源保留、历史构建成功和本次静态清单完整程度都不足以得出这种结论。

## 建议与恢复下一步

- **应优先请求后续修复授权**：P-012鱿鱼字段、R-001/008退化动画周期/帧边界、R-003与P-003的格式异常隔离；随后按明确语义处理R-002/004及render异常恢复。这是建议，不是本轮已修改。
- **应由用户决定或补场景**：1.x/26.x远端连段统一策略、现代盔甲支持的模型/provider范围、Addon允许的注册时机/失败重试保证、历史删减是否继续作为当前产品范围。无证据不能把某项取舍写成用户已接受。
- **无证据继续改的部分**：不恢复公共HTTP/支持者/Previewer空壳，不删除未被默认引用的历史资源，不因当前审计发现未用类就清理源码；不修改Target状态、技术栈或第三方模组。
- **恢复入口**：先读本页覆盖表与P-012，再按DYN/CHECK任务选择经授权的验证。三名Luna本轮已交付并停止（A额外非玩家、B额外盔甲已完成）；无需重做基线提取、代理创建或旧迁移任务。

## 结束封存与写入边界

- 结束内容清单：`D:/Workspaces/Artifacts/MoBends/PortStatusAudit/20260910-152656/after-seal.json`，SHA-256 `30895EF5EC4C94AFF073A9F3BD645F6EC086CBE01FD942D1545341FF09FCC37D`；`content-drift.json` 为 `[]`。
- 2784个审计前存在且纳入选择范围的文件，开始/结束内容SHA-256全部一致；完整index相同，HEAD/branch相同，暂存0。结束清单排除五个本轮audit-port文档本身，避免将记录追加误当产品漂移；这些文件在开场均不存在。运行实例/缓存/私密本机配置从始至终不属于所声称的内容封存范围。
- 开始6 M / 583 D / 2786 untracked；结束6 M / 583 D / 2791 untracked。唯一新增状态为本线程四份审计文件和总协调独占的 `audit-port-coordination.md`；后者本线程未编辑。旧整改全部保持原状，`git diff --check`通过。
- 本线程写入：`documents/development/audit-port-status.md`、`audit-port-upstream.md`、`audit-port-mapping.md`、`audit-port-review.md`；仓库外为本轮内容清单/采集脚本、Git archive参考归档及鱿鱼依赖证据。原始U源码独立参考副本为从指定Git对象导出的只读研究材料，没有改写port实现。
- 构建=0，产品测试执行=0，游戏/GUI=0，stage=0，commit=0，amend=0，push=0；没有改远程、分支、原有数据、AGENTS、依赖或运行配置。历史validation结果只作绑定Update/迁移快照的辅助，不转为本轮PASS。
