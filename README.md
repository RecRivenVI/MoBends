# Mo' Bends

[![Stonecutter CI](https://github.com/RecRivenVI/MoBends/actions/workflows/stonecutter.yml/badge.svg)](https://github.com/RecRivenVI/MoBends/actions/workflows/stonecutter.yml)

Mo' Bends 是由 RecRivenVI 独立维护的多版本 Minecraft Java 模组。它为玩家和多种生物提供更
自然的关节、姿态、动作过渡、装备渲染与攻击轨迹，并通过 Stonecutter 保存五个
Minecraft/Loader 目标的结构与兼容边界。当前只有 Fabric 26.2 进入实现、构建和发布生命周期。

## 支持矩阵

| Target | Minecraft | Loader | 状态 |
| --- | --- | --- | --- |
| `forge-1.20.1` | 1.20.1 | Forge | Planned |
| `neoforge-1.21.1` | 1.21.1 | NeoForge | Planned |
| `neoforge-26.1.2` | 26.1.2 | NeoForge | Planned |
| `fabric-26.2` | 26.2 | Fabric | Implemented |
| `neoforge-26.2` | 26.2 | NeoForge | Planned |

`versions/current` 指向默认活动目标 `fabric-26.2`。Planned 表示现有源码、适配层、测试资料和
运行实例被安全保留以供未来重新激活，不表示当前可构建、可运行、可发布或已通过验收。

## 主要功能

- 玩家、僵尸、骷髅、僵尸猪灵、蜘蛛、鱿鱼和狼的弯曲模型与动画；
- 步行、疾跑、潜行、跳跃、游泳、攀爬、骑乘、飞行、睡眠与鞘翅姿态；
- 食物、弓、盾、工具、拳击、剑连段及现代物品动作；
- 分段盔甲、手持物、头戴物、披风、鞘翅、剑刃拖尾和箭矢拖尾；
- 通过 Mod Menu 或加载器 Mods 页面打开的原版风格配置页；
- 本地 BendsPack 扫描、排序、自动刷新、KUMO 条件和 Addon API；
- 可选的服务端动画策略同步，未安装 Mo' Bends 的客户端仍可加入。

## 开发环境

- Windows；
- JDK 25 用于 Gradle；
- Gradle Wrapper 9.7.0；
- Stonecutter 0.10；
- 当前 implemented 的 Fabric 26.2 依赖由 Gradle 自动解析；planned 节点不会解析对应
  Minecraft/Loader 依赖。

详情见 [开发入门](docs/development/getting-started.md)。

## 构建、测试和运行

```powershell
.\gradlew.bat spotlessApply
.\gradlew.bat spotlessCheck verifyTargets testAll checkAll buildAll collectArtifacts `
  --configuration-cache --configuration-cache-problems=fail `
  --no-daemon --stacktrace --console=plain
.\gradlew.bat :fabric-26.2:runClient -Pruntime.world=<save-directory> -Pruntime.username=<profile>
```

当前唯一生产 JAR 位于 `build/artifacts/fabric-26.2/`，文件名为
`mobends-<mod-version>+fabric-26.2.jar`。所有声明目标的运行实例仍保留在
`run/<target>/<side>/`，但只能通过 implemented Target 的统一 Gradle 任务启动。`run/` 是受
Git 忽略的本地用户数据；干净 checkout 不要求预先存在任何实体运行目录。

## 源码结构

- `src/main`、`src/test`：五目标共享内容；
- `src/minecraft-1x`、`src/minecraft-26x`：Minecraft 版本家族实现；
- `src/fabric`、`src/forge`、`src/neoforge-common`、`src/neoforge-1x`、`src/neoforge-26x`：加载器实现；
- `versions/<target>/src`：精确目标覆盖；
- `stonecutter.properties.toml`：`mod.*`、`deps.*`、`target.*` 中央属性；
- `docs/`：架构、开发、测试和上游行为参考。

完整说明见 [项目布局](docs/architecture/project-layout.md)。

## Addon API 边界

公开 Addon API 继续保留 `goblinbob.mobends` 包名以及注册、生命周期、Bender、动画和 KUMO
条件能力。旧 1.12.2 Addon JAR 不承诺跨 Minecraft 版本二进制兼容，需要针对具体 Target 重新
编译。详情见 [兼容性策略](docs/development/compatibility-policy.md)。

## 当前已知限制

- 远程包数据库、下载、支持者身份与外部动画编辑器不属于当前功能；
- 没有本地可见披风来源时，披风视觉验收无法完成；
- 第三方自定义盔甲和头戴层需要按具体模组提供兼容样本；
- planned Target 尚不构成当前支持声明；其历史 GUI、渲染和运行结果不能作为当前 PASS 状态。

持续验证范围见 [验收矩阵](docs/testing/acceptance-matrix.md) 和
[运行时验证](docs/testing/runtime-validation.md)。

## Credits

Mo' Bends 最初由 Iwo Plaza（GoblinBob）创作。本项目保留原始许可、版权、公开 mod ID、资源
命名空间、Addon API 包名和兼容标识；完整归属见 [CREDITS.md](CREDITS.md)。

## License

本项目依照仓库中的 [MIT License](LICENSE) 分发。已有源码文件中的原始版权声明继续有效。
