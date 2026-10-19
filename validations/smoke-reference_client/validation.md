# 参考端客户端启动检查

## 目标

确认 26.3 NeoForge 能进入隔离的已有测试世界副本，玩家 Bender 与实体数据正常注册，并记录基本待机、移动画面。结果不替代用户对所有动画、游泳、披风和装备的视觉验收。

## 运行

```powershell
.\gradlew.bat :version:26.3-neoforge:runValidationSmokeReferenceClientClient
.\gradlew.bat :acceptance_checks:verifyStartup
```

迁移任务先把备份中的测试世界复制到本验证自己的 `instance/`。探针只在此运行加载，使用全局静音；记录两张画面、短时间正常前进并释放输入，恢复测试前位置、速度与视角后保存退出。此运行不读写日常实例。

## 通过标准

`result/startup.json` 中玩家、维度、分辨率、Bender 注册和实体数据有效；正常移动有位移，膝关节姿态随动作推进；截图可读；退出时无新增 Mixin 或渲染错误。`verifyStartup` 检查记录并将截图、成功日志和 `startup-check.json` 收入结果目录。报告分别记录启动、动画观察和用户验收，不把注册成功当完整动画 PASS。
