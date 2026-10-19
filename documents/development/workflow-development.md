# 开发与构建

使用 JDK 25 运行 Wrapper；各 Target 按事实文件选择 Java 17、21 或 25。所有 Target 的 Minecraft 源码各自维护；纯 Java 动画逻辑由 `animation_core` 组件提供。

## 构建

```powershell
.\gradlew.bat spotlessApply
.\gradlew.bat check verifyRelease --configuration-cache --configuration-cache-problems=fail
.\gradlew.bat :version:26.3-neoforge:build
```

发行 JAR 收集到 `build/release/5.1.7/`，打包不会安装到实例。

JVM 测试使用各 Target 的 `build/test-work/` 作为工作目录，日志不写入源码根。`check` 继续使用严格模式；本轮用户授权保留的 P-04 可选服务端偏差与 T-07 提示仍报告失败，不表示完整门禁通过，逐项理由见[模板边界](porting-template.md)。

## 运行

```powershell
.\gradlew.bat :version:26.3-neoforge:runClient
.\gradlew.bat :version:26.3-neoforge:runClientMultiplayer
```

日常实例采用 `instances/<target>/<variant>/`。共享设置在 `instances.toml`；本机输入路径在忽略的 `local.toml`。参考端运行只使用自身实例，不操作其他实现或项目。

## 验证边界

迁移保存校验由 `acceptance_checks` 组件执行。原始审计与历史运行记录保存在各验证的 `result/`，其旧提交绑定、路径与 PASS 范围保持原文。

本轮只要求参考端运行；其他端构建通过不能替代实际动画验收。后续玩法修复从审计索引逐项处理。
