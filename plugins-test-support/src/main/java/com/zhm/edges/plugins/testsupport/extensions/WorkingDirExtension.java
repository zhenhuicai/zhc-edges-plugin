package com.zhm.edges.plugins.testsupport.extensions;

import com.zhm.edges.plugins.api.WorkingConfiguration;
import com.zhm.edges.plugins.api.WorkingDirContext;
import com.zhm.edges.plugins.api.bootstrap.WorkingDirBootstrap;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class WorkingDirExtension implements BeforeAllCallback, AfterAllCallback {
  private final String testName;
  private WorkingDirBootstrap bootstrap;
  private Path workDir;

  public WorkingDirExtension(String testName) {
    this.testName = testName;
  }

  @Override
  public void beforeAll(ExtensionContext context) throws Exception {
    Path base = Paths.get("build", "test-work");
    workDir = base.resolve(testName);
    try {
      Files.createDirectories(workDir);
    } catch (Exception ignored) {}
    System.setProperty("work.dir", workDir.toAbsolutePath().toString());

    WorkingConfiguration cfg = new WorkingConfiguration().setDir(workDir.toAbsolutePath().toString());
    bootstrap = new WorkingDirBootstrap(cfg);
    bootstrap.start();
    Path loggerDir = WorkingDirContext.INSTANCE.logger;
    if (loggerDir != null) {
      System.setProperty("LOG_FILE", loggerDir.resolve("app.log").toAbsolutePath().toString());
    }
  }

  @Override
  public void afterAll(ExtensionContext context) throws Exception {}

  public Path workDir() {
    return workDir;
  }
}
