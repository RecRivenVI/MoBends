# 迁移与启动校验

`acceptance_checks` 是不随模组发布的验证工具。它按迁移前的逐文件 SHA-256 和长度清单核对用户数据，不解析世界内容，也不启动 Minecraft。

## 输入与输出

输入为 `validations/migration-template/task/preservation.json`，每项写明原路径、现路径、字节数和 SHA-256。输出为该验证的 `result/preservation.json`。

工具只读取仓库内部的目标路径；发现路径越界、文件缺失或内容不符时失败。校验通过只证明所列文件保存完整，不代表动画视觉或多人行为通过。

`verifyStartup` 读取隔离启动验证的 `startup.json`、两张 PNG 和成功退出日志，检查注册、关节姿态推进、分辨率、静音与状态恢复，并把画面及日志收进该验证的结果目录。它不启动或操作游戏，不读取日常实例；输出的有限 smoke PASS 与用户视觉验收 PENDING 分开记录。其 JVM 测试防止只注册不运动、未恢复位置、飞行残留或错误分辨率被当作通过。

## 构建

组件使用 Java 17 兼容代码与 Gson。根 `check` 只检查保存清单的结构，不把用户实例作为干净构建的前置条件；显式 `verifyPreservation` 才读取文件并核对内容。
