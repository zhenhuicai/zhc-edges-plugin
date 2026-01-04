package com.zhm.edges.plugins.api.playwright;

import com.microsoft.playwright.Browser;
import java.util.function.Supplier;

public interface BrowserTask {

  Runnable EMPTY = () -> {};

  /**
   * The live browser and the work context
   *
   * @param browser
   * @param worker
   */
  void accept(final Browser browser, BrowserTaskProcessor.BrowserTaskWorker worker);

  default String description() {
    return "Access head browser";
  }

  default void done(Status status) {
    done(status, null);
  }

  /**
   * All gone, no matter good or bad
   *
   * @param status
   * @param message
   */
  void done(Status status, final String message);

  /**
   * Identify of this task
   *
   * @return string
   */
  default String id() {
    return "Browser task";
  }

  /**
   * Whether should we keep go one with this job?
   *
   * @return
   */
  default Supplier<Boolean> go() {
    return () -> true;
  }

  default String vendorName() {
    return vendorId();
  }

  String vendorId();

  enum Status {
    OK,
    CANCEL,
    INTERRUPTED,
    EXCEPTION,
  }
}
