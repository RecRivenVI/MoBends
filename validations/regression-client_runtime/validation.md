# 历史验证记录：regression-client_runtime

## 目标

保留旧布局验证的原始结果、时间与路径绑定。历史 PASS 不转换成本轮产品验收。

## 运行

```powershell
.\gradlew.bat check
```

当前命令执行迁移保存检查。旧验证操作说明保存在 `result/baseline-validation.md`，旧脚本已随退役布局进入回收站；不再作为活动入口。

## 通过标准

历史结果原文保持；实例文件与迁移清单匹配。当前模板与参考端验收见 `migration-template`；本目录不声明新运行 PASS。
