package com.zhm.edges.plugins.api;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.zhm.edges.plugins.api.job.JobContext;
import com.zhm.edges.plugins.api.playwright.AbstractBrowserTask;
import com.zhm.edges.plugins.api.playwright.BrowserTaskProcessSupplier;
import com.zhm.edges.plugins.api.playwright.BrowserTaskProcessor;
import java.util.Optional;
import java.util.function.BiConsumer;

public abstract class HeadlessTask extends AbstractBrowserTask {

  public final JobContext jobContext;
  public final BrowserTaskProcessSupplier supplier;
  protected final AbstractCrawler<?> owner;

  public HeadlessTask(
      AbstractCrawler<?> owner, final JobContext context, final BrowserTaskProcessSupplier supplier) {
    super(context.sessionId(), () -> !context.isCanceling(), owner.name(), owner.id());
    this.owner = owner;
    this.jobContext = context;
    this.supplier = supplier;
  }

  public abstract Page _accept(final BrowserContext context);

  @Override
  public void accept(Browser browser, BrowserTaskProcessor.BrowserTaskWorker worker) {

    BiConsumer<BrowserContext, Page> strategy;
    BrowserContext browserContext;
    if (worker instanceof BrowserTaskProcessor.ContextShareableBrowserTaskWorker shareableWorker) {
      @SuppressWarnings("unchecked")
      Optional<BrowserContext> contextOptional = shareableWorker.borrow(owner);
      if (contextOptional.isPresent()) {
        browserContext = contextOptional.get();
        strategy = (context, page) -> owner.releasePage(page);
      } else {
        browserContext = owner.browserContext(browser, true);
        strategy = (context, page) -> {
          shareableWorker.attach(owner, context, false);
          owner.releasePage(page);
        };
      }
    } else {
      browserContext = owner.browserContext(browser, true);
      strategy = (context, page) -> release(context, page);
    }
    Page page = null;
    try {
      page = _accept(browserContext);
    } finally {
      strategy.accept(browserContext, page);
    }
  }

  @Override
  public void done(Status status, String message) {
    jobContext.vendorDone(owner, message);
  }
}

