package com.zhm.edges.plugins.api;

public abstract class VendorConfiguration<T extends VendorConfiguration> {

  protected long elementWaitTimeout;
  protected long navigatorTimeout;

  /** Whether forbidden to access when anonymous */
  protected boolean forbidAnonymous;

  // Also supply schema so the front-end can config it

  protected Long delayGapTimeInMillSeconds;

  /** Search top n of the item list */
  protected int topN;

  public long getElementWaitTimeout() {
    return elementWaitTimeout;
  }

  public VendorConfiguration<T> setElementWaitTimeout(long elementWaitTimeout) {
    this.elementWaitTimeout = elementWaitTimeout;
    return this;
  }

  public long getNavigatorTimeout() {
    return navigatorTimeout;
  }

  public VendorConfiguration<T> setNavigatorTimeout(long navigatorTimeout) {
    this.navigatorTimeout = navigatorTimeout;
    return this;
  }

  public boolean isForbidAnonymous() {
    return forbidAnonymous;
  }

  public VendorConfiguration<T> setForbidAnonymous(boolean forbidAnonymous) {
    this.forbidAnonymous = forbidAnonymous;
    return this;
  }

  public Long getDelayGapTimeInMillSeconds() {
    return delayGapTimeInMillSeconds;
  }

  public VendorConfiguration<T> setDelayGapTimeInMillSeconds(Long delayGapTimeInMillSeconds) {
    this.delayGapTimeInMillSeconds = delayGapTimeInMillSeconds;
    return this;
  }

  public int getTopN() {
    return topN;
  }

  public VendorConfiguration<T> setTopN(int topN) {
    this.topN = topN;
    return this;
  }
}
