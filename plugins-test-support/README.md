# plugins-test-support 使用说明

- 依赖：在插件模块 `build.gradle` 增加 `testImplementation(project(':zhm-edges-plugins:plugins-test-support'))`
- 典型用法：
```
@RegisterExtension
static BrowserEngineExtension engine = BrowserEngineExtension.shared();

@Test
void job(){
  Job job = JobFixtures.newJob("A9F18106");
  JobContext ctx = JobFixtures.newContext(job).vendors("zkh").build();
  new ZkhCrawler().job(ctx, engine.get());
}
```
- 扩展职责：
  - `BrowserEngineExtension`：初始化 `work.dir`、启动 `WorkingDirBootstrap` 与 `PlaywrightBootstrap`，并管理 `BrowserTaskEngine` 生命周期。
  - `WorkingDirExtension`：按测试名创建 `build/test-work/<test>` 目录并设置 `LOG_FILE`（可选）。
- 目录与日志：统一输出到 `build/test-work/<TestClass>`，日志文件默认 `app.log`。
- 参数化与标签示例：
```
@ParameterizedTest
@ValueSource(strings = {"A9F18106", "4V21008B"})
@Tag("e2e")
void job(String input){
  Job job = JobFixtures.newJob(input);
  JobContext ctx = JobFixtures.newContext(job).build();
  new ZkhCrawler().job(ctx, engine.get());
}
```
