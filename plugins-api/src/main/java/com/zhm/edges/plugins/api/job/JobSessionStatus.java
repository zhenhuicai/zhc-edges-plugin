package com.zhm.edges.plugins.api.job;

/** Every job only start a session then will have status, some kind like runtime of a job */
public enum JobSessionStatus {
  INIT(1),
  PREPARING(2),
  PREPARED(3),
  CRAWLING(4),
  CANCELED(5),
  CRAWLED(6),
  SUMMARY(7),
  DONE(8);

  final int step;

  JobSessionStatus(int step) {
    this.step = step;
  }

  public int step() {
    return step;
  }
}
