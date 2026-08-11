# 运行时验证

## Target 门禁

仓库声明五个 Loader-first Target，但只有 `fabric-26.2` 为 implemented。`verifyTargets` 始终检查
五个节点的名称、状态、属性、源码根、节点覆盖和受跟踪 Gradle 运行路径映射；其余生命周期
聚合任务只选择 implemented Target。`run/` 是受忽略的用户数据，干净 checkout 无需包含该目录；
结构验证不枚举实例、不要求任一 Target 实例存在，也不读取世界、配置、日志或截图。

四个 planned Target 的 `build`、`runClient` 和生产归档任务必须逐一执行负向回归。失败必须
发生在 Settings 阶段，并明确包含 Target ID、`planned` 状态以及把 `target.status` 改为
`implemented` 的重新激活方式。输出中不得出现该节点的 `stonecutterGenerate`、`compileJava`、
`processResources`、`jar`、`reobfJar` 或 `remapJar` 已执行记录。使用 `--offline` 运行负向命令，
证明门禁不依赖对应 Minecraft/Loader 依赖且不能下载它们。

当前生产归档任务为：

| Target | 状态 | 生产归档任务 |
| --- | --- | --- |
| `forge-1.20.1` | planned | `reobfJar` |
| `neoforge-1.21.1` | planned | `jar` |
| `neoforge-26.1.2` | planned | `jar` |
| `fabric-26.2` | implemented | `jar` |
| `neoforge-26.2` | planned | `jar` |

## 自动门禁

先删除可再生的旧 `build/` 与 `versions/*/build/` 输出，不得清理 `run/`。依次完成：

1. `git diff --check`；
2. `spotlessCheck`；
3. `verifySourceLayout` 与 `verifyTargets`；
4. planned Target 直接任务负向回归；
5. `testAll`；
6. `checkAll`；
7. `buildAll`；
8. `collectArtifacts`；
9. 完整命令连续两次，第二次复用 configuration cache；
10. 核对 `build/artifacts` 只含 `fabric-26.2` 生产 JAR。

统一正向命令为：

```powershell
.\gradlew.bat spotlessCheck verifyTargets testAll checkAll buildAll collectArtifacts `
  --configuration-cache --configuration-cache-problems=fail `
  --no-daemon --stacktrace --console=plain
```

## Fabric 26.2 客户端

自动门禁通过后只启动：

```powershell
.\gradlew.bat :fabric-26.2:runClient
```

gameDir 必须精确为 `run/fabric-26.2/client`。连接 DebugBridge 后读取 Minecraft、Loader、gameDir、
玩家、维度、位置、实体和 framebuffer；进入已有兼容世界，最小抽验 HUD、玩家基础动画、一种
非玩家 Bender、盔甲、物品动作、标准配置入口、本地 BendsPack 和轨迹。检查 `latest.log`，但
日志或进程存活不能替代真实世界和 framebuffer 证据。

不得启动四个 planned Target，也不得移动或删除其世界、配置、日志、截图和其他实例数据。若
验收操纵物理状态，结束前必须恢复并核对重力、碰撞、飞行、速度、位置和着地状态。完成后保持
Fabric 客户端在世界中供用户验收。
