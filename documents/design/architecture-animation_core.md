# 动画核心组件

`animation_core` 提供不引用 Minecraft 或加载器的数学、关键帧、响应式值、表达式和动画数据契约；各 Target 保留模型、渲染与加载器适配。Java 包和公开类型名称不变。

## 边界

只移动各 Target 有效代码一致、且传递依赖闭合的类型。Minecraft API 的版本差异留在各 Target 内；组件不反向依赖 Target。

## 构建

组件采用 `java-library`。模板把组件 JAR 内容合并到五个发行 JAR，并在开发运行中加入类路径；不会形成玩家需要另装的模组。

原纯库测试移入组件，其余游戏相关测试仍在对应 Target。使用标准 JUnit 运行，发行检查验证合并内容。
