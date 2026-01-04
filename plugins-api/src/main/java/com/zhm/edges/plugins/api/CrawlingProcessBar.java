package com.zhm.edges.plugins.api;


import java.util.concurrent.atomic.AtomicInteger;

public class CrawlingProcessBar {
  public static CrawlingProcessBar DEAD =
      new CrawlingProcessBar(0) {
        @Override
        public int addAndGet(int step) {
          return 0;
        }

        @Override
        public int incrementAndGet() {
          return 0;
        }

        @Override
        public int finishedCount() {
          return 0;
        }
      };
  public final int totalItemsCount;
  public AtomicInteger finishedCounter = new AtomicInteger(0);

  CrawlingProcessBar(int totalItemsCount) {
    this.totalItemsCount = totalItemsCount;
  }

  public static CrawlingProcessBar of(final int totalItemsCount) {
    return new CrawlingProcessBar(totalItemsCount);
  }

  public int addAndGet(int step) {
    return finishedCounter.addAndGet(step);
  }

  public int incrementAndGet() {
    return finishedCounter.incrementAndGet();
  }

  public int finishedCount() {
    return finishedCounter.get();
  }
}
