package com.zhm.edges.plugins.api;

import com.zhm.edges.plugins.api.playwright.AbstractBrowserTask;
import com.zhm.edges.plugins.api.playwright.BrowserTaskProcessSupplier;

public abstract class DelegatorHeadTask<T extends AbstractBrowserTask> extends AbstractBrowserTask {

  public final T delegator;
  public final BrowserTaskProcessSupplier supplier;
  protected final AbstractCrawler<?> owner;

  protected DelegatorHeadTask(
      AbstractCrawler<?> owner, T delegator, BrowserTaskProcessSupplier supplier) {
    super(delegator.id(), delegator.go(), owner.name(), owner.id());
    this.owner = owner;
    this.delegator = delegator;
    this.supplier = supplier;
  }

  @Override
  public void done(Status status, String message) {
    try {
      supplier.headlessProcessor().submit(delegator);
    } catch (InterruptedException e) {
      delegator.done(Status.INTERRUPTED, e.getMessage());
    }
  }
}

