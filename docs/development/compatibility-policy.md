# 兼容性策略

## Minecraft 与加载器

只有 `fabric-26.2` 当前进入编译、测试、运行和发布生命周期。四个 Forge/NeoForge Target 作为
planned 结构保留，不声明当前兼容性，也不把其历史静态或视觉结果作为当前证据。未来重新激活
后，每个 Target 必须独立编译、测试和运行，不能从另一个版本外推。GUI 使用目标 Minecraft
版本自身的页面、控件、导航和焦点行为；只有领域模型、必要文案和版本 API 能力可以产生差异。
第三方模组兼容逻辑由 Mo' Bends 承载，不要求第三方项目重新编译。

## 稳定标识

`mobends`、`assets/mobends`、`goblinbob.mobends`、现有配置键、网络标识、资源路径和存档兼容
标识属于兼容面。许可证、原始版权声明和公开 Addon API 类型不得因独立维护而删除或改名。

## Addon API

正式源码级兼容面包括：

- `AddonHelper.registerAddon` 和 `Addons.getRegistered()`；
- `IAddon` 的注册、显示名称、tick、render 和 refresh 生命周期；
- `AddonAnimationRegistry` 的 Bender、动画和 KUMO 条件注册；
- KEYFRAME、PROCEDURAL、嵌入动画、组合条件和本地 BendsPack 数据。

旧 Minecraft 1.12.2 Addon JAR 不承诺跨版本二进制兼容；Addon 必须针对具体 Target 重新编译。
外部动画编辑器、Previewer、远程数据库、在线下载和支持者服务不是正式兼容面。

## BendsPack

本地包兼容覆盖旧单文件格式、目录包、ZIP/KUMO 包、嵌入动画、条件组合、优先级和可恢复的
错误处理。包只从本机目录读取；自动目录检测采用与对应 Minecraft 资源包页面一致的生命周期，
不恢复远程检索或下载。
