# MCP 参考端动画烟测

## 目标

用固定 MCP alpha 接管参考端的独立测试副本，进入世界、移动、读取画面并正常保存退出。内部 Bender、动画姿态与状态仍由本项目独立探针观察，不用 MCP 代替产品断言。

## 运行

```powershell
.\gradlew.bat :acceptance_checks:prepareMcpAnimation
.\gradlew.bat :version:26.3-neoforge:runValidationSmokeMcpAnimationClient
```

使用本实例端点完成 READ、显式 acquire、语义界面选择、普通移动及截图；结束前释放输入并正常保存退出。不使用宿主键鼠。

## 通过标准

1920×1080、全局静音、玩家 RecRivenVI；世界和玩家有效，实际移动与截图可读，7 类 Bender 注册且玩家动画数据存在。观察退出前的重力、碰撞、飞行与着地状态；输入释放，游戏正常保存退出。完整视觉验收仍由用户完成。
