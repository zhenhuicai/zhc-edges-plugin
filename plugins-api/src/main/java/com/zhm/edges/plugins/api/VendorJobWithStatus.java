package com.zhm.edges.plugins.api;

import com.zhm.edges.plugins.api.job.JobSessionVendorStatus;

import java.util.LinkedHashMap;
import java.util.Map;

public class VendorJobWithStatus {

  public final Crawler crawler;
  public long startTime = System.currentTimeMillis();
  public String lastMessage;
  public Map<String, Object> extra;
  public JobSessionVendorStatus status = JobSessionVendorStatus.INIT;
  public long updateTime;

  // Or its self statistic like   20/33 sku list etc

  public VendorJobWithStatus(Crawler crawler) {
    this.crawler = crawler;
  }

  public VendorJobWithStatus status(JobSessionVendorStatus status) {
    this.status = status;
    return this;
  }

  public Map<String, Object> getExtra() {
    return extra;
  }

  public VendorJobWithStatus setExtra(Map<String, Object> extra) {
    this.extra = extra;
    return this;
  }

  public VendorJobWithStatus addExtra(final String key, String value) {
    if (extra == null) {
      extra = new LinkedHashMap<>();
    }
    extra.put(key, value);
    return this;
  }

  public long startTime() {
    return startTime;
  }

  public VendorJobWithStatus setLastMessage(String lastMessage) {
    this.lastMessage = lastMessage;
    return this;
  }

  public VendorJobWithStatus setUpdateTime(long updateTime) {
    this.updateTime = updateTime;
    return this;
  }

  public long updateTime() {
    return updateTime;
  }

  public VendorJobWithStatus updateTime(long updateTime) {
    this.updateTime = updateTime;
    return this;
  }

  public String lastMessage() {
    return lastMessage;
  }

  public JobSessionVendorStatus status() {
    return status;
  }

  public VendorJobWithStatus setStatus(JobSessionVendorStatus status) {
    this.status = status;
    return this;
  }
}
