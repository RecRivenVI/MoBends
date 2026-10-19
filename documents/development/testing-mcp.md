# MCP alpha 接入与检查清单

固定快照 `20261019-1245-bfdda69c` 已复制到本项目产物目录；清单的 `packagedRuntimeVerified=true`，所有 JAR、手册及清单副本哈希已核对，不使用 MCP 仓库构建目录。当前参考端只消费 26.3 NeoForge JAR，SHA-256 为 `8b443fa8925a1305360fb907b4e4ed7a95bc536ef4414f3cc16f57d350bee158`。

## 实例与证据

最小验证为 `validations/smoke-mcp_bootstrap`，仅加载 MCP。产品验证为 `validations/smoke-mcp_animation`，加载 MoBends、MCP 与本项目只读探针。两者均为 1920×1080、总音量零，不使用用户存档；世界从迁移前备份复制并核对。

原生 Loader 运行从所选实例的 `minecraft-protocol/endpoint.json` 连接，默认端口零，不扫描端口。请求工具在 `components/acceptance_checks/task/Invoke-McpRequest.ps1`，逐次核对目录、Target、PID/启动时间和 UUID；令牌和 Lease 仅留在忽略的实例中。

```powershell
.\gradlew.bat :acceptance_checks:verifyMcpSmoke
```

结果保存在对应验证的 `result/`；截图只证明有界烟测画面可读，动画内部状态来自本项目探针，用户完整视觉验收仍为 PENDING。

## 缺陷清单

本轮进入世界、移动、视角、截图、释放输入及保存退出的范围未发现 MCP 阻断缺陷。退出游戏时请求可能返回手册已说明的 `RUNTIME_SHUTTING_DOWN/not_confirmed`；不重试，以本实例的保存/停止日志、正常退出码及端点移除独立确认，不把 HTTP 错误当成退出成功回执。

若后续发现缺陷，在此追加固定快照、JAR 哈希、复现步骤、期望与实际，交用户转发；不修改 MCP 或补装依赖。

## 用户验收

日常参考端采用 2560×1440、RecRivenVI、总音量 50%、音乐零。CSL 15.1 来自 [Modrinth 精确版本](https://modrinth.com/mod/customskinloader/version/yyYvYfZI)。2026-10-19 查询 [Zume](https://modrinth.com/mod/zume/versions) 的 26.3/NeoForge 版本列表为空，按用户约定跳过，不安装旧版或替代品。

```powershell
.\gradlew.bat :acceptance_checks:prepareUserAcceptance
.\gradlew.bat :version:26.3-neoforge:runClient
```

验收与自动验证使用不同实例；MCP 回到 READ、无输入 Lease 后交给用户。安装测试模组不增加产品依赖或兼容承诺。
