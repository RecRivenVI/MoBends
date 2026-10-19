# 模板迁移保存检查

## 目标

核对从签名检查点迁移的实例与历史验证数据逐文件内容不变。本项不证明动画视觉或跨 Target 行为等价。

## 运行

```powershell
.\gradlew.bat :acceptance_checks:verifyPreservation
.\gradlew.bat check verifyRelease --configuration-cache --configuration-cache-problems=fail
```

`acceptance_checks` 为包含构建。根 `check` 只检查保存清单的路径与哈希格式，不要求干净检出包含用户实例；逐文件内容核对必须显式运行 `verifyPreservation`，两者不混为同一结果。

## 通过标准

保存清单中的文件均存在，长度与 SHA-256 一致；结果写入 `result/preservation.json`。构建、发行和严格合规另按实际结果报告，模板运行端缺口不得隐瞒。
