package com.zhm.edges.plugins.api.playwright;

import static com.zhm.edges.plugins.api.utils.Utils.safeRunner;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Playwright;
import com.zhm.edges.plugins.api.Crawler;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public abstract class BrowserTaskProcessor<T extends BrowserTaskProcessor.BrowserTaskWorker> {
  protected final int parallel;
  protected final ArrayBlockingQueue<BrowserTask> tasks;
  protected List<T> workers = new ArrayList<>();
  protected List<Future> futures = new ArrayList<>();
  protected Logger logger = LoggerFactory.getLogger(getClass());

  protected BrowserTaskProcessor(int queueSize, int parallel) {
    this.parallel = parallel;
    tasks = new ArrayBlockingQueue<>(queueSize);
  }

  /**
   * Submit task
   *
   * @param task
   * @return
   * @throws InterruptedException
   */
  public BrowserTaskCanceller submit(BrowserTask task) throws InterruptedException {
    tasks.offer(task, 10, TimeUnit.SECONDS);

    return () -> {
      boolean removed = tasks.removeIf(item -> item.id().equals(task.id()));
      if (removed) {
        return Optional.of(task);
      } else {
        return Optional.empty();
      }
    };
  }

  public void start() {
    AtomicInteger cnt = new AtomicInteger(1);
    ExecutorService executorService =
        Executors.newFixedThreadPool(
            parallel,
            runnable -> {
              final String threadName =
                  getClass().getSimpleName() + "-Executor-" + cnt.getAndIncrement();
              Thread res = new Thread(runnable, threadName);
              res.setUncaughtExceptionHandler(
                  (thread, throwable) -> logger.error(threadName + " exception", throwable));
              return res;
            });

    for (int i = 0; i < parallel; i++) {
      final T worker = work(i);
      workers.add(worker);
      futures.add(executorService.submit(worker));
    }
    postStart();
  }

  public void postStart() {}

  public void stop() {
    for (var future : futures) {
      safeRunner(() -> future.cancel(true));
    }
    for (var work : workers) {
      safeRunner(() -> work.shutdown());
    }
    workers.clear();
    postStop();
  }

  public void postStop() {}

  protected abstract T work(final int idx);

  public abstract class BrowserTaskWorker implements Runnable {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    public final String name;
    protected Playwright playwright;
    protected Browser browser;
    protected volatile boolean closed;

    // Vendor context?

    protected BrowserTaskWorker(String name) {

      this.name = name;
      playwright = playwright();
      browser = browser(playwright);
      browser.onDisconnected(
              browser -> {
                closed = true;
                browserClosed();
                logger.warn("headless browser closed");
              });
    }

    protected abstract Playwright playwright();

    public abstract boolean headless();

    protected abstract Browser browser(final Playwright playwright);

    protected void shutdown() {
      this.closed = true;
      logger.warn("Browser processor shutting down, worker: " + name);
      safeRunner(() -> browser.close());
      safeRunner(() -> playwright.close());
      logger.warn("Browser processor shut down done, worker: " + name);
    }

    protected void browserClosed() {}

    @Override
    public void run() {
      while (true) {
        BrowserTask task = null;
        try {
          if (closed) {
            logger.warn("{} closed already", name);
            break;
          }
          // who pick whose response:
          task = tasks.take();
          MDC.put("vendor", task.vendorName());
          if (!task.go().get()) {
            logger.warn("Task quit before execute {}", task.id());
            // Task canceled this is shortcut
            task.done(BrowserTask.Status.CANCEL);
            continue;
          }
          Benchmark benchmark = Benchmark.of(task.id());
          logger.debug("{} processing task {}", name, task.id());
          task.accept(browser, this);
          logger.debug("Benchmark {}", benchmark.end());
        } catch (InterruptedException e) {
          if (task != null) {
            task.done(BrowserTask.Status.INTERRUPTED, e.getMessage());
          }
          logger.warn("Work: {} , interrupt {}", name, Thread.currentThread().getName());
          Thread.currentThread().interrupt();
          // Clean up things
          shutdown();
          break;
        } catch (Exception e) {
          logger.warn("failed processing task {}", name, e);
        } finally {
          MDC.remove("vendor");
        }
      }
      logger.warn("Browser processor work all done: " + name);
    }
  }

  public abstract class ContextShareableBrowserTaskWorker extends BrowserTaskWorker {
    protected ContextShareableBrowserTaskWorker(String name) {
      super(name);
    }

    public abstract Optional<BrowserContext> borrow(final Crawler vendor);

    public abstract void attach(Crawler vendor, BrowserContext ctx, boolean login);
  }
}
