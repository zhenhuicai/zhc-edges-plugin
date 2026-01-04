package com.zhm.edges.plugins.api.job;

/** Every job will have multiple vendors to do the task so this is vendor runtime status */
public enum JobSessionVendorStatus {
  INIT,
  CRAWLING,
  CANCELED,
  CRAWLED,
  /** Wait for login auth */
  PENDING_LOGIN,
  /** Wait for the anti crawler, human check in loop */
  ANTI_CRAWLER,
}
