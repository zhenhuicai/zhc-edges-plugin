
## Java plugin lib:

参考: https://github.com/halo-dev/halo/blob/main/application/src/main/java/run/halo/app/plugin/

[pf4j](https://pf4j.org/)

每个plugin:

1. 启动加载机制
2. 上下文隔离机制
3. 配置方式
4. 最小子集合，安全机制， 配合 [2]

## 注意

1. 确保 pluginId 全局唯一、稳定，不随版本变动。
2. 采用语义化版本，严格区分破坏性与兼容性变更。

发布maven central

```
# 发布 plugins-api 模块并部署到 Maven Central
./gradlew :plugins-api:publish :plugins-api:jreleaserDeploy

# 发布 plugins-test-support 模块并部署到 Maven Central
./gradlew :plugins-test-support:publish :plugins-test-support:jreleaserDeploy

# 或者只查看配置
./gradlew :plugins-api:publish :plugins-api:jreleaserConfig
```