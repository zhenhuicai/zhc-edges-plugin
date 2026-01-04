package com.zhm.edges.plugins.testsupport.extensions;

import com.zhm.edges.plugins.api.WorkingConfiguration;
import com.zhm.edges.plugins.api.bootstrap.PlaywrightBootstrap;
import com.zhm.edges.plugins.api.bootstrap.WorkingDirBootstrap;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import com.zhm.edges.plugins.api.playwright.BrowserTaskEngine;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class BrowserEngineExtension implements BeforeAllCallback, AfterAllCallback {
  private static BrowserTaskEngine shared;
  private BrowserTaskEngine instance;
  private final boolean useShared;

  private BrowserEngineExtension(boolean useShared) {
    this.useShared = useShared;
  }

  public static BrowserEngineExtension shared() {
    return new BrowserEngineExtension(true);
  }

  public static BrowserEngineExtension isolated() {
    return new BrowserEngineExtension(false);
  }

  @Override
  public void beforeAll(ExtensionContext context) throws Exception {
    String name = context.getRequiredTestClass().getSimpleName();
    Path workDir = Paths.get("build", "test-work", name).toAbsolutePath();
    try { Files.createDirectories(workDir); } catch (Exception ignored) {}
    System.setProperty("work.dir", workDir.toString());
    WorkingConfiguration cfg = new WorkingConfiguration().setDir(workDir.toString());
    new WorkingDirBootstrap(cfg).start();

    PlaywrightBootstrap playwrightBootstrap = new PlaywrightBootstrap();
    playwrightBootstrap.start();
    if (useShared) {
      if (shared == null) {
        shared = new BrowserTaskEngine();
        shared.start();
      }
      instance = shared;
    } else {
      instance = new BrowserTaskEngine();
      instance.start();
    }
  }

  @Override
  public void afterAll(ExtensionContext context) throws Exception {
    if (!useShared && instance != null) {
      instance.stop();
    }
  }

  public BrowserTaskEngine get() {
    return instance;
  }
}
