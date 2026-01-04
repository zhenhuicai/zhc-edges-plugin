package com.zhm.edges.plugins.api.job;

import com.zhm.edges.plugins.api.*;
import com.zhm.edges.plugins.api.Crawler;
import com.zhm.edges.plugins.api.job.hook.JobSessionListener;
import com.zhm.edges.plugins.api.job.vo.CrawlerItem;
import com.zhm.edges.plugins.api.job.vo.Job;
import com.zhm.edges.plugins.api.playwright.BrowserTaskCanceller;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class DefaultJobContext implements JobContext {
  final Job job;
  final String sessionId;
  final Path jobRoot;
  final Path loggerPath;
  final Path resourcePath;
  final Path resourceInPath;
  final Path resourceOutPath;
  final JobLogger logger;
  long startTime = System.currentTimeMillis();
  long lastUpdateTime = startTime;
  long endTime;
  protected transient JobSessionListener listener = JobSessionListener.DEAD;
  protected List<String> vendorNames;
  private Set<String> normalizedInputs;
  private Map<String, Set<String>> normalizedInputsMap = new LinkedHashMap<>();
  protected List<Path> inputResources;
  protected Map<String, List<JobResultItem>> results = new LinkedHashMap<>();
  protected Map<Crawler, BrowserTaskCanceller> vendorBrowserTaskCancellerMap = new HashMap<>();
  protected int round = 1;
  int watchedCount = 0;
  private volatile JobSessionStatus status = JobSessionStatus.INIT;
  private volatile boolean canceling = false;
  protected transient volatile Cancel cancel;
  protected transient Map<String, VendorJobWithStatus> vendorJobWithStatusMap;
  protected transient Set<Crawler> pendingVendorSet;
  protected CrawlingProcessBar processBar = CrawlingProcessBar.DEAD;

  public DefaultJobContext(
      Job job,
      final String sessionId,
      final Path jobRoot,
      final Path loggerPath,
      final Path resourcePath,
      final Path resourceInPath,
      final Path resourceOutPath,
      final JobLogger logger) {
    this.job = job;
    this.jobRoot = jobRoot;
    this.loggerPath = loggerPath;
    this.resourcePath = resourcePath;
    this.resourceInPath = resourceInPath;
    this.resourceOutPath = resourceOutPath;
    this.logger = logger;
    this.sessionId = sessionId;
  }

  public DefaultJobContext(
      Job job,
      final Path jobRoot,
      final Path loggerPath,
      final Path resourcePath,
      final Path resourceInPath,
      final Path resourceOutPath,
      final JobLogger logger) {
    this(
        job,
        UUID.randomUUID().toString(),
        jobRoot,
        loggerPath,
        resourcePath,
        resourceInPath,
        resourceOutPath,
        logger);
  }

  @Override
  public String jobId() {
    return job.getJobId();
  }

  @Override
  public String sessionId() {
    return sessionId;
  }

  @Override
  public boolean isCanceling() {
    return canceling;
  }

  @Override
  public JobSessionStatus status() {
    return status;
  }

  @Override
  public long startTime() {
    return startTime;
  }

  @Override
  public long lastUpdateTime() {
    return lastUpdateTime;
  }

  @Override
  public long endTime() {
    return endTime;
  }

  @Override
  public int round() {
    return round;
  }

  @Override
  public int watchedCount() {
    return watchedCount;
  }

  @Override
  public List<Path> inputResources() {
    return inputResources;
  }

  @Override
  public boolean hasInputResources() {
    return inputResources != null && !inputResources.isEmpty();
  }

  @Override
  public Path jobRoot() {
    return jobRoot;
  }

  @Override
  public Path loggerPath() {
    return loggerPath;
  }

  @Override
  public Path resourcePath() {
    return resourcePath;
  }

  @Override
  public Path resourceInPath() {
    return resourceInPath;
  }

  @Override
  public Path resourceOutPath() {
    return resourceOutPath;
  }

  @Override
  public JobLogger logger() {
    return logger;
  }

  @Override
  public Job job() {
    return job;
  }

  @Override
  public void preparing() {
    JobSessionStatus old = status;
    this.status = JobSessionStatus.PREPARING;
    listener.status(this, old, status, null, null);
  }

  @Override
  public void prepared() {
    JobSessionStatus old = status;
    this.status = JobSessionStatus.PREPARED;
    listener.status(this, old, status, null, null);
  }

  @Override
  public void crawling() {
    this.status = JobSessionStatus.CRAWLING;
  }

  @Override
  public void crawled() {
    this.status = JobSessionStatus.CRAWLED;
  }

  @Override
  public void done() {
    done(null);
  }

  @Override
  public void done(String message) {
    if (status == JobSessionStatus.DONE) {
      // TODO fixme
      return;
    }
    status = JobSessionStatus.DONE;
    endTime = System.currentTimeMillis();
    lastUpdateTime = endTime;
    // FIXME just a simple track: we need to trade off abnormal case
    if (message != null) {
      logger.info("job mark done with message: " + message);
    }
    listener.done(this);
  }

  @Override
  public void cancel() {
    logger.info("Prepare cancel job: " + job.getJobId());
    if (cancel != null) {
      try {
        cancel.start();
      } catch (Throwable throwable) {
        logger.info(
            "fail cancel job id: " + job.getJobId() + " session id: " + sessionId, throwable);
      }
    }
  }

  @Override
  public void markAsCanceling() {
    this.canceling = true;
  }

  @Override
  public JobContext retry() {
    this.round += 1;
    return this;
  }

  @Override
  public void watchedRetry() {
    watchedCount++;
  }

  @Override
  public void vendorDone(Crawler vendor, String message) {
    vendorBrowserTaskCancellerMap.remove(vendor);
    lastUpdateTime = System.currentTimeMillis();
    VendorJobWithStatus vendorJobWithStatus = vendorJobWithStatusMap.get(vendor.id());
    if (vendorJobWithStatus != null) {
      JobSessionVendorStatus old = vendorJobWithStatus.status;
      vendorJobWithStatus.status =
          isCanceling() ? JobSessionVendorStatus.CANCELED : JobSessionVendorStatus.CRAWLED;
      vendorJobWithStatus.updateTime = lastUpdateTime;
      if (message != null) {
        vendorJobWithStatus.lastMessage = message;
      }
      listener.vendorStatus(this, vendor, old, vendorJobWithStatus.status, null, null);
    }
    if (pendingVendorSet != null) {
      pendingVendorSet.remove(vendor);
      if (pendingVendorSet.isEmpty() && !isCanceling()) {
        // Auto trigger the jobs
        done();
      }
    }
  }

  @Override
  public int pendingVendorCount() {
    return pendingVendorSet == null ? 0 : pendingVendorSet.size();
  }

  @Override
  public Set<Crawler> getPendingVendors() {
    return pendingVendorSet;
  }

  @Override
  public Map<String, VendorJobWithStatus> allVendorsStatus() {
    return vendorJobWithStatusMap;
  }

  @Override
  public List<String> vendorNames() {
    return vendorNames;
  }

  @Override
  public Set<String> getNormalizedInputs() {
    return normalizedInputs;
  }

  @Override
  public Set<String> getNormalizedInputs(Crawler crawler) {
    return this.normalizedInputsMap.get(crawler.id());
  }

  @Override
  public Map<Crawler, BrowserTaskCanceller> getVendorBrowserTaskCancellerMap() {
    return vendorBrowserTaskCancellerMap;
  }

  @Override
  public Map<String, List<JobResultItem>> getResults() {
    return results;
  }

  @Override
  public <T> void publish(T message) {
    listener.publish(message);
  }

  @Override
  public int processBarTotal() {
    return processBar != null ? processBar.totalItemsCount : 0;
  }

  @Override
  public int processBarFinishedCount() {
    return processBar != null ? processBar.finishedCount() : 0;
  }

  @Override
  public void initProcessBar(int totalCnt) {
    this.processBar = CrawlingProcessBar.of(totalCnt);
  }

  @Override
  public void startVendorCrawling(Crawler crawler) {
    lastUpdateTime = System.currentTimeMillis();
    VendorJobWithStatus vendorJobWithStatus = vendorJobWithStatusMap.get(crawler.id());
    if (vendorJobWithStatus != null) {
      JobSessionVendorStatus oldStatus = vendorJobWithStatus.status;
      vendorJobWithStatus.status = JobSessionVendorStatus.CRAWLING;
      vendorJobWithStatus.updateTime = lastUpdateTime;
      listener.vendorStatus(this, crawler, oldStatus, JobSessionVendorStatus.CRAWLING, null, null);
    }
  }

  @Override
  public void vendorDone(Crawler crawler) {
    vendorDone(crawler, null);
  }

  @Override
  public void vendorStatus(Crawler crawler, JobSessionVendorStatus jobSessionVendorStatus) {
    vendorStatus(crawler, jobSessionVendorStatus, null, null);
  }

  @Override
  public void canceled() {
    if (status == JobSessionStatus.DONE || status == JobSessionStatus.CANCELED) {
      return;
    }
    status = JobSessionStatus.CANCELED;
    endTime = System.currentTimeMillis();
    lastUpdateTime = endTime;
    logger.info("job mark canceled");
    listener.done(this);
  }

  public void vendorStatus(
      final Crawler vendor,
      final JobSessionVendorStatus status,
      final String remark,
      final Map extra) {
    VendorJobWithStatus vendorJobWithStatus = vendorJobWithStatusMap.get(vendor.id());
    if (vendorJobWithStatus != null) {
      JobSessionVendorStatus old = vendorJobWithStatus.status;
      vendorJobWithStatus.status = status;
      vendorJobWithStatus.updateTime = lastUpdateTime;
      if (remark != null) {
        vendorJobWithStatus.lastMessage = remark;
      }
      if (extra != null) {
        vendorJobWithStatus.extra = extra;
      }
      listener.vendorStatus(this, vendor, old, status, remark, extra);
    } else {
      logger.warn(
          "fail switch vendor {} status to {}, as this vendor lost from context",
          vendor.id(),
          status);
    }
  }

  @Override
  public void addCrawlerResult(Crawler vendor, CrawlerItem item, String input) {
    final List<JobResultItem> bucket = results.computeIfAbsent(input, s -> new ArrayList<>());
    bucket.add(new JobResultItem(vendor, item));
    processBar.incrementAndGet();
    listener.result(this, vendor, item, input);
  }

  @Override
  public JobContext setInputResources(List<Path> inputResources) {
    this.inputResources = inputResources;
    return this;
  }

  @Override
  public JobContext setVendorNames(List<String> vendorNames) {
    this.vendorNames = vendorNames;
    return this;
  }

  @Override
  public void setNormalizedInputs(Set<String> inputs) {
    this.normalizedInputs = inputs;
  }

  @Override
  public void setNormalizedInputsMap(Crawler crawler, Set<String> normalizedInputs) {
    this.normalizedInputsMap.put(crawler.id(), normalizedInputs);
  }

  @Override
  public void setListener(JobSessionListener jobCrawlerListener) {
    this.listener = jobCrawlerListener;
  }

  @Override
  public void setStartTime(long startTime) {}

  @Override
  public void setLastUpdateTime(long lastUpdateTime) {}

  @Override
  public void setEndTime(long endTime) {}

  @Override
  public void setPendingVendors(List<Crawler> qualifyVendors) {
    vendorJobWithStatusMap =
        qualifyVendors.stream()
            .collect(Collectors.toConcurrentMap(Crawler::id, VendorJobWithStatus::new));
    pendingVendorSet = new HashSet<>(qualifyVendors);
  }

  @Override
  public void setResults(Map<String, List<JobResultItem>> results) {
    this.results = results;
  }

  @Override
  public void setPendingVendorSet(Set<Crawler> pendingVendorSet) {
    this.pendingVendorSet = pendingVendorSet;
  }

  @Override
  public void setProcessBar(CrawlingProcessBar processBar) {
    this.processBar = processBar;
  }

  @Override
  public void cancelHandler(Cancel cancel) {
    this.cancel = cancel;
  }

  @Override
  public void addVendorCanceller(Crawler vendor, BrowserTaskCanceller canceller) {
    vendorBrowserTaskCancellerMap.put(vendor, canceller);
  }

  @Override
  public void setPendingVendorMap(Map<String, VendorJobWithStatus> vendorsStatusMap) {
    this.vendorJobWithStatusMap = vendorsStatusMap;
  }

  @Override
  public void resume() {
    JobSessionStatus old2 = this.status;
    this.status = JobSessionStatus.CRAWLING;
    listener.status(this, old2, this.status, null, null);
  }
}
