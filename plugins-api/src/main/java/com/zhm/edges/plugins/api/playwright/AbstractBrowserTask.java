package com.zhm.edges.plugins.api.playwright;

import static com.zhm.edges.plugins.api.utils.Utils.safeRunner;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import java.util.function.Supplier;

public abstract class AbstractBrowserTask implements BrowserTask {
  protected final String id;
  protected final Supplier<Boolean> go;
  protected final String vendorName;
  protected final String vendorId;

  protected AbstractBrowserTask(
      String id, Supplier<Boolean> go, String vendorName, String vendorId) {
    this.id = id;
    this.go = go;
    this.vendorName = vendorName;
    this.vendorId = vendorId;
  }

  @Override
  public String id() {
    return id;
  }

  @Override
  public Supplier<Boolean> go() {
    return go;
  }

  @Override
  public String vendorName() {
    return vendorName;
  }

  @Override
  public String vendorId() {
    return vendorId;
  }

  /**
   * This is safe process page make sure the resource is released
   *
   * @param context the browser context
   * @param page the page
   * @param operation action
   */
  protected void processPage(
      final BrowserContext context, final Page page, final Operation operation) {
    try {
      operation.accept(context, page);
    } finally {
      safeRunner(() -> page.close());
      safeRunner(() -> context.close());
    }
  }

  protected void release(final BrowserContext context, final Page page) {
    if (page != null) {
      safeRunner(() -> page.close());
    }
    if (context != null) {
      safeRunner(() -> context.close());
    }
  }

  @FunctionalInterface
  public interface Operation {
    void accept(final BrowserContext context, final Page page);
  }
}
