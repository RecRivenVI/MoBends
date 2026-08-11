# 开发入门

## 环境

- Windows；
- JDK 25 用于运行 Gradle；
- 仓库自带 Gradle Wrapper 9.7.0；
- Git 和可访问 Minecraft、Fabric、Forge、NeoForge 依赖仓库的网络环境；
- 真实客户端验收使用全局 mcdev-mcp/DebugBridge，不在仓库内保存工具副本。

先确认 `java -version` 和 `versions/current`，再运行：

```powershell
.\gradlew.bat help --configuration-cache --configuration-cache-problems=fail --no-daemon
.\gradlew.bat spotlessCheck verifyTargets testAll checkAll buildAll collectArtifacts `
  --configuration-cache --configuration-cache-problems=fail `
  --no-daemon --stacktrace --console=plain
```

完整命令必须连续成功两次，第二次应报告 configuration cache reused。

## 当前实现入口

只有 `fabric-26.2` 是 implemented Target：

```powershell
.\gradlew.bat :fabric-26.2:test
.\gradlew.bat :fabric-26.2:check
.\gradlew.bat :fabric-26.2:build
.\gradlew.bat :fabric-26.2:runClient
.\gradlew.bat :fabric-26.2:runServer
```

可用 `-Pruntime.world=<save-directory>` 快速进入已兼容的世界，用
`-Pruntime.username=<profile>` 设置合法的离线开发档案名。不得绕过 Gradle 任务直接拼接加载器
启动命令，也不得新增仓库专用 PowerShell 启动器。Forge/NeoForge 节点当前为 planned；它们允许
`help`、`tasks`、`properties`、`clean` 和结构验证，但任何生成、编译、测试、归档或运行请求都
会在 Settings 阶段失败，且不会配置对应 Loader 构建链。

## 修改原则

优先把纯逻辑放到 `src/main`，Minecraft API 差异放到版本家族或 `VersionAdapter`，加载器 API
放到相应 loader root。新增或修复行为要补测试；渲染、GUI、Mixin、资源和网络改动还要完成
真实客户端或专用服务端验收。格式修复使用 `spotlessApply`，提交前使用 `spotlessCheck`。
