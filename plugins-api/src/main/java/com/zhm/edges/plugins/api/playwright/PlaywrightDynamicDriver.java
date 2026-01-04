package com.zhm.edges.plugins.api.playwright;

import com.microsoft.playwright.impl.driver.Driver;
import com.zhm.edges.plugins.api.bootstrap.PlaywrightBootstrap;

import java.nio.file.Path;

public class PlaywrightDynamicDriver extends Driver {

  private final Path driverTempDir;

  public PlaywrightDynamicDriver() {
    driverTempDir = PlaywrightBootstrap.checkPlaywrightVersionDir();
    logMessage("created DriverJar: " + driverTempDir);
  }

  @Override
  protected void initialize(Boolean installBrowsers) throws Exception {}

  @Override
  public Path driverDir() {
    return driverTempDir;
  }
}
