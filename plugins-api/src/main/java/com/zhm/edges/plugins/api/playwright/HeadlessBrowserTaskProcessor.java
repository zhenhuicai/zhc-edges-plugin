package com.zhm.edges.plugins.api.playwright;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.microsoft.playwright.*;
import com.zhm.edges.plugins.api.Crawler;
import com.zhm.edges.plugins.api.utils.GlobalExecutors;
import com.zhm.edges.plugins.api.utils.JsonUtil;
import com.zhm.edges.plugins.api.utils.TimeUtils;
import com.zhm.edges.plugins.api.utils.Utils;

import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class HeadlessBrowserTaskProcessor
    extends BrowserTaskProcessor<HeadlessBrowserTaskProcessor.HeadlessBrowserTaskWorker> {

  protected final long CONTEXT_OUT_OF_DATE_TIME_GAP = TimeUnit.SECONDS.toMillis(11);
  ScheduledFuture scheduledFuture;

  public HeadlessBrowserTaskProcessor(int queueSize, int parallel) {
    super(queueSize, parallel);
  }

  @Override
  public void postStart() {
    // Start monitor
    scheduledFuture =
            GlobalExecutors.INSTANCE
                    .globalScheduleExecutor()
                    .scheduleAtFixedRate(
                            new Runnable() {
                              @Override
                              public void run() {
                                // Over 11 minutes not live remove this context
                                final long cutoffTimestamp =
                                        System.currentTimeMillis() - CONTEXT_OUT_OF_DATE_TIME_GAP;

                                final Iterator<HeadlessBrowserTaskWorker> iterator = workers.iterator();
                                final Map statistic = new LinkedHashMap();
                                while (iterator.hasNext()) {
                                  HeadlessBrowserTaskWorker worker = iterator.next();

                                  final String workName = worker.name;

                                  final Iterator<Map.Entry<Crawler, VendorBrowserContext>> contextIterator =
                                          worker.vendorBrowserContextCache.entrySet().iterator();

                                  List workContext = new ArrayList();
                                  statistic.put(workName, workContext);
                                  // All is headless context
                                  while (contextIterator.hasNext()) {
                                    final Map.Entry<Crawler, VendorBrowserContext> vendorContexts =
                                            contextIterator.next();
                                    final Crawler vendor = vendorContexts.getKey();
                                    VendorBrowserContext browserContext = vendorContexts.getValue();
                                    if (browserContext.cachedBrowserContext != null
                                            && browserContext.cachedBrowserContext.lastPageOpen < cutoffTimestamp) {
                                      // You are dead
                                      contextIterator.remove();
                                      browserContext.cachedBrowserContext.release();
                                      logger.warn(
                                              "remove inactive context in worker {} for vendor {}, session long ago: {}",
                                              workName,
                                              vendor.id(),
                                              TimeUtils.getTimeAgo(
                                                      browserContext.cachedBrowserContext.lastPageOpen));
                                    } else {
                                      // Summary it
                                      final Map rowStatistic = new LinkedHashMap();
                                      rowStatistic.put("vendor", vendor.id());
                                      rowStatistic.put(
                                              "lastPageOpened",
                                              TimeUtils.getTimeAgo(
                                                      browserContext.cachedBrowserContext.lastPageOpen));
                                      List pages = new ArrayList();
                                      rowStatistic.put("pages", pages);

                                      for (final var page :
                                              browserContext.cachedBrowserContext.context.pages()) {
                                        Map _page = new LinkedHashMap();
                                        _page.put("url", page.url());
                                        _page.put("title", page.title());
                                        pages.add(_page);
                                      }
                                    }
                                  }
                                }
                                // Todo statistic
                                try {
                                  logger.warn(
                                          "\n\n=======Browser context statistic=======\n\n{}\n\n",
                                          JsonUtil.getObjectMapper()
                                                  .writerWithDefaultPrettyPrinter()
                                                  .writeValueAsString(statistic));
                                } catch (JsonProcessingException e) {
                                  logger.warn("fail to purge and statistic ", e);
                                }
                              }
                            },
                            3,
                            3,
                            TimeUnit.MINUTES);
  }

  @Override
  public void postStop() {
    if (scheduledFuture != null) {
      Utils.safeRunner(
              () -> {
                scheduledFuture.cancel(true);
                scheduledFuture = null;
              });
    }
  }

  public HeadlessBrowserTaskProcessor() {
    this(1 << 10, 2);
  }


  @Override
  protected HeadlessBrowserTaskWorker work(int idx) {
    return new HeadlessBrowserTaskWorker("HeadlessTaskWorker-" + idx);
  }

  /**
   * You need to think about this:
   *
   * @return
   */
  protected BrowserType.LaunchOptions launchOptions() {
    BrowserType.LaunchOptions res = new BrowserType.LaunchOptions();
    List<String> allArgs = new ArrayList<>();
    List<Constant.OptimizesArgs> DEFAULT =
        Arrays.asList(
            Constant.OptimizesArgs.CORE_SECURITY_SANDBOX,
            Constant.OptimizesArgs.BACKGROUND_NETWORK,
            Constant.OptimizesArgs.EXTENSIONS,
            Constant.OptimizesArgs.FEATURE_DISABLE,
            // First 4 seems has issue?
            Constant.OptimizesArgs.NETWORK_SECURITY,
            Constant.OptimizesArgs.UX_OPTIMIZATION,
            Constant.OptimizesArgs.PERFORMANCE_RESOURCE,
            Constant.OptimizesArgs.POPUP_CONTROL,
            Constant.OptimizesArgs.HEADLESS);

    for (final Constant.OptimizesArgs args : DEFAULT) {
      allArgs.addAll(args.args());
    }
    res.setHeadless(true);
    res.setArgs(allArgs);
    res.setTimeout(Constant.LAUNCH_TIME_OUT);
    res.setSlowMo(0); // 无延迟，最大化性能
    return res;
  }

  /** Only when stateless we want to attach， This is totally thread safe */
  public class VendorBrowserContext {

    protected Crawler vendor;

    protected volatile CachedBrowserContext cachedBrowserContext;

    public void attach(BrowserContext context, boolean withLoginState) {
      if (cachedBrowserContext != null) {
        // Release old only keep one
        CachedBrowserContext old = cachedBrowserContext;
        cachedBrowserContext = new CachedBrowserContext(context, withLoginState, this);
        old.release();
      }
    }

    public Optional<CachedBrowserContext> borrow() {
      return Optional.ofNullable(cachedBrowserContext);
    }

    /**
     * This is call back not thread safe
     *
     * @param context
     */
    void contextClosed(CachedBrowserContext context) {
      if (cachedBrowserContext != null && context.id.equals(cachedBrowserContext.id)) {
        cachedBrowserContext = null;
      }
    }
  }

  public class CachedBrowserContext {
    public final BrowserContext context;
    public final boolean login;
    final String id = UUID.randomUUID().toString();
    final VendorBrowserContext parent;
    long lastPageOpen;
    String lastPageUrl, lastPageTitle;

    public CachedBrowserContext(BrowserContext context, VendorBrowserContext parent) {
      this(context, false, parent);
    }

    public CachedBrowserContext(
        BrowserContext context, boolean login, VendorBrowserContext parent) {
      this.context = context;
      this.login = login;
      this.parent = parent;
      context.onClose(cxt -> parent.contextClosed(CachedBrowserContext.this));
      context.onPage(
          page -> {
            lastPageOpen = System.currentTimeMillis();
            lastPageUrl = page.url();
            lastPageTitle = page.title();
          });
    }

    void release() {
      if (context != null) {
        Utils.safeRunner(() -> context.close());
      }
    }
  }

  protected class HeadlessBrowserTaskWorker extends BrowserTaskProcessor<HeadlessBrowserTaskWorker>.ContextShareableBrowserTaskWorker {

    protected Map<Crawler, VendorBrowserContext> vendorBrowserContextCache = new HashMap<>();

    protected HeadlessBrowserTaskWorker(String name) {
      super(name);
    }

    @Override
    protected Playwright playwright() {
      return Playwright.create();
    }

    @Override
    public boolean headless() {
      return true;
    }

    @Override
    public Optional<BrowserContext> borrow(final Crawler vendor) {
      VendorBrowserContext ctx = vendorBrowserContextCache.get(vendor);
      if (ctx != null) {
        Optional<CachedBrowserContext> cachedBrowserContextOptional = ctx.borrow();
        if (cachedBrowserContextOptional.isPresent()) {
          return Optional.ofNullable(cachedBrowserContextOptional.get().context);
        }
      }
      return Optional.empty();
    }

    @Override
    public void attach(Crawler vendor, BrowserContext ctx, boolean login) {
      VendorBrowserContext vendorBrowserContext =
          vendorBrowserContextCache.computeIfAbsent(vendor, vd -> new VendorBrowserContext());
      vendorBrowserContext.attach(ctx, login);
    }

    @Override
    protected Browser browser(Playwright playwright) {
      return playwright.chromium().launch(launchOptions());
    }
  }
}
