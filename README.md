# Mo' Bends

为 Minecraft 玩家和生物提供分段模型、连续动作动画、装备与披风姿态，以及本地 BendsPack。

## Target

| Target | 加载器版本 | Java |
| --- | --- | --- |
| `1.20.1-forge` | 47.4.28 | 17 |
| `1.21.1-neoforge` | 21.1.264 | 21 |
| `26.1.2-neoforge` | 26.1.2.122 | 25 |
| `26.3-neoforge` | 26.3.0.94 | 25 |
| `26.3-fabric` | 0.20.0 | 25 |

参考端为 `26.3-neoforge`。构建通过与实际动画验收分别记录，已知产品问题见审计索引。

## 快速开始

使用 JDK 25，从仓库根目录运行：

```powershell
.\gradlew.bat spotlessApply
.\gradlew.bat check verifyRelease
.\gradlew.bat :version:26.3-neoforge:runClient
```

## 文档

- [玩家使用说明](documents/usage/guide-animations.md)
- [开发与构建](documents/development/workflow-development.md)
- [版本 API 移植及模板边界](documents/development/porting-template.md)
- [审计结论与当前源码位置](documents/research/audit-port_status.md)
- [依赖与测试输入](documents/reference/dependency-development.md)
- [更新日志](documents/release/changelog-changes.md)

## 许可

采用 MIT 许可，见 [LICENSE](LICENSE)。原作者及贡献者版权声明保留；构建基础及第三方归属见 [NOTICE](NOTICE) 与 [licenses](licenses/)。
