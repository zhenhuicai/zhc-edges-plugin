package com.zhm.edges.plugins.api.playwright;

import com.zhm.edges.plugins.api.bootstrap.BootStrapReadyEvent;
import jakarta.annotation.PreDestroy;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;

@Service
public class BrowserTaskEngine implements BrowserTaskProcessSupplier {

  protected AtomicBoolean started = new AtomicBoolean(false);
  Logger logger = LoggerFactory.getLogger(BrowserTaskEngine.class);
  HeadBrowserTaskProcessor headBrowserTaskProcessor;
  HeadlessBrowserTaskProcessor headlessBrowserTaskProcessor;

  public BrowserTaskEngine() {
    headBrowserTaskProcessor = new HeadBrowserTaskProcessor();
    headlessBrowserTaskProcessor = new HeadlessBrowserTaskProcessor();
  }

  @EventListener(BootStrapReadyEvent.class)
  public void start() {

    if (started.compareAndSet(false, true)) {
      long MB = 1024 * 1024;

      final Runtime runtime = Runtime.getRuntime();
      final long freeMemory = runtime.freeMemory();
      final SystemInfo si = new SystemInfo();
      final GlobalMemory memory = si.getHardware().getMemory();
      final long systemMemory = memory.getAvailable();
      headBrowserTaskProcessor.start();
      headlessBrowserTaskProcessor.start();
      long _freeMemory = runtime.freeMemory();
      long _systemMemory = memory.getAvailable();

      StringBuffer statistic = new StringBuffer();
      statistic.append(
          String.format("\t%-32s  %64s\n", "JVM Cost", (freeMemory - _freeMemory) / MB + " MB"));

      statistic.append(
          String.format(
              "\t%-32s  %64s\n", "System Cost", (systemMemory - _systemMemory) / MB + " MB"));

      logger.warn("\n\nMemory Statistic\n{}\n", statistic);

      // TODO monitor the health...
    }
  }

  @PreDestroy
  public void stop() {
    if (started.compareAndSet(true, false)) {
      headBrowserTaskProcessor.stop();
      headlessBrowserTaskProcessor.stop();
    }
  }

  @Override
  public HeadBrowserTaskProcessor headProcessor() {
    return headBrowserTaskProcessor;
  }

  @Override
  public HeadlessBrowserTaskProcessor headlessProcessor() {
    return headlessBrowserTaskProcessor;
  }
}
