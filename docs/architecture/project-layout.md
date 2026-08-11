# 项目布局

Mo' Bends 是 Loader-first 的 Stonecutter 多版本项目。Target ID 固定为
`<loader>-<minecraft-version>`，版本、加载器、源码组合、运行实例和产物目录均使用同一个 ID。

## Target 与源码组合

| Target | 状态 | Minecraft 家族 | Loader 层 | 节点本地层 |
| --- | --- | --- | --- | --- |
| `forge-1.20.1` | planned | `src/minecraft-1x` | `src/forge` | `versions/forge-1.20.1/src` |
| `neoforge-1.21.1` | planned | `src/minecraft-1x` | `src/neoforge-common` + `src/neoforge-1x` | `versions/neoforge-1.21.1/src` |
| `neoforge-26.1.2` | planned | `src/minecraft-26x` | `src/neoforge-common` + `src/neoforge-26x` | `versions/neoforge-26.1.2/src` |
| `fabric-26.2` | implemented | `src/minecraft-26x` | `src/fabric` | `versions/fabric-26.2/src` |
| `neoforge-26.2` | planned | `src/minecraft-26x` | `src/neoforge-common` + `src/neoforge-26x` | `versions/neoforge-26.2/src` |

`src/main` 和 `src/test` 是所有目标共享层；`src/minecraft-1x-test` 只补充 1.x 家族测试。
节点本地层只容纳精确版本 API 差异。Planned 节点的这些源码继续保留并参加目录、重复 FQCN 和
结构契约检查，但不进入依赖解析、源码生成、编译、测试、归档或运行。完全相同的类型必须放在
共享层，少量映射差异优先经 `VersionAdapter` 或最小 Stonecutter 条件处理，显著 API 分叉才
保留在家族层。

## 构建文件

- `settings.gradle.kts`：声明五个 Stonecutter 节点和默认目标，并在项目配置前拦截 planned
  节点的直接构建请求；
- `stonecutter.gradle.kts`：中央属性标签、格式门禁、结构验证和仓库级任务；
- `build.planned.gradle.kts`：不应用 Minecraft/Loader 插件的 planned 节点最小任务面；
- `build.fabric.gradle.kts`、`build.forge.gradle.kts`、`build.neoforge.gradle.kts`：各 Loader
  的插件、依赖与运行入口；
- `gradle/mobends-common-target.gradle`：五个节点共享的源码组合、资源处理、测试与产物契约；
- `stonecutter.properties.toml`：唯一的 `mod.*`、`deps.*`、`target.*` 属性源；
- `gradle/stonecutter-target-conventions.gradle.kts`：Target 状态和运行侧契约；
- `gradle/formatting`：作为独立 included build 承载 Spotless，避免 Stonecutter 分支根成为可构建工程；
- `versions/current`：默认活动目标，固定为 `fabric-26.2`。

## 运行和产物

客户端与服务端分别位于 `run/<target>/client/` 和 `run/<target>/server/`。生产 JAR 收集到
`build/artifacts/<target>/`，文件名为
`mobends-<mod-version>+<loader>-<minecraft-version>.jar`。运行数据、世界、日志、截图、录像、
DebugBridge 数据和构建产物均不受 Git 跟踪。结构门禁验证受跟踪 Loader 构建脚本中的运行路径
映射，不要求 `run/` 或任一实例在干净 checkout 中物理存在，也不读取实例内容。

## 稳定标识

独立维护不改变 `mobends` mod ID、`assets/mobends` 资源命名空间、`goblinbob.mobends` Java
包名、存档兼容标识或公开 Addon API 包名。这些标识只在明确的兼容性版本中演进。
