# MoBends 旧 port 上游能力账本（MB-U）

状态：进行中。本文只记录锁定上游基线自身的能力、注册链、调用关系、资源和依赖，不宣称当前 port 等价；不以运行 PASS、构建 PASS 或文件同名作为结论。

## 1. 基线与证据边界

- 上游基线：`0508765b3a9066d0eb8124fbe771a0ca4600f637`，即唯一 Initial port `92c5960f9ab4749f9a243d8f4708059c2b706a7a` 的父提交，版本 `v1.2.2 Forge 1.12.2`。本文所有上游符号均按此提交解释；不追踪更晚上游。
- 独立只读树：`D:/Workspaces/References/MoBends/Upstream/0508765b3a9066d0eb8124fbe771a0ca4600f637`。本轮未 checkout、构建、启动游戏或改写该树。
- 上游快照规模（静态）：`rg --files` 计 554 个文件；Java/Kotlin 在 `src/main`，Forge 1.12.2 入口在 `goblinbob.mobends`；资源在 `src/main/resources`，作者素材在 `misc/`。
- 当前 port 真实进度（仅工作树事实）：分支 `stonecutter`，HEAD `01d5cff03a188ee3bf0366d4e2c17ed0ff1e85a3`（Update）；五个 `versions/<target>/src/main` 均存在且各计 508 个路径，工作树仍有历史迁移删改/未跟踪变化，暂未构建或运行。本账本不把这些事实解释为等价。
- 文档状态：创建前确认 `documents/development/audit-port-upstream.md` 不存在；本文件是本轮唯一新增写入目标。不得用本文件覆盖其他代理的审计文档。

## 2. 判定词

`静态对应已确认` 只表示在源代码中找到可追溯接口/调用/资源引用；`定位但接入未确认` 表示声明或实现存在但没有从入口走通；`空壳/不可达` 表示注册被注释、调用链断开或仅有占位；`未知` 表示当前上游静态材料不足。资源存在不等于加载、打包或用户可见效果。

## 3. 第一批：入口、模块、Bender 注册与渲染调用链

### MB-U-001 — Forge mod 生命周期与 Core 分层

- 上游证据：`src/main/java/goblinbob/mobends/standard/main/MoBends.java:21-66` `MoBends`、`preInit/init/postInit/refreshSystems`；`src/main/java/goblinbob/mobends/core/Core.java:19-91` `Core`、`preInit/init/postInit/registerModule/refreshModules/createAsClient/createAsServer/saveConfiguration`；`src/main/java/goblinbob/mobends/core/CoreClient.java:21-79` `CoreClient` 构造器和三阶段生命周期；`src/main/java/goblinbob/mobends/core/CoreServer.java:8-33` `CoreServer`。
- 注册/调用：Forge `@Mod(modid = ModStatics.MODID)` 入口在 `MoBends`; `preInit` 先 `proxy.createCore()`，再 `Core.getInstance().preInit(event)`，再代理预初始化；客户端 Core 构造时注册 `EnvironmentModule.Factory`、`ConnectionManager.Factory`、`AssetsModule.Factory`、`SupporterContent.Factory`；客户端 init 初始化 `PackManager`、按键并注册五个 Forge event handler 以及资源 reload listener；postInit 应用 `CoreClientConfig` 到 `EntityBenderRegistry`。
- 副作用/用户行为：创建全局 Core、SimpleNetworkWrapper、配置、包管理器、事件监听、资产重载；`refreshSystems()` 刷新实体数据库/Bender mutator/模块。服务端 Core 仍注册配置同步网络通道，但没有客户端渲染。
- 证据等级：明确。当前 port 的目标版本/loader 入口与上游 Forge 事件模型不同，是否保持同一时序需由其他账本复核。

### MB-U-002 — 默认 Addon 及七类实体 Bender

- 上游证据：`src/main/java/goblinbob/mobends/standard/client/ClientProxy.java:16-41` `ClientProxy.preInit/init/createCore`；`src/main/java/goblinbob/mobends/standard/DefaultAddon.java:23-106` `DefaultAddon.registerContent/onRenderTick/onClientTick/onRefresh`；`src/main/java/goblinbob/mobends/core/addon/AddonAnimationRegistry.java:18-127` 全部 `registerNewEntity/registerEntity/registerTriggerCondition/registerAnimationEditor`；`src/main/java/goblinbob/mobends/core/bender/EntityBenderRegistry.java:13-109` 注册、按类/实体查找、缓存、配置应用、刷新。
- 注册事实：`ClientProxy.init()` 调用 `AddonHelper.registerAddon(ModStatics.MODID, new DefaultAddon())`。`DefaultAddon.registerContent` 实际注册七个 Bender：`PlayerBender`、Zombie、Skeleton、PigZombie、Spider、Squid、Wolf；另有 ZombieVillager 和 Husk/WitherSkeleton/Stray 等专用注册调用被注释，默认 Addon 不会为这些类建立独立 Bender（继承兜底另见 MB-U-009）。默认 Addon 还注册 `wolf_state` 条件（82 行）以及 render/client tick/reload 回调。
- 调用/副作用：`AddonAnimationRegistry.registerNewEntity` 创建 `DefaultEntityBender` 并交给 `EntityBenderRegistry.instance.registerBender`；`EntityRenderHandler.beforeLivingRender`（`core/client/event/EntityRenderHandler.java:15-42`）按实体取 Bender，先 pushMatrix；动画启用时调用 `applyMutation`、Mutator 和 `MutatedRenderer.beforeRender`，动画关闭时在 Pre 分支调用 `deapplyMutation`。Post 事件（44-55）只调用 `afterRender` 并 popMatrix，不负责 demutate。配置关闭实体会在 `applyConfiguration`（31-37）设置 `animate`，实体映射还会缓存（64-99）。用户可见为对应实体的模型动画与变形；未证明所有 Bender 在当前 port 五目标均行为一致。
- 关键模型/Mutator：见 MB-U-010 至 MB-U-016；此处保留注册入口，避免把实现文件名误当成接入证据。

### MB-U-003 — Addon 公共扩展面

- 上游证据：`src/main/java/goblinbob/mobends/core/addon/IAddon.java:1-17`；`AddonHelper.java:1-16`；`Addons.java:1-62`；`AddonAnimationRegistry.java:18-127`。
- 能力：外部 mod 以 `AddonHelper.registerAddon(modId, IAddon)` 注册；Addon 可通过 `registerNewEntity` 提供实体类、数据工厂、Mutator 工厂、`MutatedRenderer`、可选预览器和可调部件，通过 `registerTriggerCondition` 接入 Kumo 条件，通过 `registerAnimationEditor` 接入编辑器。注册后的 Bender 由 `EntityBender.java:58` 以 `modId + "-" + key` 形成稳定键并加入全局表（不是 namespace:key）。
- 副作用：Addon 注册会改变实体渲染查找和设置页面列表；重复 key/类的确切覆盖策略需结合 `Addons` 实现进一步复核，当前不推断为安全合并。
- 证据等级：明确（接口和默认调用均存在）；第三方 Addon 兼容性动态未知。

### MB-U-004 — 通用模型变异渲染桥

- 上游证据：`src/main/java/goblinbob/mobends/core/client/MutatedRenderer.java:11-100` `beforeRender/afterRender/renderLocalAccessories/transformLocally/interpolateRotation`；`src/main/java/goblinbob/mobends/core/bender/EntityBender.java:1-170`；`src/main/java/goblinbob/mobends/core/mutators/Mutator.java:1-120`；`src/main/java/goblinbob/mobends/core/data/EntityData.java:1-180`。
- 调用：Pre render 中由 `EntityRenderHandler` pushMatrix，动画启用时调用 Bender/Mutator/MutatedRenderer 的局部变换与附件；动画关闭时在同一 Pre 事件 deapplyMutation；Post 事件调用 afterRender 并 popMatrix。Bender 负责 renderer 适配、数据实例、mutator 选择和 `animate` 开关。
- 用户可见行为：实体本体姿态、局部旋转、附属装备/披风/轨迹发生动画化；GL 状态恢复失败会影响后续渲染，这是高风险副作用。
- 证据等级：明确；当前只静态审计，未验证运行时状态隔离。

### MB-U-005 — 全局刷新/配置影响面

- 上游证据：`standard/main/MoBends.java:55-66` `refreshSystems()`；`core/bender/EntityBenderRegistry.java:31-37,87-109` `applyConfiguration/clearCache/refreshMutators`；`core/data/EntityDatabase.java` `refresh`；`core/Core.java:54-67` `refreshModules`。
- 语义：配置变化时刷新实体数据库、清除 Bender 缓存、重新建立 Mutator，并通知模块；这不是仅保存文件的副作用。具体 GUI 入口见 MB-U-040。
- 证据等级：明确；配置监听是否在当前目标的同一时机触发需另审。

## 4. 第一批当前 port 对照钩子（仅用于后续复核，不是等价结论）

| MB-U | 上游能力 | 当前 port 静态对照线索 | 本文结论 |
|---|---|---|---|
| 001 | Forge Mod/Core lifecycle | `versions/<target>/src/main` 各自入口、platform/loader 包 | 定位对应但时序/副作用未确认 |
| 002 | 七 Bender + DefaultAddon registration | `standard/DefaultAddon.java` 与各 `standard/mutators`, `standard/client/renderer` | 文件/符号对应可见；注册和渲染行为未在本账本宣称 |
| 003 | Addon API | `core/addon/*`, `api/*` | 接口族存在；跨版本 ABI/第三方行为未知 |
| 004 | MutatedRenderer bridge | `core/client/MutatedRenderer.java`, loader renderer adapters | 定位对应；未运行 |
| 005 | refresh/config path | `Core*Config`, `MoBends.refreshSystems`, GUI/config code | 定位对应；触发时序未知 |

## 5. 待续覆盖范围（中断恢复点）

已先落盘基线、真实工作树进度、入口链、七 Bender 注册、Addon API、渲染桥和配置刷新第一批。剩余必须从上游调用链继续补齐并稳定分配 `MB-U-*`：

1. 七类 Bender 的数据/Mutator/动画控制器/关键动作：步行、冲刺、跳跃、游泳、梯子、骑乘、潜行、攻击、吃东西、弓/剑/工具、盾牌、火把、睡眠、飞行/Elytra/披风、蜘蛛/僵尸/猪僵尸/鱿鱼/狼等。
2. 物品、盔甲、披风、Elytra、第一人称边界、箭/剑轨迹及真实 renderer layer 引用。
3. Bends 模型、矩阵/四元数/平滑时间、Kumo 状态/条件/模板/序列化、二进制/JSON animation loader。
4. BendsPack legacy 解析、PackManager/Combiner/cache、资源目录/真实引用，和 GUI 菜单/设置/包/Addon/节点编辑器。
5. 网络与 WebAPI：Core config sync、ConnectionManager/Ping/PlayerSettings、Kumo/remote/public database/supporter 内容，分别标记明确/间接/未知及副作用。
6. 配置字段、环境开关、语言、依赖（Forge/Kotlin/Gson/Apache/Netty 等）和所有可见资源；区分实现、注释/空壳、未注册、不可达。
7. 以 commit/path:symbol:line 补齐全部条目，并在最终表中分别报告已覆盖、未检查和不能由静态证据证明的项目；不把静态对应写成移植完整性或动态 PASS。

## 6. 动画状态、时间和七类 Bender 的实体能力

### MB-U-006 — EntityData / LivingEntityData 的时间与姿态数据面

- 上游证据：src/main/java/goblinbob/mobends/core/data/EntityData.java:23-306 EntityData；LivingEntityData.java:13-252 LivingEntityData；EntityDatabase.java:14-108 EntityDatabase；src/main/java/goblinbob/mobends/core/client/event/DataUpdateHandler.java:10-68 updateAnimations/onClientTick。
- 能力：每个实体按 entity id 保存 LivingEntityData；RenderTick 记录 partialTicks、全局 ticks、每帧最多推进 1 tick 的 ticksPerFrame，再更新实体数据和 Addon 回调。数据面保留 globalOffset/localOffset/renderRotation/centerRotation、运动/地面/静止覆盖、头/肢体摆动、攻击后/空中/下落/攀爬计时和 PackAnimationState。
- 用户可见行为：动画控制器以实体运动状态和插值时间驱动模型；暂停世界时不推进 RenderTick 动画，ClientTick 仍更新客户端侧数据。EntityDatabase.updateClient（69-91）按 PreviewHelper 豁免预览实体后清理不在当前 world/实体对象已替换的数据；EntityDatabase.refresh（99-102）本身直接清空全表，不能描述为“清理非预览数据”。
- 证据等级：明确。具体 timing 数值和动作边界来自源代码；未做动态帧率/暂停验证。

### MB-U-007 — Player Bender、状态优先级和第一人称前置姿态

- 上游证据：standard/PlayerBender.java:15-61 PlayerBender（alterable parts、PlayerData/PlayerMutator/previewer 工厂）；standard/animation/controller/PlayerController.java:26-190；standard/data/PlayerData.java:14-186；standard/client/event/RenderingEventHandler.java:12-33；standard/mutators/PlayerMutator.java:25-358。
- 状态链：披风层持续运行；睡眠优先于骑乘，骑乘再区分 living mount/非 living 坐姿；Elytra 飞行、攀爬、入水、离地（飞行/下落/冲刺跳/跳跃）、地面静止/步行/冲刺，再叠加潜行和火把层；动作控制器单独处理持物/攻击。PlayerData.onAttack 在短时间内循环 1..5 攻击段，受 performSpinAttack 和骑乘条件影响；空手在左右拳臂间切换。
- 第一人称：RenderingEventHandler.beforeHandRender(RenderHandEvent) 通过 BenderHelper 获取当前 PlayerMutator，并在实体启用动画时调用 PlayerMutator.poseForFirstPersonView()；这是明确的 RenderHand hook，不能仅凭第三人称渲染对应推断为 vanilla first-person。
- 用户可见行为：玩家第三人称身体、手持物、披风、Elytra 与第一人称手部会按上述状态变形；PlayerPreviewer 在 GUI 预览中暂时覆盖飞行/地面/静止/摆动值并在结束恢复。PlayerPreviewer 静态生命周期见 standard/previewer/PlayerPreviewer.java:9-97。
- 风险：PlayerMutator 在 mutate/demutate/swapLayer/deswapLayer:58-156 增删自定义披风/Elytra/配件 layer，并保留 small-arms/装饰模型；状态覆盖和真实 renderer layer 时序需动态复核。

### MB-U-008 — 通用 Biped 动画与物品/攻击分类

- 上游证据：standard/animation/controller/SkeletonController.java:28-79、PigZombieController.java:25-77、standard/animation/bit/biped/item/BipedActionController.java:23-174；standard/UseActionType.java:3-13；standard/AttackActionType.java:3-11。
- 通用状态：Stand/Walk/Jump/Sprint/Sneak/Swimming/Riding/Sitting/Falling/LadderClimb/TorchHolding/Shield/Eating 等 AnimationBit 以动作 key 写入 HardAnimationLayer；动作层依据主副手、活跃物品和 vanilla arm pose 选择 UseAction（FOOD/BOW/SHIELD 等），攻击选择 FISTS/SWORD/TOOL，再创建 BowAction、PunchingAction、SwordAction、ToolAction。初始化时缺少 action map 项会主动抛 IllegalStateException（BipedActionController:30-53），而不是静默 fallback。
- 关键姿态：SwordAction 组合攻击站姿、冲刺站姿及 slash-down/inward/outward/up/whirl；PunchingAction 组合 guard 与左右拳；BowAction 依主手；ToolAction 采用工具挥动；盾牌/火把/吃东西分别覆盖上肢。
- 用户可见行为：持剑、工具、弓、盾、食物、空手的手臂和身体动作不同；Spin Attack 是可配置分支，不是所有攻击默认等价。
- 证据等级：明确。外部物品可通过 ModConfig.itemUseClassifications/itemAttackClassifications 用 namespace:path 通配规则重分类；未证明第三方物品实际动作兼容。

### MB-U-009 — 其余六类默认实体控制器（含空壳/不可达边界）

- Zombie：standard/animation/controller/ZombieController.java:24-69 注册 Stand/Walk/Jump 与 ZombieLeanAnimationBit、ZombieStumblingAnimationBit；ZombieDataBase.java:10-49 以 entity id 派生两个 animation set，并每 80–99 tick 随机切换 walk state。ZombieVillager 同构于 ZombieVillagerController.java:24-69，但默认 Addon 的专用注册调用在 DefaultAddon.java:35-40 被注释；Registry.getForEntity（EntityBenderRegistry.java:64-86）还会按 entityClass.isInstance(entity) 做继承兜底，所以“专用注册不可达”不等同于该实体完全没有可能继承 Zombie Bender，实际取决于 Minecraft 类继承关系。
- PigZombie：PigZombieController.java:25-77 基础步态加 AttackSlashInwardAnimationBit，数据/Mutator 在 PigZombieData.java:6-25 与 PigZombieMutator.java:10-49；默认已注册。
- Skeleton：SkeletonController.java:28-79 基础 Stand/Walk/Jump + BipedActionController；SkeletonMutator.java:20-157 按 bone limbs 建模。默认已注册。
- Spider：SpiderController.java:22-82 Idle/Move/Jump/Death/Crawl 五状态；SpiderData.java:15-360 保存头/颈/身、八条 Limb 的世界/局部位置和 IK；SpiderMutator.java:13-140 建八条上下肢。墙面方向只在 calcWallFacing 检测 ladder/邻块，用户可见为爬墙/落地蜘蛛姿态。
- Squid：SquidController.java:18-55 直接计算 body/tentacle 摇摆，但 perform 返回 null；SquidData.java:8-76 创建 8×9 tentacle sections；SquidMutator.java:14-108 替换 vanilla 模型。能力是明确接入的模型变形，但动作 key 集合为空/未返回，属于“几何更新已实现、动作集合未知”，不应强行归类为标准 Bit 链。
- Wolf：WolfController.java:26-99 通过 Kumo animator、AnimationLoader.loadFromPath 载入动画；WolfData.java:9-121 映射 head/body/legs/tail/mane/nose/mouth/tongue/ears；WolfMutator.java:20-254 创建自定义模型和 LayerWolfMisc。Wolf controller perform 返回 null，行为主要来自 Kumo/资源状态机；默认已注册。其资源和状态机见 MB-U-016。
- 默认 Addon 注册清单的精确入口：standard/DefaultAddon.java:26-82；注释掉的调用只能证明专用 Bender 注册不可达；Husk/WitherSkeleton/Stray 等是否经 EntityBenderRegistry.getForEntity 继承兜底命中，需按对应 Minecraft 类继承关系复核，不能统称为完全无动画。

### MB-U-010 — 模型变换、层级骨骼和数学接口

- 上游证据：core/client/model/IModelPart.java:7-94；ModelPartTransform.java:15-162；ModelPart.java:26-420；ModelPartExtended.java:10-100；ModelPartPostOffset.java:8-52；core/math/Quaternion.java:3-116；SmoothOrientation.java:5-266；SmoothVector3f.java:5-299；TransformUtils.java:11-215。
- 能力：IModelPart 分离 pre/global、local/parent、character/all-parents、post/children 变换及 renderPart/renderJustPart/update/setVisible；ModelPart 将 vanilla ModelRenderer 替换为带 MutatedBox、parent、extension、AABB collider 的自定义几何；ModelPartExtended 把前臂/小腿 extension 接到 parent 后置变换，ModelPartPostOffset 为 held-item/armor 等 postRender 提供偏移。
- 数学：四元数轴角/乘法/归一化；SmoothOrientation 通过 start/end/smooth/progress 插值并支持 global/local/instant rotate；SmoothVector3f 为全局/局部位移提供逐轴滑动和最大距离限制；矩阵/向量/physics 包提供 3×3/4×4、平移/旋转/缩放、AABB/OBB/平面/射线相交（core/math/physics/Physics.java:9-183）。
- 用户可见行为：模型部件通过平滑四元数、父子骨骼和 GL/matrix 接口写入 position/rotation/scale/offset；本条不推断其是否覆盖每一项 vanilla yaw/pose。模型 scale/offset 等由 Bends pack 可能改变，但网络 policy 仅部分接入（见 MB-U-020）。

### MB-U-011 — Mutator 模型替换与七类 renderer 接线

- 上游证据：core/mutators/Mutator.java:21-234 mutate/demutate/updateModel/performAnimations/syncUpWithData；standard/mutators/BipedMutator.java:21-256；PlayerMutator.java:25-358；SpiderMutator.java:13-140；SquidMutator.java:14-108；WolfMutator.java:20-254；ZombieMutatorBase.java:21-42、ZombieMutator.java:9-32、ZombieVillagerMutator.java:10-48、PigZombieMutator.java:10-49、SkeletonMutator.java:20-157。
- 调用：每个 EntityBender 按 renderer 实例缓存一个 Mutator；Pre render 首次 mutate 保存 vanilla model、创建 custom parts、替换 layers，之后 updateModel -> controller.perform -> BendsPackPerformer -> syncUpWithData；动画关闭时由 EntityRenderHandler 的同一 Pre 分支调用 deapplyMutation 还原并移除缓存，正常 Post render 只执行 afterRender/popMatrix。shouldModelBeSkipped 以具体 vanilla Model 类型拒绝不匹配 renderer。
- 模型用户行为：Biped 增加前臂/前小腿可分段骨骼；Player 额外覆盖 wear layers、Cape/Elytra/Accessories；Spider 八腿 IK；Squid 8×9 tentacle chain；Wolf 头/口/耳/鬃/尾和 LayerWolfMisc；Zombie/Skeleton/PigZombie 复用 Biped。scaleFactor 传入值在 Mutator.mutate:83-104 固定为 0F，不是动态模型膨胀参数。
- 证据等级：明确接线；GL 状态、layer 索引、renderer 复用/重建的跨目标行为未知。

### MB-U-012 — 盔甲/可穿戴模型分片与 vanilla 保留规则

- 上游证据：standard/client/model/armor/ArmorModelFactory.java:9-48；ArmorWrapper.java:19-190；HumanoidPartWrapper.java:10-89；HumanoidLimbWrapper.java:14-174；PartContainer.java:17-269；standard/client/renderer/entity/layers/LayerCustomBipedArmor.java:17-84。
- 调用：LayerCustomBipedArmor.getArmorModelHook 先让 ForgeHooksClient.getArmorModel 提供建议模型，再取实体数据；除非 ModConfig.shouldKeepArmorAsVanilla(item) 且 entity 是 BipedData，才经 ArmorModelFactory.getArmorModel 返回 wrapper。ArmorWrapper 将 body/head/arms/legs 封装为 HumanoidPartWrapper/HumanoidLimbWrapper，按 cut plane 分上/下肢并同步动画 part；格式异常抛 MalformedArmorModelException 后回退建议模型。
- 用户可见行为：玩家/生物盔甲可以跟随分段骨骼、局部位移、旋转和 scale；按物品通配规则可保持 vanilla armor，异常或不符合标准的第三方盔甲回退 vanilla。该规则是“盔甲层接入已确认”，不是所有 armor model 的视觉兼容保证。

### MB-U-013 — 手持物、披风、Elytra、Wolf 附件、第一人称与配件

- Held item：standard/client/renderer/entity/layers/LayerCustomHeldItem.java:21-96 依据 EnumHandSide 选主/副手、调用 ModelBiped.postRenderArm 和 renderItemSide，使用 renderRightItemRotation/renderLeftItemRotation；BipedMutator 在 swapLayer/deswapLayer:91-140 替换 vanilla held/head/armor layers。
- Cape：BendsCapeRenderer.java:15-196 创建 16 段 slab（10×16×1），applyAnimation(PlayerData) 更新每段铰链/旋转；LayerCustomCape.java:18-72 仅在 player info、可见、穿 Cape、非 Elytra 时绑定玩家 cape texture，并按 sneaking/flying 计算变换。
- Elytra：LayerCustomElytra.java:20-81 在胸甲槽为 Elytra 时绑定玩家 Elytra、cape 或 vanilla textures/entity/elytra.png；ModelBendsElytra.java:14-96 根据速度、下俯/飞行状态写 Elytra rotation，兼容玩家 rotateElytraX/Y/Z。
- Wolf 附件：LayerWolfMisc.java:19-121 真实引用 mobends:textures/entity/wolf_misc.png，绘制 mouthBottom/mouthTop/mouthInside/tongue Mesh；WolfMutator 接入该 layer。WolfData 的 nose/mouth/tongue 等模型部件由 Kumo 资源命名。
- Player accessories：LayerPlayerAccessories.java:30-155 从 SupporterContent.getAccessories() 取 accessory details 和每玩家 settings，以 BindPoint（head/body/四肢/前臂/大腿/小腿，core/supporters/BindPoint.java:14-70）应用模型变换，且 renderAccessory:66-72 明确跳过 !settings.isUnlocked() 或 settings.isHidden() 的配件，再绑定 diffuse/inked texture 和 baked model；这是网络 supporter 功能，不是本地静态资产。
- 第一人称接线见 MB-U-007；其余动作/附件并未证明当前 port 的同一 layer 顺序。

### MB-U-014 — 箭轨迹与剑轨迹

- 箭：ArrowTrailManager.java:7-47 按 EntityArrow 维护 trail；DefaultAddon.onRenderTick:86-90 在 ModConfig.showArrowTrails 为真时推进 ArrowTrailManager.onRenderTick，RenderBendsArrow.doRender:18-25 同一开关下先调用 renderTrail 再 super.doRender。ArrowTrail.java:16-215 固定 MAX_LENGTH=10、SPAWN_INTERVAL=1，TrailNode 由箭 forward/up 计算 right，并沿 up/right 生成交叉条带（不是无条件面向相机的普通 billboard）；Tipped/Spectral renderer 在各自文件 9-31，由 ClientProxy.preInit:20-23 注册。
- 剑：BipedEntityData.java:10-111 为每个 BipedData 创建 SwordTrail(() -> SupporterContent.getTrailColorFor(entity))；SwordTrail.java:21-195 按持手 arm/foreArm/body/renderRotation/itemRotation 采样 TrailPart，20 tick 后删除；BipedRenderer.renderLocalAccessories:11-35 在 ModConfig.showSwordTrail 为真时绘制。颜色来自 supporter API，缺失时默认白色。
- 用户可见行为：箭在世界中留下有限长度、随箭 forward/up 姿态生成的交叉带；剑挥动绘制带颜色拖尾。配置/网络/renderer 接线存在；未做动态性能和边界验证。

## 7. Kumo、动画资源和 BendsPack

### MB-U-015 — Kumo 状态机和条件/节点扩展接口

- 上游证据：core/kumo/KumoSerializer.java:12-52；state/KumoAnimatorState.java:11-51；state/ILayerState.java:11-37；state/KeyframeLayerState.java:21-242；state/DriverLayerState.java:5-20；state/ConnectionState.java:10-48；state/keyframe/KeyframeNodeRegistry.java:15-77；state/keyframe/StandardKeyframeNode.java:17-131；MovementKeyframeNode.java:18-117。
- 语义：Gson 通过 type adapter 反序列化 LayerTemplate、KeyframeNodeTemplate、TriggerConditionTemplate；AnimatorTemplate 的每层实例化为 KEYFRAME 或 DRIVER；KeyframeLayer 以 entry node 运行节点，按 ConnectionState 的 condition、transitionDuration、Easing 在 current/previous node 间过渡；Standard 节点可 looping，Movement 节点绑定运动型播放。未知 layer/node/condition type 会 warning/抛 MalformedKumoTemplateException，不是静默兼容。
- 条件注册：TriggerConditionRegistry 构造内置 core:or/and/not/state/ticks_passed/equipment_name/animation_finished（state/condition/TriggerConditionRegistry.java:12-100）；Addon 可通过 MB-U-003 注册 mod-owned condition。StateCondition 观察 on-ground/sprinting/stillness，EquipmentNameCondition 用 display name regex 匹配槽位，TicksPassedCondition 依 DataUpdateHandler ticks。
- 变量：KumoVariableRegistry.java:11-45 提供 ticks/random/ticksAfterPunch/health；provideTemporaryData 是每次 Mutator perform 前的数据上下文。用户可见为资源化状态切换、身体部位 mask、过渡动画。
- 已知空壳/边界：DriverLayerState.update 当前无具体行为；MovementKeyframeNode.isAnimationFinished:78-81 恒返回 false，因此依 animation_finished 条件的 Movement 节点不能以静态证据证明完成转换；这是上游自身语义风险。

### MB-U-016 — Wolf Kumo 资源实际引用与自定义条件

- 上游证据：standard/animation/controller/WolfController.java:26-99 的 WOLF_ANIMATOR = mobends:bends/animators/wolf.json 和 AnimationLoader.loadFromPath；standard/kumo/WolfStateCondition.java:15-69；资源 src/main/resources/assets/mobends/bends/animators/wolf.json:1-153。
- 真实资源链：wolf.json 第一层 KEYFRAME 节点实际引用 wolf_idle.bendsanim、wolf_walking.bendsanim、wolf_sitting_down.bendsanim、wolf_sitting.bendsanim、wolf_standing_up.bendsanim，状态条件为 core:state MOVING_HORIZONTALLY/STANDING_STILL、mobends:wolf_state SITTING、core:animation_finished；第二 additive 层仅 mask tongue/mouth，在 core:ticks_passed 80/50 tick 间切换 wolf_idle/wolf_breathing。
- 资源目录还含 wolf_run.bendsanim、wolf_test.bendsanim 与 player_anim_harvest.json。在 wolf.json 当前没有引用 wolf_run、wolf_test、player harvest；它们是存在但未从默认 animator 接通的资源，不能算用户默认行为。
- 自定义 WolfStateCondition 读取 EntityWolf sitting/begging/angry 等状态（具体 enum 在 60 行起），由 DefaultAddon.registerTriggerCondition("wolf_state",...) 注册。静态上游链完整；动态加载/二进制解码未验证。

### MB-U-017 — Keyframe/BENDSANIM loader 和真实格式

- 上游证据：core/animation/keyframe/AnimationLoader.java:12-95；BinaryAnimationLoader.java:13-107（HEADER/flags/骨骼读取）；KeyframeAnimation.java:5-23、Bone.java:5-8、Keyframe.java:3-18；core/animation/bit/KeyframeAnimationBit.java:12-161。
- AnimationLoader 支持 File、JSON string、Minecraft ResourceLocation，以及含 modid:path 的 ambiguous path；资源扩展名/头部决定 JSON 或 BinaryAnimationLoader。Kumo/PackDataProvider 通过 AnimationLoader.loadFromPath 验证/载入 animation key。当前基线资源中 .bendsanim 是二进制 BENDSANIM，player_anim_harvest.json 是 JSON；两种格式均真实存在。
- 证据边界：没有在本轮执行解码或构建；只证明 loader 分支和资源引用。二进制格式兼容性、坏文件回退、跨版本 reader 行为未知。

### MB-U-018 — 本地 BendsPack 读取、合并、应用和 legacy 命名

- 上游证据：core/pack/PackManager.java:19-178；LocalBendsPack.java:13-89；PackDataProvider.java:17-125；PackCombiner.java:6-31；BendsPackData.java:9-21；BendsPackPerformer.java:10-34；BendsPackMeta.java:3-13；BendsPackHelper.java:3-16。
- 本地接线：PackManager.initialize 固定使用 <mcDataDir>/bendspacks，缓存 <bendspacks>/public_cache；initLocalPacks 扫描 .bendsmeta，LocalBendsPack.readFromFile 用 Gson 读取 metadata；每个 pack 的 .bends 数据在 PackDataProvider.getDataForPack 读取并经 Kumo validation；PackCombiner.combineData:22-27 以列表末尾到开头倒序 putAll，使列表中 FIRST/top pack 对同名 target/animation 具有优先权（源码注释明确 first in array take precedence），不是后应用覆盖先应用。
- 应用副作用：GUI 或 config 调 setAppliedPacks 后重建 PackDataProvider.appliedData；Mutator 每次动画在 NetworkConfiguration.areBendsPacksAllowed() 允许时调用 BendsPackPerformer.performCurrentPack，坏模板会记录警告并 PackManager.resetAppliedPacks(true)。运动限制为 global/local offset 相对上一帧最大 10F。
- legacy 结论：上游类名/后缀是 BendsPack*、.bendsmeta、.bends；本基线没有名为 LegacyBendsPackParser 的独立类，也没有从 misc/packDatabase.bpd 运行读取链。misc/funatparties.bends、misc/packDatabase.json/.bpd 是作者/历史素材，不能替代实际 runtime parser 证据。

### MB-U-019 — Public database、thumbnail、远程 pack 与在线资产：可达性分层

- 上游证据：core/pack/PublicDatabase.java:12-47 downloadPublicDatabase；ThumbnailProvider.java:9-39；PackManager.java:157-178；GuiPacksWindow.java:20-178；GuiLocalPacks.java:22-183；core/asset/AssetsModule.java:24-185；AssetReloadListener.java:17-62。
- Public pack：PublicDatabase.downloadPublicDatabase(databaseUrl) 可从任意 URL 读 JSON，PackEntry 含 name/displayName/author/description/uploaded/updated/downloadLink/thumbnail；但 rg 在 src/main/java 中未找到其调用者。GUI 的 public tab 在 GuiPacksWindow.drawScreen:137-145 仅显示 Coming soon...，所以 public database/download 在基线是“声明 API + 静态素材存在、默认不可达”。
- Thumbnail：ThumbnailProvider 能下载 URL 图片到 public_cache 并注册 mobends:bendsPackThumbnails/<packName>，且 LocalBendsPack.setThumbnailURL:41-45 真实可通过 PackManager.getThumbnailLocation 调到 ThumbnailProvider；本轮未找到默认 GUI 对 setThumbnailURL 的调用，故标记为“接口/间接可达”，不能宣称公开包下载已接入。
- 在线资产：AssetsModule 是实际 CoreClient module（MB-U-001），preInit 构造即读取本地 mobends/asset_manifest.json，请求 EnvironmentConfig.apiUrl 的 /api/asset/manifest，按 id/version 下载 models/textures 到 mobends/assets，并由 AssetReloadListener 注册 AssetTexture/AssetModels。这是明确在线请求和本地写入能力，失败时 warning/保留旧清单；本轮未发起请求。

## 8. 网络、supporter、remote 和配置

### MB-U-020 — Server network policy/config 同步

- 上游证据：core/network/NetworkConfiguration.java:9-65；SharedConfig.java:8-43；SharedProperty.java:10-55；SharedBooleanProp.java:6-36；MessageConfigRequest.java:9-39；MessageConfigResponse.java:14-63；WorldJoinHandler.java:10-24；Core.java:24-37。
- 协议：Core 在 channel mobends 注册 request id 0（client→server）和 response id 1（server→client）。玩家加入世界时先 onWorldJoin 使用最保守默认（单人允许 scaling；pack true；多人 movementLimited true），再发送 MessageConfigRequest；服务端回 SharedConfig NBT，客户端覆盖属性。
- policy：modelScalingAllowed default false、bendsPacksAllowed default true、movementLimited default true；Mutator 实际消费 areBendsPacksAllowed/isMovementLimited，但 isModelScalingAllowed 在基线全树只有声明/初始化/getter，没有下游调用，属于“协议字段有、行为接入未确认/疑似空壳”。
- 用户可见行为：服务器可禁用 BendsPack、限制 pack offset 距离；主菜单隐藏 Packs 按钮（GuiBendsMenu:64-75,194-198）当 policy false；服务器配置由 CoreServerConfig 从 [Server] category 读取。网络安全/恶意 payload 处理未动态验证。

### MB-U-021 — API activity、player settings、supporter accessories

- 上游证据：core/connection/ConnectionManager.java:16-99；PingTask.java:13-69；PlayerSettingsDownloader.java:18-84；PlayerSettingsResponse.java:7-14；core/supporters/SupporterContent.java:22-133；AccessoryDetails.java:10-34；AccessoryPart.java:8-45；AccessorySettings.java:6-34。
- 真实远程链：ConnectionManager.Factory.preInit 创建 ConnectionManager，构造即向 apiUrl + /api/activity/join POST {app:"mobends",version:...}，按响应 pingInterval 启动 daemon PingTask（apiUrl + /api/activity/ping）和 PlayerSettingsDownloader；渲染 Player Accessories 或取得 sword trail 颜色时，缓存缺失会按玩家名排队 apiUrl + /api/accessory 请求。
- supporter 内容：preInit 还向 apiUrl + /api/accessory/details GET，得到 accessory key→details（parts=modelPath/diffuse/inked texture + BindPoint）；每玩家 settings 含 unlocked/hidden/color。LayerPlayerAccessories.renderAccessory:66-72 明确跳过 !settings.isUnlocked() 或 settings.isHidden() 的配件，再渲染 parts；这是网络 supporter 功能，本轮未发起请求。
- 失败行为：无网络通常保留空 accessory/details，trail 默认白色；join 失败通过 ErrorReporter.showErrorToPlayer 报告并不启动线程。API URL 可由 mobends/env.json 覆盖（MB-U-022）。

### MB-U-022 — Environment、ModConfig、CoreClientConfig 和配置 UI

- Environment：core/env/EnvironmentModule.java:25-101 从 <config>/mobends/env.json 覆盖默认 https://mobends.com；EnvironmentConfig.java:3-13 只暴露 getApiUrl。刷新系统会重读环境并再次触发连接/资产/supporter模块。
- ModConfig：standard/main/ModConfig.java:20-249 的可见字段为 showArrowTrails、showSwordTrail、performSpinAttack、item use/attack classification arrays、keep armor/entity vanilla arrays；ConfigChangedEvent 清 cache、解析分类、调用 MoBends.refreshSystems。资源名匹配支持 namespace/path 通配；错误分类通过 ErrorReporter 告知玩家。
- CoreClientConfig：core/configuration/CoreClientConfig.java:9-63 保存 [General] AppliedPacks 与 [Animated] <bender key>；EntityBenderRegistry.applyConfiguration 在 postInit 读取动画开关。CoreServerConfig 只从 NetworkConfiguration shared properties 读 [Server] 并保存。
- GUI：GuiBendsMenu、GuiSettingsWindow、GuiBenderList/GuiBenderSettings 用搜索和 toggle 修改 Bender animate 并 Core.saveConfiguration；Packs UI 以左右列表拖放 applied/unused，调用 PackManager.setAppliedPacks；G 键打开 menu、F10 刷新（KeyboardHandler.java:12-34）。这是明确用户可见配置链，未做 UI 运行验证。

### MB-U-023 — WebAPI / editor / addon GUI 的空壳与可达性

- WebAPI：core/WebAPI.java:12-63 首次点击 Customize 时 GET 固定 raw.githubusercontent.com/mobends/mobends-resources/master/static-api.json，读取 officialAnimationEditorUrl；请求失败显示 ErrorReporter，随后 GuiEditorNotFound 可打开浏览器 URL。该 URL 拉取是实际可达但只在用户点击 Customize 且无已注册 editor 时发生。
- Editor API：AnimationEditorRegistry.java:5-32 只返回第一个注册的 IAnimationEditor；基线默认 Addon 未注册 editor，故 Customize 默认进入 WebAPI/not-found popup。Addon 注册 editor 是可扩展接口，不代表官方编辑器随 mod 打包。
- Addons GUI：GuiAddonsWindow.java:12-64 能绘制 Addons.getRegistered()，但主菜单 addons button 在 GuiBendsMenu.java:31-48 被注释，当前默认不可达。GuiPopUpCreatePack.java:9-47 有 Create/Cancel 文本按钮，但未接到 PackManager 创建流程，属于 UI 空壳；不把语言 key 或 png 当成可用创建功能。

## 9. 资源、美术、依赖与测试边界

### MB-U-024 — src/main/resources 真实资源清单与引用

- 元数据：src/main/resources/mcmod.info（modid mobends、logoFile assets/mobends/textures/logo512.png）、pack.mcmeta pack_format 3、META-INF/accesstransformer.cfg；这些是 Forge 1.12.2 包入口，不是新版 loader metadata。
- 运行时实际引用纹理：entity/wolf_misc.png（Wolf mouth/tongue）、textures/gui/title.png、icons.png、pack_window.png、buttons.png、fonts/bold.png、default_pack_thumbnail.png；引用点见 MB-U-013、MB-U-019、GuiBendsMenu.java:26-29、GuiSettingsWindow.java:24-25、GuiPacksWindow.java:23-24、GuiSectionButton.java:19-20、CustomFont.java:11-14、ThumbnailProvider.java:11-12。cape.png 只有 BendsCapeRenderer.CAPE_TEXTURE 常量（BendsCapeRenderer.java:23），LayerCustomCape 实际绑定 player.getLocationCape，故不能列为已消费内置 cape 纹理；addons_window.png 由不可达 GuiAddonsWindow 声明，node_editor.png、bends.png、stand_block.png 在本基线未找到 Java runtime 引用，logo512.png 是 mcmod.info logoFile 元数据，不是实体渲染纹理。
- 运行时动画资产：bends/animators/wolf.json；二进制 wolf_idle/walking/run/sitting/sitting_down/standing_up/breathing/test.bendsanim；JSON player_anim_harvest.json。只有 wolf.json 中列明的资源从默认 Wolf Kumo 链真实引用，未引用资源已在 MB-U-016 标出。
- 语言：assets/mobends/lang/en_us.lang 及 fr_fr/it_it/no_NO/pl_PL/pt_BR/ru_ru.lang；大小写混合符合 1.12 LegacyV2 说明但不适用于新版资源命名约束。语言 key 覆盖 GUI/config/setting，存在 key 不等于新版 port 翻译完整。
- 作者素材：misc/ 含 Blender/XCF/PNG/FBX/DAE rigs、funatparties.bends、packDatabase.json/.bpd、promo 图和备用 resources；除非上文有 source 引用，否则全部标为作者/历史素材，不计入 runtime。

### MB-U-025 — 上游依赖和打包边界

- 精确证据：build.gradle:1-188；gradle.properties:1-8。ForgeGradle 3.+、Kotlin Gradle plugin 1.3.72、Shadow 4.0.4；Minecraft 1.12.2、Forge 14.23.5.2859、snapshot mappings 20171003-1.12；Java source/target 1.8，Kotlin JVM 1.8；运行依赖 Kotlin stdlib/stdlib-jdk7/stdlib-jdk8/reflect 1.3.72，testCompile JUnit 4.11。
- 直接源码依赖：Minecraft/Forge 1.12.2 APIs（Forge events, SimpleNetworkWrapper, ModelRenderer/GlStateManager）、Gson、Apache HttpClient exceptions、LWJGL/OpenGL、Kotlin stdlib；Shadow 打包并 relocate Kotlin 到 goblinbob.mobends.kotlin。AccessTransformer 由 jar manifest FMLAT 指向 accesstransformer.cfg。
- 打包副作用：shadowJar 包含 Kotlin stdlib/jdk8 并 reobf；processResources 只展开 mcmod.info 的 version/mcversion；其余资源原样复制。当前 port 五目标使用不同 loader/API，不能仅凭这些依赖名称声称可复用。

### MB-U-026 — 上游测试、注释和静态边界

- 上游测试只有 src/test/java/goblinbob/mobends/test/core/util/WildcardPatternTest.java（精确/prefix/suffix/contains）与 core/math/vector/Vec3fTest.java（length/normalize 等）；未找到动画、renderer、网络、Pack、Kumo、GUI 或 supporter 的上游单元/集成测试。
- 明确的上游空壳/不可达：DefaultAddon 对 ZombieVillager/Husk/WitherSkeleton/Stray 的专用注册调用被注释（不等同于继承兜底绝对不命中）；GuiBendsMenu 注释的 addons button；Packs public tab Coming soon...；GuiPopUpCreatePack 未绑定 PackManager；SquidController.perform/WolfController.perform 返回 null；SpiderPreviewer.getBoneMetadata 返回 null；DriverLayerState 无业务 update；MovementKeyframeNode.isAnimationFinished 恒 false；NetworkConfiguration.isModelScalingAllowed 没有消费者；PublicDatabase 无调用者；wolf_run、wolf_test、player_anim_harvest 无默认 animator 引用。
- “未找到”不等于不存在：上述结论限于锁定提交的 src/main/src/main/resources/src/test 静态调用检索；没有进行构建、启动、GUI、网络请求或客户端动态验证。

## 10. 紧凑覆盖表（上游能力 → 本轮账本状态）

| 主题 | MB-U | 上游状态 | 关键风险/差异线索 |
|---|---|---|---|
| 生命周期/Core/模块 | 001 | 静态接入明确 | Forge 1.12 事件与五目标 loader 时序不同 |
| 七默认 Bender | 002,009,011 | Player + 六类实际注册；部分变体仅专用注册注释，继承兜底另审 | Wolf/Squid动作集合返回 null；renderer/layer 时序需动态 |
| Addon/预览 | 003,007,023 | API明确；预览器部分实现 | Spider metadata null；editor/addons UI默认不可达 |
| 动画/动作/物品 | 006-009,017 | 状态、Biped action、loader静态明确 | JSON/BENDSANIM、帧时序、第三方 Item 未动态验证 |
| 模型/矩阵/IK | 010,011 | 自定义模型和数学链明确 | GL 状态/跨 loader 变换未知 |
| 盔甲/手持/披风/Elytra/第一人称 | 012,013 | 真实 layer/调用明确 | Armor 第三方格式回退；第一人称是 RenderHand hook |
| 箭/剑轨迹 | 014 | DefaultAddon RenderTick + RenderBendsArrow 双调用链明确 | forward/up 交叉带、颜色依 supporter；动态性能未验证 |
| BendsPack/Kumo | 015-019 | 本地 .bendsmeta/.bends pipeline 明确；public API未接入 | 无独立 legacy parser；Movement finish 恒 false |
| 网络/server policy | 020 | request/response/默认 policy 明确 | modelScalingAllowed 无消费者；协议安全未验证 |
| online/supporter/remote | 019,021,023 | Assets/Activity/Accessories/WebAPI 可达链存在 | 会写 config/assets、启动 daemon threads；本轮未联网 |
| GUI/config | 022,023 | Settings/Packs/Customize/F10/G 入口明确 | Public/addons/create-pack 部分空壳 |
| resources/deps/tests | 024-026 | 资源真实引用与 Forge/Kotlin 依赖列明 | 仅 2 个测试类，不支持行为完整性声明 |
| 六类非玩家 controller/data | 027 | 五 Target 有效分支已逐类对照 | Spider climb predicate、Squid movement fields、Wolf tail/baby geometry、排除实体仍需动态/MC语义验证 |

## 11. 当前覆盖、未覆盖和最重要差异线索

已覆盖：上游入口和注册调用、七默认 Bender（含注释/不可达区分）、状态动画/物品动作、模型/盔甲/披风/Elytra/第一人称/轨迹、Addon/预览、BendsPack/Kumo/loader、GUI/config、网络 server policy、Assets/Supporter/WebAPI/public remote、真实资源和依赖/测试边界。

仍需主代理或复核代理确认：

- 将每个 MB-U 的上游 path:symbol:line 与当前五 Target 具体实现/注册/资源引用逐一对照，尤其 EntityRenderHandler/Mixin/loader events、Player first-person、Armor layers、ArrowTrail manager、NetworkConfiguration、Pack/Kumo。
- 对上游 WolfController/SquidController 返回 null、Movement finish 恒 false、modelScalingAllowed 无消费者、GUI 空壳等高风险线索做独立 review；这些是上游账本事实，不是本 port 修复建议。
- 需要动态运行才能确认的项：实际 renderer layer 顺序、GL 状态恢复、动作帧时间、跨版本模型尺寸/皮肤/盔甲/Elytra、网络失败 UI、远程 payload/文件写入、用户视觉效果。现有运行 PASS 不属于本轮结论。

本文没有把任何静态对应、资源存在或历史运行记录写成“移植等价”；若后续扩展只应新增事实和精确证据，不回溯替换本基线。

## 12. MB-U-027：六类非玩家控制器与五 Target 的有效正文复核

本项只比较锁定 U（Forge 1.12.2）与当前五 Target 的有效条件分支；没有把另一分支的 block-comment 正文计入。静态核对确认：1.20.1-forge 与 1.21.1-neoforge 有相同的 legacy 有效分支；26.1.2-neoforge、26.2-fabric、26.2-neoforge 有相同的 modern 有效分支。五个 Target 的有效注册正文分别位于 DefaultAddon.java 的 modern 2-69 或 legacy 72-139；无构建、无运行、无视觉结论。

### MB-U-027-A — 注册边界、实体类迁移和新增/排除实体

- U：standard/DefaultAddon.java:26-82 注册 Player、EntityZombie、EntitySkeleton、EntityPigZombie、EntitySpider、EntitySquid、EntityWolf；没有 Drowned/GlowSquid/baby-Squid 排除。U 的 EntityBenderRegistry 没有当前 port 的 inherited exclusion/predicate API。
- 当前五 Target：DefaultAddon.java modern:26-50、legacy:95-119 均注册 Player、Zombie、Skeleton、ZombifiedPiglin、Spider、Squid、Wolf；实体 API 迁移为 EntityPigZombie→ZombifiedPiglin、EntitySpider→Spider、EntitySquid→Squid、EntityWolf→Wolf。modern/legacy 两段的注册顺序和七个主体 Bender 一致。
- 当前新增边界是有效行为而非注释：DefaultAddon modern:30、45-47 / legacy:100、115-117 调用 excludeInheritedEntityClass(Drowned)、excludeInheritedEntityClass(GlowSquid)，并对 baby Squid 调用 excludeEntityWhen；EntityBenderRegistry.java:64-105 先执行 predicate，再在继承查找前执行 excluded class 检查。因此 Drowned、GlowSquid 和 baby Squid 不会继承 Zombie/Squid Bender。是否还有其他 Minecraft 子类由 runtime 类继承关系决定，静态未扩展推断。
- 原因：明确（当前源代码分支和调用顺序）；U→当前的 MC 类名是明确 API 迁移；排除后用户视觉影响仍未知。

### MB-U-027-B — Zombie：控制优先级和变体动作

- U 有效正文：standard/animation/controller/ZombieController.java:24-69；standard/data/ZombieDataBase.java:10-49。current controller 在五 Target 全文件 SHA-256 前缀均为 6971C05B1F，正文为 ZombieController.java:19-57；modern data 为 ZombieDataBase.java:8-46，legacy 为 54-92。
- 状态控制在 U/current 静态相同：airborne 或 touchdown window 先播放 jump；否则 still-horizontal 播 stand，否则 walk；随后无条件在第二层播放 animationSet 的 lean 或 stumbling。U 的动作 keys 是 stand/walk/jump/lean/stumbling；current bit 正文为 ZombieLeanAnimationBit.java:8-14 返回 lean、ZombieStumblingAnimationBit.java:9-15 返回 stumbling，base biped bits 返回 stand/walk/jump。
- Data 输入语义相同：两代都以实体 id 派生 2 个 animationSet，并每 80-99 tick 随机切换 currentWalkingState；current API 仅将 U 的 getEntityId 映射为 getId（modern ZombieDataBase.java:19-25，legacy:65-71）。没有外部 wolf/Kumo 资源触发。
- 逐 Target 结论：1.20.1-forge、1.21.1-neoforge（legacy）和 26.1.2-neoforge、26.2-fabric、26.2-neoforge（modern）控制器正文可静态视为同一状态优先级；Drowned 排除是注册边界的新增差异，非 controller 正文差异。明确控制流；动态模型/API 行为未知。

### MB-U-027-C — Skeleton 与 ZombifiedPiglin/PigZombie：Biped 动作层

- Skeleton：U standard/animation/controller/SkeletonController.java:28-79；current modern SkeletonController.java:24-67，legacy:93-137。五 Target 有效分支只改变 Minecraft model/entity API 和 actionController 的传参形式：current performActionAnimations 将共享 actions 集合传入 BipedActionController.perform，随后与 base layer 的 actions 合并；U 由 action controller 自己执行 action layer。base 优先级仍为 jump > stand/walk，主体 keys 仍 jump/stand/walk；持物动作由 BipedActionController 追加，属于动作输入依赖，不是 skeleton-specific Kumo resource。
- PigZombie：U standard/animation/controller/PigZombieController.java:25-77；current modern PigZombieController.java:22-62，legacy:86-126。U EntityPigZombie 已迁移为 current ZombifiedPiglin；base 优先级仍 jump > stand/walk，U 以 pigZombie.swingProgress>0、current 以 getAttackAnim(partialTicks)>0 播放 AttackSlashInward，否则 clear。主体 keys 为 jump/stand/walk，攻击 bit 明确返回 attack、attack_slash_inward（current AttackSlashInwardAnimationBit.java:15-20/103-108；legacy/modern 分支语义相同）。
- Data 输入：SkeletonData/PigZombieData 仅持 controller 与 BipedEntityData pose；没有外部动画资源。current 五 Target 在对应 family 内正文 hash 一致（Skeleton legacy 9891580CD1、modern 5B6BC27DCD；Pig legacy 5AA049DBB8、modern 5F114B2445），说明逐 Target 只存在两组 API 分支，而非五套独立控制规则。
- 共享持物输入有明确 API 差异：U BipedActionController.java:77-112 用 ItemFood/ItemBow/ItemSword 与 1.12 ArmPose；current modern BipedActionController.java:109-157 用 IItemCapabilityProvider.isFood、BowItem、ItemTags.SWORDS/registry holder，legacy 有效段 368-415 用 isFood + BowItem + SwordItem。输出 action keys 可复用，但第三方食物/剑/弓的分类命中条件不是静态等价。
- 原因：明确（优先级、动作 key、shared actions 调用）；U action layer 自执行与 current shared collection 是调用形态差异，是否对 pack action 产生边界差异需动态/包复核。

### MB-U-027-D — Spider：死亡/攀爬/跳跃/落地状态和 IK 输入

- U：standard/animation/controller/SpiderController.java:22-82 的优先级为 health<=0 death > entity.isBesideClimbableBlock() crawl > airborne jump > landing reset 后 idle/move；U controller 本身已有 resetAfterJumped。U data 的 wallFacing 是 EnumFacing，calcWallFacing 在 standard/data/SpiderData.java:119-150 使用 isOnLadder、EnumFacing 和四邻方块。
- 当前：modern SpiderController.java:18-60、legacy:123-165 保持 resetAfterJumped（27、42-46/132、147-151），并以 spider.onClimbable()（36-38/141-143）作为攀爬优先条件；仍保持 death > climb > jump > idle/move。current SpiderData modern:15-142、legacy:367-493 使用 Direction、entity.onClimbable()、level().getBlockState，八条 Limb 和 IK 数据面保持不变。
- 动作 keys：idle/move/jump/death/crawl；current SpiderIdleAnimationBit.java:15-16、SpiderMoveAnimationBit.java:9-14、SpiderJumpAnimationBit.java:11-12、SpiderDeathAnimationBit.java:11-18、SpiderCrawlAnimationBit.java:11-15（legacy/modern 有效分支同 key）。无外部资源触发。
- 差异判定：明确存在 isBesideClimbableBlock→onClimbable 的 controller API 迁移、U data 的 isOnLadder→current onClimbable/Direction 迁移；resetPosition 并非新增而是 U 已有。仅当新 API 的 onClimbable 语义覆盖 U controller/data 的 climb 条件时，状态优先级可视为条件等价。视觉 IK/墙面朝向未验证。

### MB-U-027-E — Squid：连续触手输入、null action 和 baby 排除

- U：standard/animation/controller/SquidController.java:18-55 计算 squidRotation 并更新 8×9 tentacle transforms，最后 return null；standard/data/SquidData.java:8-76 固定 TENTACLE_SECTIONS=9、8 条触手。U 输入使用 1.12 映射字段 squidRotation/prevSquidRotation。
- 当前：modern SquidController.java:16-58 使用 oldTentacleMovement/tentacleMovement；legacy:75-114 使用 xBodyRotO/xBodyRot，并保留相同的 +1.1、60° base angle、每段 10° sin 摆动和 return null。legacy 代码注释声称 1.20.1 做了字段重命名，但这只是 port 注释而非 API 证据；modern 分支反而明确说明 xBodyRot 是 body pitch、不可驱动触手。因此 1.x xBodyRotO/xBodyRot 到 modern tentacleMovement 的映射属于实质可疑/未知，不能按“已确认 API 适配”计入等价。
- Data：current SquidData modern:9-69、legacy:80-140 仍是 8×9 结构，无动作 keys/外部资源。DefaultAddon modern:44-47 / legacy:113-117 对 baby Squid 直接 predicate exclude；GlowSquid 另由 inherited exclusion 排除。U 没有该排除。
- 差异判定：return null 在 U 和五 Target 都是明确的“controller 不提交动作 key、只写触手 transform”；字段映射的物理时序、baby exclusion 的用户可见范围未知。

### MB-U-027-F — Wolf：Kumo 图、null action、tail roll 与 baby geometry

- U：standard/animation/controller/WolfController.java:26-99 读取 mobends:bends/animators/wolf.json，Kumo update 后写 baby head offsetScale/offset、head/mane/tail roll，最后 return null；WolfData.java:9-121 提供 head/body/四腿/tail/mane/nose/mouth/tongue/ears。
- 当前有效 controller：modern WolfController.java:22-109，legacy:132-208。两者都加载相同 Kumo path、对 DataUpdateHandler.ticksPerFrame 更新 Kumo、最后 return null；当前资源 wolf.json 在五 Target 完整文件 SHA-256 前缀均为 741398083E，除 U 的 comment 属性被删除和 JSON 排版压缩外，驱动层/node/connection/action key 字段一致。wolf_idle/walking/sitting_down/sitting/standing_up/breathing 二进制资源在五 Target 与 U 的 SHA-256 内容一致；wolf_run、wolf_test 仍未被 wolf.json 引用。
- Controller 差异：modern 去除 U 的 per-head baby offset（WolfController.java:64-69），因为 modern mutator 采用 dedicated baby parts；legacy 保留 U 式 baby head offset（WolfController.java:174-181）。modern tail 使用 toMoBendsTailPitch 并基于 Wolf.getShakeAnim 计算 body roll（98-107），legacy 使用 Wolf.getBodyRollAngle（206-208）并直接转换 tail angle；两者目标是适配 MC API/模型，不足以静态证明数值等价。
- Baby geometry：modern WolfMutator.java:42-44、163-224、289-347 建 adultParts/babyParts、baby 32×32 atlas、空 mane animation node、独立 baby tail/muzzle/ears/lower legs，并在 syncUpWithData 按 babyGeometry 选择/重映射；legacy WolfMutator.java:514、648-718 使用同一 adult parts，baby 时在 renderMutated 分 adult body/head 两次 Ageable transform，再在 renderMisc 做 child correction。U 1.12 WolfMutator 没有该 current dedicated/ageable branch。此处是明确的 Target/MC 模型差异，视觉/UV/姿态等价未知。
- Data part names 与 U 保持（current modern WolfData.java:9-106、legacy:118-217）；wolf_state 条件和资源触发图在五 Target 保持。结论：成人 Kumo 动作图静态一致，baby geometry 和 tail-roll 是需动态复核的差异。

### MB-U-027-G — 五 Target 逐项结论和可复用条件

| Target | 有效 controller/data 分支 | 静态结论 |
|---|---|---|
| 1.20.1-forge | legacy；controller/data 文件以条件后的 legacy 正文生效 | Zombie/Skeleton/Pig base/action 优先级复用；Spider 使用 is/onClimbable 适配且保留 U 的落地 reset；Squid 使用 xBodyRotO/xBodyRot，触手输入映射可疑；Wolf adult mesh + child render passes。 |
| 1.21.1-neoforge | legacy；与 1.20.1 有效文件 hash/正文相同 | 与 1.20.1 同一控制器规则；Squid xBodyRotO/xBodyRot 映射仍未知；MC API 实际运行差异未动态验证。 |
| 26.1.2-neoforge | modern；首个条件分支生效 | 与 26.2-fabric/NeoForge controller/data 规则相同；dedicated Wolf baby parts、tentacleMovement、onClimbable 生效；与 1.x 触手映射未证等价。 |
| 26.2-fabric | modern；首个条件分支生效 | 与 26.1.2/26.2-neoforge controller/data 规则相同；无独立动作状态分支。 |
| 26.2-neoforge | modern；首个条件分支生效 | 与其他 modern Target 静态相同；loader 渲染接入不在本 MB-U-027 扩展范围。 |

可复用条件：Zombie/Skeleton/Pig 的状态优先级和主体 action keys 可静态复用；Spider 的动作优先级可复用但 climbing/reset 输入不同；Squid 的 null-action/8×9 触手结构可复用但 movement 字段映射与 baby 排除不同；Wolf 的 Kumo graph、part names、null-action 可复用，但 tail-roll API 和 baby geometry 不可凭静态相似率宣称等价。以上均为静态明确/间接/未知分层，不是运行 PASS。
