package com.zhm.edges.plugins.api.job;

import com.zhm.edges.plugins.api.Crawler;
import com.zhm.edges.plugins.api.CrawlingProcessBar;
import com.zhm.edges.plugins.api.VendorJobWithStatus;
import com.zhm.edges.plugins.api.job.hook.JobSessionListener;
import com.zhm.edges.plugins.api.job.vo.CrawlerItem;
import com.zhm.edges.plugins.api.job.vo.Job;
import com.zhm.edges.plugins.api.playwright.BrowserTaskCanceller;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface JobContext {

  /**
   * The id of this job
   *
   * @return id of the job
   */
  String jobId();

  /**
   * The session id related to this job context
   *
   * @return id of the session
   */
  String sessionId();

  /**
   * Whether this context is canceling
   *
   * @return true | false
   */
  boolean isCanceling();

  /**
   * Current status of the job session
   *
   * @return job session status
   */
  JobSessionStatus status();

  /**
   * Start time of the job
   *
   * @return start time in milliseconds
   */
  long startTime();

  /**
   * Last update time of the job
   *
   * @return last update time in milliseconds
   */
  long lastUpdateTime();

  /**
   * End time of the job
   *
   * @return end time in milliseconds
   */
  long endTime();

  /**
   * Current round number
   *
   * @return round number
   */
  int round();

  /**
   * Count of watched items
   *
   * @return watched count
   */
  int watchedCount();

  /**
   * Input resources for the job
   *
   * @return list of input paths
   */
  List<Path> inputResources();

  /**
   * Check if there are any input resources
   *
   * @return true if input resources exist
   */
  boolean hasInputResources();

  /**
   * Root directory for the job
   *
   * @return job root path
   */
  Path jobRoot();

  /**
   * Path for logger files
   *
   * @return logger path
   */
  Path loggerPath();

  /**
   * Resource directory
   *
   * @return resource path
   */
  Path resourcePath();

  /**
   * Input resource directory
   *
   * @return input resource path
   */
  Path resourceInPath();

  /**
   * Output resource directory
   *
   * @return output resource path
   */
  Path resourceOutPath();

  /**
   * Job logger instance
   *
   * @return job logger
   */
  JobLogger logger();

  /**
   * Job instance
   *
   * @return job
   */
  Job job();

  /** Mark job as preparing */
  void preparing();

  /** Mark job as prepared */
  void prepared();

  /** Mark job as crawling */
  void crawling();

  /** Mark job as resumed (enter RESUMING then previous active state) */
  void resume();

  /** Mark job as crawled */
  void crawled();

  /** Mark job as done */
  void done();

  /**
   * Mark job as done with a message
   *
   * @param message the completion message
   */
  void done(String message);

  /** Cancel the job */
  void cancel();

  /** Mark job as canceling */
  void markAsCanceling();

  /**
   * Retry the job
   *
   * @return updated job context
   */
  JobContext retry();

  /** Retry watched items */
  void watchedRetry();

  /**
   * Mark vendor as done
   *
   * @param vendor vendor instance
   * @param message final message
   */
  void vendorDone(Crawler vendor, String message);

  /**
   * Count of pending vendors
   *
   * @return pending vendor count
   */
  int pendingVendorCount();

  /**
   * Get pending vendors
   *
   * @return set of pending vendors
   */
  Set<Crawler> getPendingVendors();

  /**
   * Get all vendors status
   *
   * @return map of vendors to their status
   */
  Map<String, VendorJobWithStatus> allVendorsStatus();

  /**
   * Get vendor names
   *
   * @return list of vendor names
   */
  List<String> vendorNames();

  /**
   * Get normalized inputs
   *
   * @return set of normalized inputs
   */
  Set<String> getNormalizedInputs();

  /** Get normalized inputs */
  Set<String> getNormalizedInputs(Crawler crawler);

  /**
   * Get vendor browser task cancellers
   *
   * @return map of vendors to their browser task cancellers
   */
  Map<Crawler, BrowserTaskCanceller> getVendorBrowserTaskCancellerMap();

  /**
   * Get job results
   *
   * @return map of results
   */
  Map<String, List<JobResultItem>> getResults();

  /**
   * Publish message during execution
   *
   * @param message the message to publish
   * @param <T> generic type of message
   */
  <T> void publish(T message);

  /**
   * Total for progress bar
   *
   * @return total count
   */
  int processBarTotal();

  /**
   * Finished count for progress bar
   *
   * @return finished count
   */
  int processBarFinishedCount();

  /**
   * Set input resources
   *
   * @param inputResources list of input paths
   * @return updated job context
   */
  JobContext setInputResources(List<Path> inputResources);

  /**
   * Set vendor names
   *
   * @param vendorNames list of vendor names
   * @return updated job context
   */
  JobContext setVendorNames(List<String> vendorNames);

  /**
   * Set normalized inputs
   *
   * @param inputs set of normalized inputs
   */
  void setNormalizedInputs(Set<String> inputs);

  /** Set normalized inputs Map */
  void setNormalizedInputsMap(Crawler crawler, Set<String> normalizedInputs);

  /**
   * Set job session listener
   *
   * @param jobCrawlerListener the listener
   */
  void setListener(JobSessionListener jobCrawlerListener);

  /**
   * Set start time
   *
   * @param startTime start time in milliseconds
   */
  void setStartTime(long startTime);

  /**
   * Set last update time
   *
   * @param lastUpdateTime last update time in milliseconds
   */
  void setLastUpdateTime(long lastUpdateTime);

  /**
   * Set end time
   *
   * @param endTime end time in milliseconds
   */
  void setEndTime(long endTime);

  /**
   * Set pending vendors
   *
   * @param qualifyVendors list of qualifying vendors
   */
  void setPendingVendors(List<Crawler> qualifyVendors);

  /**
   * Set pending vendor set
   *
   * @param pendingVendorSet set of pending vendors
   */
  void setPendingVendorSet(Set<Crawler> pendingVendorSet);

  /**
   * Set results
   *
   * @param results map of results
   */
  void setResults(Map<String, List<JobResultItem>> results);

  /**
   * Set progress bar
   *
   * @param processBar progress bar instance
   */
  void setProcessBar(CrawlingProcessBar processBar);

  /**
   * Set pending vendor map
   *
   * @param vendorsStatusMap map of vendors to their status
   */
  void setPendingVendorMap(Map<String, VendorJobWithStatus> vendorsStatusMap);

  /**
   * Set cancel handler
   *
   * @param cancel cancel handler
   */
  void cancelHandler(Cancel cancel);

  /**
   * Add vendor canceller
   *
   * @param vendor vendor instance
   * @param browserTaskCanceller browser task canceller
   */
  void addVendorCanceller(Crawler vendor, BrowserTaskCanceller browserTaskCanceller);

  /**
   * Add crawler result
   *
   * @param vendor vendor instance
   * @param item crawler item
   * @param input input string
   */
  void addCrawlerResult(Crawler vendor, CrawlerItem item, String input);

  /**
   * Initialize progress bar
   *
   * @param i total count
   */
  void initProcessBar(int i);

  void startVendorCrawling(Crawler crawler);

  void vendorDone(Crawler crawler);

  void vendorStatus(Crawler crawler, JobSessionVendorStatus jobSessionVendorStatus);

  void canceled();

  /** Job result item record */
  record JobResultItem(Crawler vendor, CrawlerItem item) {}

  /** Cancel interface */
  interface Cancel {
    void start();
  }
}
