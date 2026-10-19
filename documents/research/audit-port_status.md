# 移植审计在新结构中的对应

2026-10-19 整理。2026-09-10 静态审计的结论保持未修复，本轮仅迁移模板与适配 26.3 API。原始材料和快照绑定保存在[历史审计](../../validations/migration-template/result/baseline/development/audit-port-status.md)。

## 问题

将既有审计的问题对应到当前源码，避免结构迁移被误认为产品缺陷已经修复。原审计不是逐方法穷尽审计，也没有本轮运行 PASS。

## 方法

从检查点的五份原生源码迁移；原 26.2 两个 Target 分别对应当前 26.3 两个 Target。纯 Java 闭合类型移到 `components/animation_core/src/main/java/io/github/recrivenvi/mobends/`，其余路径仍在 Target 内。Fabric 客户端使用 `src/client/java`。

## 发现

下表的 Java 路径以 `io/github/recrivenvi/mobends/` 为起点，`Target` 指 `versions/<target>/src/main/java/`；Fabric 使用 `src/client/java/`。类与方法比旧行号稳定，历史行号不重写。

| 原编号 | 未修复内容 | 当前实现位置 |
| --- | --- | --- |
| MB-P-012 | 两个 1.x 鱿鱼触手周期误用身体俯仰字段 | 两个 1.x Target 的 `standard/animation/controller/SquidController.java`，`perform` |
| MB-R-001、008 | 退化 KUMO 帧、时长与循环边界 | Target 的 `core/kumo/state/keyframe/StandardKeyframeNode.java` 及对应层 |
| MB-R-003 | 坏 metadata 越过单包异常隔离 | Target 的 `core/pack/PackManager.java`、`core/pack/LocalBendsPack.java` |
| MB-R-009 | 非法动画资源标识符越过包错误隔离 | Target 的 `core/pack/PackDataProvider.java`，`doesAnimationExist`，及 `core/animation/keyframe/AnimationLoader.java` |
| MB-P-003 | 坏服务端 JSON 的异常边界；缩放字段无内置消费者 | Target 的 `core/network/ServerNetworkConfig.java`、Loader transport 与 `core/mutators/Mutator.java`；`NetworkConfiguration.java` 已在 `animation_core` |
| MB-R-002 | Procedural 非 additive 权重语义 | Target 的 `core/kumo/state/procedural/ProceduralLayerState.java` |
| MB-R-004 | Legacy 无轴旋转执行不完整 | Target 的 `core/pack/LegacyBendsPackPerformer.java` |
| MB-R-005、006 | Addon 早注册和失败重试边界 | Target 的 `core/addon/Addons.java`、`AddonAnimationRegistry.java` |
| MB-R-007 | Previewer、编辑器属于已有范围删减 | 原始审计与当前公开 Addon API；本轮不恢复 |
| MB-P-002、008、009 | 泳姿根接管、中心俯仰与轨迹坐标链 | Target 的 `mixin/AvatarRendererMixin.java` 或 `PlayerRendererSwimMixin.java`，`core/client/MutatedRenderer.java`、`standard/client/renderer/entity/SwordTrail.java` |
| MB-P-005 | 远端连段跨版本分歧、睡眠后动作缓存 | Target 的 `standard/data/PlayerData.java`、`standard/animation/bit/biped/item/SwordAction.java`、`BipedActionController.java` |
| MB-P-007 | 帧时钟、菜单及世界切换时的缓存寿命仍缺运行证明 | Target 的 `core/client/event/DataUpdateHandler.java`、`core/data/EntityDatabase.java` 与 Loader 渲染事件 |
| MB-P-010 | 本地包输入与扩展的合并风险 | 同表 MB-R-001 至 009 对应的 parser、状态机及 Addon 注册路径；不另作已修复结论 |
| MB-P-006、MB-R-010 | 披风、渲染异常路径的姿态恢复 | Target 的 `standard/client/renderer/entity/layers/LayerCustomCape.java` 与 Loader `RenderingEventHandler.java` |
| MB-P-011、MB-M-010 | 盔甲默认路径和资源重载跨端差异 | Target 的 `standard/client/model/armor/`、各 Loader `client/render/MoBendsRenderState.java` 与初始化 |
| MB-M-009 | 1.x 图标指向与实际资源不一致 | 两个 1.x 的 `src/main/resources/META-INF/`；元数据迁移时是否改变须单独记录 |
| MB-M-009 | Forge access-widener 资源是否被消费仍无证据 | `versions/1.20.1-forge/src/main/resources/mobends-forge.accesswidener`；保留资源，不把文件存在当作权限已生效 |
| MB-P-004a 至 g | 远程功能的已有删减与范围证据 | 原始审计逐项账本；不随模板迁移恢复 |

其他条目是接线与覆盖账本，而非单独确认的产品缺陷：MB-P-001 对应各 Target 的 `standard/DefaultAddon.java` 和 `core/bender/EntityBenderRegistry.java`；MB-M-001、002 对应 `settings.gradle.kts`、`target.properties`、原生 Target 构建与模板收集任务；MB-M-003 至 008 对应各 Loader 入口、`api/platform`、Mixin JSON、`core/client/ClientRuntime.java`、`core/pack`、`core/addon` 和配置页面。旧源码行号、阶段结论及未执行声明在历史原文中保持不变，不拿本轮参考端启动覆盖其跨 Target 运行缺口。

## 结论

本轮不声明五目标行为等价、全动画完成或功能损失为零。构建与参考端启动记录单独给出；这些审计项留待下一轮修复与场景验收。
