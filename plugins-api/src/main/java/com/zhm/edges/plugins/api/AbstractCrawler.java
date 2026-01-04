package com.zhm.edges.plugins.api;

import static com.zhm.edges.plugins.api.job.LoggerType.*;
import static com.zhm.edges.plugins.api.utils.Utils.*;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.zhm.edges.plugins.api.job.JobContext;
import com.zhm.edges.plugins.api.playwright.*;
import com.zhm.edges.plugins.api.playwright.customizer.PageCustomizerBuilder;
import com.zhm.edges.plugins.api.utils.Utils;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public abstract class AbstractCrawler<T extends AbstractBrowserTask> implements Crawler, Constant {

  static Logger logger = LoggerFactory.getLogger(Utils.class);

  protected final String id;
  protected final String name;
  protected final String description;
  protected final String homePage;

  protected AbstractCrawler(String id, String name, String description, String homePage) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.homePage = homePage;
  }

  @Override
  public String id() {
    return id;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public String description() {
    return description;
  }

  @Override
  public String homePage() {
    return homePage;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    AbstractCrawler<?> that = (AbstractCrawler<?>) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }

  @Override
  public Optional<BrowserTaskCanceller> job(
      JobContext jobContext, BrowserTaskProcessSupplier supplier) {

    var task = crawlerTask(jobContext, supplier);
    try {
      MDC.put("vendor", id());
      BrowserTaskCanceller canceller = supplier.headlessProcessor().submit(task);
      jobContext.logger().debug(CRAWL_TASK_SCHEDULED);
      return Optional.ofNullable(canceller);
    } catch (InterruptedException e) {
      jobContext.logger().debug(BROWSER_RESOURCE_INSUFFICIENT);
      jobContext.vendorDone(this, e.getMessage());
    } finally {
      MDC.remove("vendor");
    }
    return Optional.empty();
  }

  /**
   * Centralize the common logic used by many crawlers to schedule/submit a standalone login task.
   * Individual crawlers create their own StandaloneLoginTask (with vendor-specific accept() logic)
   * and pass it here; this helper handles MDC, submission and uniform logging on failure.
   */
  protected void scheduleLoginTask(
      BrowserTaskProcessSupplier supplier, AbstractBrowserTask task, JobContext jobContext) {
    try {
      MDC.put("vendor", name());
      supplier.headProcessor().submit(task);
      if (jobContext != null) {
        jobContext.logger().debug(LOGIN_TASK_SCHEDULE);
      } else {
        logger.debug("{} schedule the login task", name());
      }
    } catch (InterruptedException e) {
      if (jobContext != null) {
        jobContext.logger().debug(BROWSER_RESOURCE_INSUFFICIENT);
      } else {
        logger.error("No browser resource to schedule the login job of vendor: {}", name());
      }
    } finally {
      MDC.remove("vendor");
    }
  }

  protected abstract T crawlerTask(JobContext jobContext, BrowserTaskProcessSupplier supplier);

  public BrowserContext browserContext(Browser browser, final boolean headless) {
    Browser.NewContextOptions options = defaultContextOptions();
    options.setBaseURL(homePage);
    // if the state exist then set it
    Path sessionDir = statePath();
    if (sessionDir.toFile().exists()) {
      options.setStorageStatePath(sessionDir);
    }
    if (headless) {
      // FIXME Can this save memory?
      options.setViewportSize(100, 200);
    }
    return browser.newContext(options);
  }

  protected Browser.NewContextOptions defaultContextOptions() {

    Browser.NewContextOptions options = new Browser.NewContextOptions();

    options.setViewportSize(DEF_VIEW_WIDTH, DEF_VIEW_HEIGHT);
    options.setUserAgent(DEF_USER_AGENT);

    // Note: Timeout settings need to be set after BrowserContext creation via
    // context.setDefaultTimeout()
    // Disable unnecessary features to improve performance
    options.setJavaScriptEnabled(true); // Keep JavaScript enabled, most modern websites require it
    options.setAcceptDownloads(false); // Disable downloads by default

    options.setPermissions(java.util.List.of("geolocation", "notifications"));

    // 设置语言和地区
    options.setLocale("zh-CN");
    options.setTimezoneId("Asia/Shanghai");

    return options;
  }

  public Page page(BrowserContext browserContext, final boolean headless) {
    Page page = browserContext.newPage();
    if (headless) {
      // TODO if headless?
      PageCustomizerBuilder.DEFAULT.customize(page);
    }
    return page;
  }

  protected void releasePage(final Page page) {
    if (page != null) {
      Utils.safeRunner(() -> page.close());
    }
  }

  public void storageState(Page page) {
    page.context()
        .storageState(
            new BrowserContext.StorageStateOptions().setIndexedDB(true).setPath(statePath()));
  }

  public void saveCredential(final Supplier<String> payloadSupplier, JobContext jobContext) {
    try {
      String key = Files.readString(WorkingDirContext.INSTANCE.config().resolve("aes"));
      String credential = encrypt(payloadSupplier.get(), stringToKey(key));
      String name = credential.replaceAll("[^a-zA-Z0-9]", "");
      if (name.length() > 18) {
        name = name.substring(0, 18);
      }
      Path cDir = resolveSubDirs(configurationPath(), true, "credential");
      // Write it back:
      Files.writeString(cDir.resolve(name), credential);
    } catch (Throwable e) {
      logger.warn("Failed to save credential", e);
      jobContext.logger().error(CREDENTIAL_SAVE_FAILED);
    }
  }

  public Integer estimateLeadTime(String delivery) {
    if (StringUtils.isBlank(delivery)) {
      return null;
    }

    if (delivery.contains("当日发货")) {
      return 0;
    } else if (delivery.contains("次日发货")) {
      return 1;
    } else {
      // 使用正则表达式匹配数字和单位
      Pattern pattern = Pattern.compile("(\\d+).*?([日周月年天])");
      Matcher matcher = pattern.matcher(delivery);

      if (matcher.find()) {
        int number = Integer.parseInt(matcher.group(1));
        String unit = matcher.group(2).trim();

        // 计算天数
        int daysToAdd = 0;
        if (unit.contains("日") || unit.contains("天")) {
          daysToAdd = number;
        } else if (unit.contains("周")) {
          daysToAdd = number * 7;
        } else if (unit.contains("月")) {
          daysToAdd = number * 30; // 简化处理，每月按30天计算
        }

        return daysToAdd;
      }
    }
    return null;
  }
}
