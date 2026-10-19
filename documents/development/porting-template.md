# 模板与 26.3 移植

本轮使用模板 2.0.2 的原样公共文件，产品仍采用原有动画实现。参考端改为 26.3 NeoForge；26.2 Fabric 与 NeoForge 源码分别迁入 26.3 对应 Target。

## 构建结构

| 原位置 | 当前位置 |
| --- | --- |
| `buildSrc/`、`eng/` 的旧构建与运行接线 | 模板插件；仍有用途的保存检查位于 `components/acceptance_checks/` |
| 重复的纯 Java 类型 | `components/animation_core/`，保留 Java 包和类型 |
| `versions/26.2-neoforge/` | `versions/26.3-neoforge/` |
| `versions/26.2-fabric/` | `versions/26.3-fabric/`，客户端代码在 `src/client/` |
| `documents/development/audit-port-*.md` | 原文保存于验证结果，当前索引在 `documents/research/audit-port_status.md` |
| `instances/26.2-fabric/client/` | `instances/26.3-fabric/client/`，迁移前完整备份且按文件核对 |

项目根与 Target 的格式任务显式使用 LF，与 C-02 和 `.gitattributes` 一致，避免 Spotless 默认的 Git 属性判定重复遍历全仓库。格式工具版本与检查范围不变；批处理 Wrapper 不在这些格式任务的输入中，继续保持模板的 CRLF 原文。

## 运行端缺口

用户确认保留可选服务端策略：客户端无需服务端安装 MoBends，已安装的服务端仍可同步策略。模板当前的 `mod_side` 不能完整表达此组合：`client` 使 Fabric 的环境字段为客户端专用；`both` 要求双端安装。

因此 Fabric 暂时保留原 `environment="*"` 和可选服务端入口。此项会触发模板 P-04；本项目没有更改模板或豁免严格检查。等待模板能力补足后再宣称严格合规通过。

## 保留的重复源码提示

2026-10-19 用户确认保留实现，逐项记录 T-07 的理由，并将其明确列为未通过项后验收。严格模式保持开启，模板文件与检查器保持原样；不以改动公开 API 或添加无用途的游戏引用消除提示。

本轮已将 146 个闭合的纯 Java 类型提取到 `animation_core`。检查器剩余的 78 条提示逐项记录于[依赖与保留清单](../../validations/migration-template/result/t07-dependencies.json)：66 项记录了到原生游戏或 Loader 类型的导入依赖链，另外 12 项属于下列不同版本 API 的依赖闭包或不同的既有行为。

| 类型 | 保留在 Target 的原因 |
| --- | --- |
| `IItemStack` | 1.x 额外声明弩、三叉戟和望远镜分类；26.x 没有这些方法，不用统一接口扩充或删减现有 API |
| `ILivingEntity`、`IPlayer` | 装备与手持物方法依赖各 Target 的 `IItemStack` 契约 |
| `IPlatformServices`、`PlatformServices` | 服务接口暴露各 Target 的物品和实体契约，ServiceLoader 加载对应实现 |
| `IRenderContext`、`ITesselator` | 分别经过实体接口和平台服务接口依赖上述契约 |
| `DataUpdateHandler` | 1.x 保留预览时间覆盖能力；26.x 不包含该机制，不在结构迁移中改变时间行为 |
| `SerialHelper` | 1.x 使用缓冲器逐字节读取；26.x 使用数组与 `readFully`，不顺带统一分配及错误边界 |

这些是用户授权保留的未通过提示，不是严格合规 PASS，也不据此声明所有源码已实现最优收敛。API 与行为的统一需要后续单独授权；P-04 的模板需求见[可选服务端提案](../project/proposal-optional_server.md)。

## API 差异

| 26.2 API | 26.3 对应 | 保持的行为 |
| --- | --- | --- |
| `PoseStack.mulPose(Quaternionf)` | `PoseStack.rotate(Quaternionfc)` | 相同四元数及调用顺序，矩阵乘法仍使用 `mulPose` |
| `getAttackAnim`、`swinging` | `getSwingAnimation`、`isSwinging` | 使用原版当前挥手状态与插值进度 |
| `AxeItem` 分类 | `ItemTags.AXES` | 原版斧类动作选择 |
| `PrimitiveTopology`、`VertexFormat` 的 Blaze3D 包 | RenderPearl 原生 API 包 | 使用当前 Minecraft 提供的同名几何拓扑与格式 |
| `OutputTarget` 与半透明目标 | RenderPass 附件及既有 `afterTerrain` 阶段 | 轨迹在半透明地形后的提交顺序 |
| `entityTranslucentCullItemTarget` | `entityTranslucentCull` | 原生材质管理当前半透明渲染附件 |
| `Util.OS.openFile` | `Blaze3D.openPath` | 打开本地包目录 |
| `ItemInHandRenderer` | `FirstPersonHandsAndItemsRenderer` 与独立手部 RenderState | 仍在原版双手提交之前同步第一人称姿态 |
| 资源包格式 84.0 | 97.1 | 26.3 两端原生资源元数据 |

Forge 的 Mixin 注解处理器显式登记为构建依赖，以生成重混淆需要的映射；不向产品添加运行时依赖。既有泳姿、披风、包解析等审计问题不在本轮修复。

FML 按模组拥有 Java 包。仅把 `animation_core` 放入普通运行类路径，仍会因其与 Target 使用相同包而在开发启动中找不到类型。Forge/NeoForge 通过原生 `ModModel.sourceSet` 将只含生成输出的 `productRuntime` 并入 MoBends 开发模块；输出来自既有组件 JAR 的原样展开，不产生手写源码根，不改变发行 JAR。Fabric 已使用 Loom 的组件模组分组，不添加这套接线。
