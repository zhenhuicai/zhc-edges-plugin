package com.zhm.edges.plugins.api.bootstrap;

import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.impl.driver.Driver;
import com.zhm.edges.plugins.api.WorkingDirContext;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.zhm.edges.plugins.api.playwright.PlaywrightDynamicDriver;
import com.zhm.edges.plugins.api.utils.MultipleThreadDownloader;
import com.zhm.edges.plugins.api.utils.Utils;
import com.zhm.edges.plugins.api.utils.exceptions.EngineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @see PlaywrightDynamicDriver
 */
@Service
public class PlaywrightBootstrap {

  static final Logger logger = LoggerFactory.getLogger(PlaywrightBootstrap.class);
  private static final String PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD = "PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD";
  private static final String SELENIUM_REMOTE_URL = "SELENIUM_REMOTE_URL";
  protected Path driverDir;
  // This for the test
  public boolean installBrowser = true;
  public final AtomicBoolean isInitialized = new AtomicBoolean(false);

  public static Path checkPlaywrightVersionDir() {
    assert WorkingDirContext.INSTANCE.playwright != null : "working dir not init yet";
    Path playwright = WorkingDirContext.INSTANCE.playwright;
    final String version = Playwright.class.getPackage().getImplementationVersion();
    Path versionDir = Paths.get(playwright.toAbsolutePath().toString(), version);
    return versionDir;
  }

  public void stop() {}

  public void start() {
    driverDir = checkPlaywrightVersionDir();
    // if this not exist then we check
    // {workDir}/playwright/{version}/......
    if (!isDriverBundleReady(driverDir)) {
      try {
        Files.createDirectories(driverDir);
      } catch (IOException e) {
        throw new EngineException("fail create version dir " + driverDir, e);
      }
//      download(Playwright.class.getPackage().getImplementationVersion(), driverDir);
      downloadDriver(Playwright.class.getPackage().getImplementationVersion(), driverDir);
    }

    // Should we install the browser?
    if (installBrowser) {
      try {
        // TODO Check where it is installed then check to exist?
        installBrowsers();
      } catch (IOException e) {
        throw new EngineException("init browser fail", e);
      } catch (InterruptedException e) {
        throw new EngineException("init browser interrupt", e);
      }
    }
    isInitialized.set(true);
    logger.info("Playwright driver initialized");
    System.setProperty("playwright.driver.impl", PlaywrightDynamicDriver.class.getCanonicalName());
  }

  void downloadDriver(String version, Path driverDir) {
    String baseUrl = "https://download.zhenhuicai.net/playwright/driver/";
    String platform = Utils.platformDir();
    String fileName = "driver-bundle-" + platform + "-" + version + ".zip";
    String downloadUrl = baseUrl + fileName;

    logger.info("Downloading driver from: {}", downloadUrl);

    try {
      // 下载文件到临时目录
      Path zipFile = driverDir.resolve(fileName);
      MultipleThreadDownloader.downloadFile(downloadUrl, zipFile.toString(), 3, (downloaded, total) ->
              logger.debug("download driver bundle [" + downloaded + "/" + total + "]"));

      // 解压ZIP文件
      Utils.extractZipFile(zipFile, driverDir);
      logger.info("Driver downloaded and extracted to: {} ", driverDir);

      // 删除ZIP文件
      Files.deleteIfExists(zipFile);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      throw new RuntimeException(e);
    }
  }

  protected void installBrowsers() throws IOException, InterruptedException {
    final Map<String, String> env = new LinkedHashMap<>(System.getenv());
    String skip = env.get(PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD);
    if (skip == null) {
      skip = System.getenv(PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD);
    }
    if (skip != null && !"0".equals(skip) && !"false".equals(skip)) {
      logger.debug(
          "Skipping browsers download because `PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD` env variable is set");
      return;
    }
    if (env.get(SELENIUM_REMOTE_URL) != null || System.getenv(SELENIUM_REMOTE_URL) != null) {
      logger.debug("Skipping browsers download because `SELENIUM_REMOTE_URL` env variable is set");
      return;
    }
    Path driver = driverDir();
    if (!Files.exists(driver)) {
      throw new RuntimeException("Failed to find driver: " + driver);
    }
    ProcessBuilder pb = createProcessBuilder();
    // TODO fixme is this chrome right?
    //  install chrome firefox
    pb.command().add("install");
    //pb.command().add("chrome");
    // Force install the chrome

    pb.redirectError(ProcessBuilder.Redirect.INHERIT);
    pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);

    Process p = pb.start();
    // Capture stderr
    StringBuilder error = new StringBuilder();
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()))) {
      String line;
      while ((line = reader.readLine()) != null) {
        error.append(line).append(System.lineSeparator());
      }
    }

    //FIXME if anything corrupt
    //  We need to purge the C:\Users\admin\AppData\Local\ms-playwright\ and re-install
    //  This may fuck us??

    boolean result = p.waitFor(12, TimeUnit.MINUTES);
    if (!result) {
      p.destroy();
      throw new RuntimeException("Timed out waiting for browsers to install");
    }
    if (p.exitValue() != 0) {
      String _error = error.toString();
      if (_error.contains("is already installed")) {
        logger.warn("!!! Chrome is already installed \n" + _error);
      }
      // throw new RuntimeException("Failed to install browsers, exit code: " +
      // p.exitValue()+"\n"+error);
    }
  }

  public ProcessBuilder createProcessBuilder() {
    final Map<String, String> env = new LinkedHashMap<>(System.getenv());
    String nodePath = env.get("PLAYWRIGHT_NODEJS_PATH");
    if (nodePath == null) {
      String node =
          System.getProperty("os.name").toLowerCase().contains("windows") ? "node.exe" : "node";
      nodePath = driverDir().resolve(node).toAbsolutePath().toString();
    }
    ProcessBuilder pb = new ProcessBuilder(nodePath);
    pb.command().add(driverDir().resolve("package").resolve("cli.js").toAbsolutePath().toString());
    pb.environment().putAll(env);
    pb.environment().put("PW_LANG_NAME", "java");
    pb.environment().put("PW_LANG_NAME_VERSION", Utils.getMajorJavaVersion());
    String version = Driver.class.getPackage().getImplementationVersion();
    if (version != null) {
      pb.environment().put("PW_CLI_DISPLAY_VERSION", version);
    }
    return pb;
  }

  public Path driverDir() {
    return driverDir;
  }

  private boolean isDriverBundleReady(Path dir) {
    if (dir == null) {
      return false;
    }
    if (!Files.exists(dir) || !Files.isDirectory(dir)) {
      return false;
    }
    String os = System.getProperty("os.name").toLowerCase();
    String node = os.contains("windows") ? "node.exe" : "node";
    Path nodePath = dir.resolve(node);
    Path cliPath = dir.resolve("package").resolve("cli.js");
    return Files.exists(nodePath) && Files.isRegularFile(nodePath) && Files.exists(cliPath) && Files.isRegularFile(cliPath);
  }
}
