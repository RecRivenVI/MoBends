# AGENTS.md

本文件约束在 Mo' Bends 仓库中工作的全部编码 Agent。违反本文件的行为视为缺陷。

Mo' Bends 由 RecRivenVI 独立维护。`upstream` 只用于只读查询原始实现及历史，不是当前项目
身份、开发基线或发布目标。本文件的独立维护治理重写已由用户明确授权；此后除非用户再次明确
授权，Agent 不得自行编辑、删除、重命名或覆盖 `AGENTS.md`。

## 1. 开始工作前

依次阅读并核对：

1. `AGENTS.md`；
2. `README.md`；
3. `docs/architecture/project-layout.md`；
4. `docs/development/getting-started.md`；
5. `docs/development/compatibility-policy.md`；
6. 与任务有关的 `docs/testing/**` 和 `docs/reference/**`；
7. 当前分支、`git status --short --branch --untracked-files=all` 与 `git remote -v`。

保留用户已有改动。不得使用 `reset --hard`、破坏性清理或上游内容覆盖工作树。未经用户明确
授权，不得提交、推送、打标签、发布或改写历史；任何情况下都不得向 `upstream` 推送。

## 2. 项目身份与兼容边界

- 公开仓库：`https://github.com/RecRivenVI/MoBends`；
- 当前维护者：RecRivenVI；
- 原作者和贡献者记录在 `CREDITS.md`，原始行为记录在 `docs/reference/upstream-behavior.md`；
- `mobends`、`assets/mobends`、`goblinbob.mobends`、存档和网络兼容标识不得擅自改名；
- 许可证文件与源码中的版权声明不得删除或弱化；
- 外部模组兼容逻辑必须由 Mo' Bends 自身承载，不要求第三方模组重新编译；
- 远程包、支持者服务和外部编辑器不因历史代码而自动恢复。

## 3. Target 契约

Target ID 严格使用 `<loader>-<minecraft-version>`，loader 只能是 `fabric`、`forge` 或
`neoforge`。仓库固定声明五个目标，当前状态为：

- `fabric-26.2`：`implemented`；
- `forge-1.20.1`：`planned`；
- `neoforge-1.21.1`：`planned`；
- `neoforge-26.1.2`：`planned`；
- `neoforge-26.2`：`planned`。

默认活动目标由 `versions/current` 声明，必须是 `fabric-26.2`。所有目标在
`stonecutter.properties.toml` 中声明 `target.status`、`target.client` 和 `target.server`；不得
以空实现、跳过测试或伪造状态绕过门禁。只有 `implemented` 目标可以解析 Minecraft/Loader
依赖、生成源码、编译、测试、打包或运行。`planned` 目标必须保留源码与实例，通过无 Loader
依赖的惰性节点参加结构检查，并对任何直接生命周期、生成、编译、归档或运行请求快速失败。

## 4. 源码、实例与产物布局

- `src/main`、`src/test`：五目标共享层；
- `src/minecraft-1x`、`src/minecraft-1x-test`：Minecraft 1.x 家族；
- `src/minecraft-26x`：Minecraft 26.x 家族；
- `src/fabric`、`src/forge`：加载器层；
- `src/neoforge-common`：所有 NeoForge 目标共享层；
- `src/neoforge-1x`、`src/neoforge-26x`：NeoForge 版本家族层；
- `versions/<target>/src`：精确目标覆盖；
- `run/<target>/<side>/`：隔离运行实例；
- `build/artifacts/<target>/`：收集后的生产 JAR；
- 产物名：`mobends-<mod-version>+<loader>-<minecraft-version>.jar`。

完全相同的 family 类必须上提共享层。纯行为一致时上提 `src/main`；只差 Minecraft API 时抽取
`VersionAdapter`；只差少量签名或映射时在 Stonecutter 管理的共享文件中使用最小 `//?`；显著
分叉才保留 family 层。不得把整类塞入大段条件注释，也不得删除 public Addon API。

## 5. 编码与格式

- Gradle 使用 JDK 25，Wrapper 使用 9.7.0；
- 所有受管文本为 UTF-8、LF、无行尾空白并以换行结尾；
- Java 使用 google-java-format AOSP；Kotlin/Kotlin DSL 使用 ktlint；
- JSON、TOML、YAML、Markdown、Gradle 和 properties 受 Spotless 基础格式门禁；
- `LICENSE` 和源码版权声明不由格式器改写；
- 新增或修复纯逻辑必须补职责级测试；
- 渲染、GUI、Mixin、资源、网络和运行配置还必须实际启动对应目标验证；
- 不机械按行数拆类，拆分必须沿真实职责边界且保持动画时序、姿态、装备坐标链和透明排序。

格式入口：

```powershell
.\gradlew.bat spotlessApply
.\gradlew.bat spotlessCheck
```

## 6. 构建、测试与运行

当前 implemented 单目标入口统一为：

```powershell
.\gradlew.bat :fabric-26.2:test
.\gradlew.bat :fabric-26.2:check
.\gradlew.bat :fabric-26.2:build
.\gradlew.bat :fabric-26.2:runClient
.\gradlew.bat :fabric-26.2:runServer
```

仓库级入口统一为：

```powershell
.\gradlew.bat spotlessCheck verifyTargets testAll checkAll buildAll collectArtifacts `
  --configuration-cache --configuration-cache-problems=fail `
  --no-daemon --stacktrace --console=plain
```

完整命令必须连续成功两次，第二次确认 configuration cache reused。`verifyTargets` 检查五个声明
节点；`testAll`、`checkAll`、`buildAll` 和 `collectArtifacts` 只选择 implemented 目标，当前只能
选择 `fabric-26.2`。`checkAll` 必须包含 `spotlessCheck`。不得保留第二套聚合入口、PowerShell
启动脚本、loader 专用启动脚本或绕过统一 Gradle task 的运行流程。

## 7. 客户端与多人验收

自动门禁通过后，只能启动 `fabric-26.2`。gameDir 必须精确为
`run/fabric-26.2/client`，并在启动后核对 Minecraft、Loader、玩家、维度、位置和 framebuffer。
四个 planned 目标不得启动；其 `run/<target>` 世界、配置、日志、截图和其他用户数据不得删除、
迁移或作为 Fabric 验收数据使用。

优先使用全局 mcdev-mcp/DebugBridge；只连接本轮明确分配的端口，不自动发现、不操作、不终止
其他 Minecraft、Gradle 或相同工作流。需要旧目标可见 UI 操作时可使用全局 computer-use/Orca。
日志或进程存活不能替代真实世界和 framebuffer 证据。若验收操纵物理状态，结束前恢复并核对
重力、碰撞、飞行、速度、位置和着地状态。

多人编排只能位于仓库外，使用独立 gameDir、端口和会话 PID 清单，只停止清单所属进程。
完整入口与矩阵见 `docs/testing/runtime-validation.md` 和
`docs/testing/acceptance-matrix.md`。

## 8. Git 与上游

- 日常开发分支为 `stonecutter`；
- `origin` 是 RecRivenVI 仓库；
- `upstream` fetch 保留原仓库，push URL 必须是 `DISABLED`；
- 未经用户授权，不提交、推送、打标签、发布或改写历史；
- 获得授权后也只暂存源码、文档和配置，不暂存 `run/`、`build/`、日志、世界、截图、录像、
  备份、GPU 捕获或 DebugBridge 数据；
- 提交前必须执行 `git diff --check`、格式门禁、五个声明节点的结构验证、全部 implemented
  目标的测试/构建、planned 快失败负向回归和与改动相称的真实运行验收。
