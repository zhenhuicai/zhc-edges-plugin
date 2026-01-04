# JReleaser 配置说明

本项目使用 JReleaser 1.22.0 来发布到 Maven 中央仓库。

## 配置要求

在 `gradle.properties` 文件中已经配置了以下必要信息：

- GPG 签名密钥信息
- Sonatype 用户名和密码
- 项目版本和开发者信息

## 发布到 Maven 中央仓库的步骤

1. 确保所有配置正确：
   ```bash
   ./gradlew jreleaserConfig
   ```

2. 检查 JReleaser 配置：
   ```bash
   ./gradlew jreleaserCheck
   ```

3. 构建并准备发布（包括生成源码和javadoc jar包）：
   ```bash
   ./gradlew clean build
   ```

4. 运行 JReleaser 预览（不实际发布）：
   ```bash
   ./gradlew jreleaserFullRelease -Pjreleaser.dryrun
   ```

5. 实际发布到 Maven 中央仓库：
   ```bash
   ./gradlew jreleaserFullRelease
   ```

## 注意事项

- 确保 GPG 密钥可用
- 确保 Sonatype 账户具有发布权限
- 确保项目已正确配置 POM 信息

## 模块

当前配置了两个模块的发布：

- `plugins-api`: API 模块
- `plugins-test-support`: 测试支持模块