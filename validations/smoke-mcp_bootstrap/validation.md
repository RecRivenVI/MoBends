# MCP 最小安装检查

## 目标

固定 alpha 原样安装到仅加载 MCP 的原生 Loader 验证实例；不加载 MoBends、探针或其他测试模组。

## 运行

```powershell
.\gradlew.bat :acceptance_checks:prepareMcpBootstrap
.\gradlew.bat :version:26.3-neoforge:runValidationSmokeMcpBootstrapClient
```

按本实例的 `minecraft-protocol/endpoint.json` 锁定 UUID、PID、Target 与目录，使用同批手册规定的原生 HTTP 接口完成 READ，随后正常退出。

## 通过标准

MCP JAR 与固定快照 SHA-256 相同，实例模组仅包含一个匹配端 MCP JAR；未加载产品和探针。READ 返回 Minecraft 26.3、NeoForge 与相同运行 UUID，正常退出并释放端点。此检查不表示 MoBends 动画通过。
