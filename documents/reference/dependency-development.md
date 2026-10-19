# 构建依赖与测试输入

构建工具版本由模板 2.0.2 的版本目录提供。各 Target 的加载器版本在 `target.properties`；Fabric API 与 Mod Menu 编译接口也按 Target 记录。

## 产品库

纯 Java 组件使用游戏已提供的 Gson、Guava、JOML 和 SLF4J 类型；不将它们重复打包。JSR-305 仅为编译注解。JUnit 仅用于测试。

## 第三方模组角色

| 对象 | 角色 | 产品依赖 | 兼容声明 |
| --- | --- | --- | --- |
| Fabric API | Fabric 产品运行依赖 | 是，仅 Fabric | 使用其原生事件与网络 API |
| Mod Menu | 既有可选配置入口集成、Fabric 标准测试模组 | 否 | 仅保留已有菜单接口 |
| CustomSkinLoader | 真实皮肤测试输入 | 否 | 安装不构成新兼容承诺 |
| Zume | 用户验收测试输入，精确匹配 Target 后安装 | 否 | 安装不构成兼容承诺；当前 26.3 NeoForge 无匹配版本，跳过 |
| Mine-Craft-Protocol alpha | 隔离实例的自动操作与采证输入 | 否 | 固定快照原样消费，不属于产品兼容声明 |

测试输入的版本与哈希在 `inputs.toml`，本机路径在忽略的 `local.toml`。S1 clean 不加载测试模组；适用的标准客户端加载 CSL，Fabric 另加 Mod Menu。

用户验收参考端使用 Modrinth 明确标记支持 26.3 NeoForge 的 CSL 15.1。旧实例及其他客户端的 15.0.1 输入保留，不覆盖历史数据。自动 MCP 最小检查仅装 MCP；项目烟测加载 MoBends 与只读探针，不混入验收专用皮肤模组。
