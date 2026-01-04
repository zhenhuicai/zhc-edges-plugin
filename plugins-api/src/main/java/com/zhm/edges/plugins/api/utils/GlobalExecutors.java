package com.zhm.edges.plugins.api.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.slf4j.LoggerFactory;

public enum GlobalExecutors {
  INSTANCE;

  ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE =
      Executors.newScheduledThreadPool(
          2,
          r -> {
            Thread thread = new Thread(r, "Global-Schedule-Executor");
            thread.setPriority(Thread.MIN_PRIORITY);
            thread.setUncaughtExceptionHandler(
                (t, e) ->
                    LoggerFactory.getLogger(GlobalExecutors.class)
                        .warn("Global-Schedule-Executor exception", e));
            return thread;
          });

  public ScheduledExecutorService globalScheduleExecutor() {
    return SCHEDULED_EXECUTOR_SERVICE;
  }
}
