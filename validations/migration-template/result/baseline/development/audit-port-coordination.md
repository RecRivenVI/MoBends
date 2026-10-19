# 旧 port 审计：总协调独立复核

审计日期：2026-09-10。此记录由总协调维护；项目主入口为 [正式证据账本](audit-port-status.md)。本轮研究和协调复核已收尾；静态覆盖与移植功能完整性分别判断，未宣称逐一穷尽所有方法。

## 身份与实际工作树

- 旧 port：`D:/Workspaces/Repositories/GitHub/RecRivenVI/MoBends`；不包含 `Repositories/Local/MoBends` 第二次实现。
- origin `https://github.com/RecRivenVI/MoBends.git`；upstream `https://github.com/mobends/MoBends.git`。
- 当前 `stonecutter`，HEAD `01d5cff03a188ee3bf0366d4e2c17ed0ff1e85a3`。开场为 583 个未暂存删除、6 个修改及未跟踪迁移内容，暂存区为空。能力结论必须针对实际工作树，不能仅标 HEAD。
- 封存 `D:/Workspaces/Artifacts/OldPortAudit/20260910/MoBends-start.json`，manifest SHA-256 `b7e737876af7400b1bc252884b57568f828c6fafb30c85a8c06a288ca749dc13`。
- 2,804 个实际文件；source seal `d57c118c2a2a36ceb13d131badcddeaa01ee8223d5c1415846ca9f67e664f979`。同时保留 index/status 和缺失路径。
- 范围：tracked + 非 ignored untracked；排除 runtime/cache/IDE、本机私密配置、本轮 `audit-port-*` 文档及软链接目标。具体排除见 manifest；不是运行实例或所有 ignored 内容快照。
- 只写审计记录，不修改源码、资源、依赖、构建、目标或配置；不清理、不启动游戏、不提交或推送。

## 已独立核实

### MOB-C001：原始移植上游有直接父提交依据

`git rev-list --parents -n 1 92c5960f9ab4749f9a243d8f4708059c2b706a7a` 返回 `0508765b3a9066d0eb8124fbe771a0ca4600f637`。该 SHA 同时对应现存 tag `v1.2.2` 和本地 `upstream/1.X/forge-1.12` 引用。应固定该 SHA 做上游盘点，不能用另外的 1.16 开发分支代替原始移植基线。

Git 谱系证明来源基点，不证明所有动画、资源、扩展或联网能力已经承接。需要从固定上游独立建立覆盖清单。

### MOB-C002：五目标实现声明仍需五目标接线证明

当前 README 和 AGENTS 均列出 Forge 1.20.1、NeoForge 1.21.1、NeoForge 26.1.2、Fabric 26.2、NeoForge 26.2。本条仅确认声明一致；各目标实际源码选择、入口、资源选择与动画驱动由主任务和映射组逐一核实，不以任一目标历史测试替代其他目标。

## 调度与下一步

实时核实主任务“核对 Mo' Bends 项目结构”，ID `019ff1d0-22dc-7c33-b59e-6e21778609e4`；调度 API 接受 `gpt-6-astra`、`max` 的审计请求。主任务已回报实际创建的 `/root/upstream_inventory`、`/root/current_mapping`、`/root/adversarial_review`；均为 `luna_worker`、`fork_turns=none`，工具角色固定 GPT-5.6 Luna/max，未返回额外 UUID。各自独占 upstream、mapping、review 文档。

总协调已独立复核联网边界反证、退化动画、两个 1.x 鱿鱼字段和选定目标条件，审阅主任务合并表及明确未查清单。资源 hash 相同不等于被实际加载；后续恢复以主入口的 DYN/CHECK 项和新的授权范围为准，不重做旧迁移。

补充调度读回：实际 rollout 本次 `turn_context` 的 `turn_id=01a08a35-3d37-7692-939b-c7391715dfd3`，`model=gpt-6-astra`、`effort=max`，与任务状态接口一致。过滤后的证据位于 `D:/Workspaces/Artifacts/OldPortAudit/20260910/main-task-models.json`。

## 第一批独立反证

### MOB-C003：模型缩放策略未消费，不能直接归为本次移植遗漏

固定上游 `0508765...:src/main/java/goblinbob/mobends/core/network/NetworkConfiguration.java:15-52` 定义三个共享布尔值，并在加入世界时选择单人/多人默认值。对该上游整个 `src/main/java` 搜索 `isModelScalingAllowed` 仅找到 getter 定义，未发现产品执行消费者。

当前五 Target 的 `core/network/NetworkConfiguration.java:9-38` 保留 private 字段、加入世界重置、服务器值应用和 getter。对各目标 Java 去除块注释后复核，缩放 getter 的产品侧读取仍未找到；Loader transport 中同名调用读取的是 `ServerNetworkConfig`，用于发送布尔值，不是执行缩放约束。五目标分别对应 ForgeNetwork、NeoForgeNetwork、FabricNetwork 的有效 transport。

结论：三个字段有传输不能证明三项限制都有执行效果；这个缩放字段的消费缺口在固定上游也存在，不能凭当前缺口指控移植丢失了已证实的上游缩放约束。来源类型为上游已存在的未接入状态；是否补全是后续产品决策，不在本轮修复。反射或外部 Addon 的潜在读取没有被这一本仓检查排除，不能扩大为所有运行环境绝对不可达。

`areBendsPacksAllowed` 和 `isMovementLimited` 则在当前 `core/mutators/Mutator.java:237-239,526-528` 有具体消费者，应与缩放字段分别判断。公共 HTTP 下载能力的删减也不能与 Minecraft 连接内策略同步合并为一个“联网已删除”结论。

### MOB-C004：其他 Loader 文件存在不等于目标编译了它们

例如 `versions/26.2-fabric/src/main/java/.../forge/network/ForgeNetwork.java:2` 以 `/*package` 开始，整段 Forge 实现处于块注释中；相邻 FabricNetwork 是活动 Java。因此简单符号搜索会同时看到多个 Loader 名称，不能据此推定注册冲突或多 Loader 支持。目标分析必须读取实际注释边界、源码集与 metadata，原 Stonecutter 标记不是独立运行的条件编译器。

### MOB-C005：上游 PublicDatabase 定义不能独自证明在线包目录能力

固定上游 `0508765...:src/main/java/goblinbob/mobends/core/pack/PublicDatabase.java:17-34` 有下载 JSON 数据库的静态方法。但对上游整个 src/main/java 与 src/main/kotlin 的类名和方法名搜索，仅找到该文件自身定义/反序列化引用。PackManager.initialize/initLocalPacks 实际处理本地 `.bendsmeta`、已启用包列表和 public_cache，未从该入口取得 PublicDatabase 消费链。

因此即便当前移除了该文件，也不能仅凭上游方法名认定“已工作的在线包目录被移植漏掉”。本条状态为上游定义已定位、实际接入未确认；不排除外部调用，不能扩大为绝对无功能。主任务继续核对 UI/cache 与网络清单。

另一条上游联网链有实际调用：CoreClient 注册 ConnectionManager 和 SupporterContent；SupporterContent 请求玩家设置，PlayerSettingsDownloader 回填；LayerPlayerAccessories 消费附件设置，BipedEntityData 把 supporter trail color 用于 SwordTrail。这些活跃入口必须与 PublicDatabase 分开评估删减及连带影响，不能笼统称“全部联网已关闭且不影响本地能力”。本轮未请求任何公共游戏服务。

### MOB-C006：复核退化 Kumo 动画的非终止循环

总协调直接读取 Fabric 26.2 的 PackDataProvider.validatePackData (`:94-145`)、StandardKeyframeNode 构造器/update、MovementKeyframeNode.update，并核实 KumoAnimatorState → KeyframeLayerState → node.update 调用点。嵌入动画校验按 key 存在通过，不拒绝空 bones；标准循环节点从空 bones 得到 duration=0，update 的 `while (progress >= duration - 1) progress -= duration - 1` 会不断增加 progress，不能终止。五 Target 的 StandardKeyframeNode 字节哈希相同；主审也回读上游相同算法并确认继承来源，见 MB-R-001 及主入口复核。

状态：部分实现中的静态缺陷已确认，不是“整个 Kumo 功能缺失”，也不是本轮运行所得的卡死记录。只限满足上述动画形状并进入循环节点的路径，不否认普通有效多帧动画的对应实现。Movement 中浮点 `% 0` 得到 NaN，不能误写成整数除零异常；单帧有骨骼时还需区分是否先在采样阶段越界。后续可在受控超时环境验证并修复输入校验/播放边界，本轮没有执行这个可能挂起的样例。

### MOB-C007：两个 1.x Target 的鱿鱼触手周期存在版本字段映射错误

总协调独立读取上游 `0508765...:standard/animation/controller/SquidController.java`：用 squidRotation/prevSquidRotation 作为周期并与 PI/2PI 比较。当前 Forge 1.20.1 与 NeoForge 1.21.1 同类的活动分支 `:81-107` 却读取 xBodyRot/xBodyRotO；正确的 tentacleMovement 分支在这两个文件前半段被块注释，不能因为搜索到了它就认定已使用。26.2 Fabric 的活动分支则使用 tentacleMovement/oldTentacleMovement。

依赖语义复核：从现有 `forge-1.20.1-47.4.23-sources.jar` 读取 Squid.java，tentacleMovement 按 tentacleSpeed 增加并在 2PI 循环，而 xBodyRot 是 atan2 速度后乘 180/PI 的身体俯仰角；1.21.1 固定反编译参考具有相同区分。总协调实核两份输入哈希分别为 `a84acf0aa784714afd24eaa215e74e2b33244b56091191d885b1f8baa45521b4`、`3397e37c34de8bfaf2cea387d570330f547abfbb41721c29cd44afb6ca9f42f0`，与主任务 `minecraft-squid-evidence.json` 一致。

结论：限上述两个 1.x Target，身体角度被用于触手周期，是已静态确认的移植输入映射缺陷。当前源码“were renamed”的注释不符合依赖字段语义，不能作为适配依据。主任务记录为 MB-P-012；三项 26.x 不因本条被一并判错。未运行动画、未修复；后续优先修正语义映射并按目标对照鱿鱼水中/离水/身体转向与触手周期。

## 本轮交付与结束核查

主任务与三个 Luna/max 代理均已完成本轮研究。正式入口归并 U001–026、非玩家补充 U027、五目标映射、反例和主审证据，另列当前新增内容与八类后续验证。五目标有实际源码和入口，不足以证明所有动画/装备/扩展行为等价。

总协调结束封存 `D:/Workspaces/Artifacts/OldPortAudit/20260910/MoBends-end.json`，SHA-256 `233ed9d61f5d4324f378124e0b2e5274e2f153d4d9dc45cc1e392976d5c516c6`。与开场同范围 2,804 个现存文件比较，变化 0；HEAD、分支、远端、index 与缺失 tracked 路径均相同。仅新增五份 audit-port 文档，既有迁移保留未提交。

统一核查见 `D:/Workspaces/Artifacts/OldPortAudit/20260910/coordination-verification.json`，SHA-256 `92347f94f9acc06929bf18090539531411cb902e4ca85603d3130164bd5b11d4`。`git diff --check` 通过；未改实现/资源/配置、未构建或运行产品测试/游戏、未 stage/commit/push。运行实例和缓存不属于所声称的源码封存范围。
