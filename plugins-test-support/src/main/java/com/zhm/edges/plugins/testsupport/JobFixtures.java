package com.zhm.edges.plugins.testsupport;

import static com.zhm.edges.plugins.api.utils.PathUtils.resolveSubDirs;
import static com.zhm.edges.plugins.api.utils.Utils.verifyDirectory;

import com.zhm.edges.plugins.api.Crawler;
import com.zhm.edges.plugins.api.WorkingDirContext;
import com.zhm.edges.plugins.api.job.DefaultJobContext;
import com.zhm.edges.plugins.api.job.DefaultJobLogger;
import com.zhm.edges.plugins.api.job.JobContext;
import com.zhm.edges.plugins.api.job.JobLogger;
import com.zhm.edges.plugins.api.job.JobSessionStatus;
import com.zhm.edges.plugins.api.job.JobSessionVendorStatus;
import com.zhm.edges.plugins.api.job.hook.JobSessionListener;
import com.zhm.edges.plugins.api.job.vo.CrawlerItem;
import com.zhm.edges.plugins.api.job.vo.Job;
import com.zhm.edges.plugins.api.utils.JsonUtil;
import com.zhm.edges.plugins.api.utils.Utils;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobFixtures {

  private static final Logger LOGGER = LoggerFactory.getLogger(JobFixtures.class);

  public static Job newJob() {
    return new Job().setJobId(UUID.randomUUID().toString());
  }

  public static Job newJob(String input) {
    return new Job().setJobId(UUID.randomUUID().toString()).setInput(input);
  }

  public static Builder newContext(Job job, Crawler crawler) {
    return new Builder(job, crawler);
  }

  public static class Builder {
    private final Job job;
    private final Crawler crawler;
    private final String session = UUID.randomUUID().toString();
    private final List<String> vendorNames = Collections.emptyList();
    private final JobSessionListener listener = JobSessionListener.DEAD;
    private JobSessionListener customListener;
    private boolean enableAutoListener = false;
    private CountDownLatch latch;
    private JobSessionVendorStatus vendorStatusToCountDown;

    public Builder(Job job, Crawler crawler) {
      this.job = job;
      this.crawler = crawler;
    }

    public Builder withListener(JobSessionListener listener) {
      this.customListener = listener;
      return this;
    }

    public Builder withAutoListener() {
      this.enableAutoListener = true;
      if (this.latch == null) {
        this.latch = new CountDownLatch(1);
      }
      return this;
    }

    public Builder withVendorStatusCountDown(JobSessionVendorStatus status) {
      this.vendorStatusToCountDown = status;
      return this;
    }

    public record Fixture(JobContext context, CountDownLatch latch) {}

    public JobContext build() {
      final Path jobPath =
          resolveSubDirs(WorkingDirContext.INSTANCE.jobsWithAccount(), true, job.getJobId());
      final Path jobLoggerPath = resolveSubDirs(jobPath, true, "logger");
      final Path jobResourcesPath = resolveSubDirs(jobPath, true, "resources");
      final Path jobResourcesInputPath = resolveSubDirs(jobResourcesPath, true, "input");
      final Path jobResourcesOutputPath = resolveSubDirs(jobResourcesPath, true, "output");

      verifyDirectory(
          jobLoggerPath, jobResourcesPath, jobResourcesInputPath, jobResourcesOutputPath);

      JobLogger logger = new DefaultJobLogger(job.getJobId(), session, jobLoggerPath);
      JobContext ctx =
          new DefaultJobContext(
                  job,
                  session,
                  jobPath,
                  jobLoggerPath,
                  jobResourcesPath,
                  jobResourcesInputPath,
                  jobResourcesOutputPath,
                  logger)
              .setVendorNames(vendorNames);

      // 绑定监听器
      if (customListener != null) {
        if (latch != null || vendorStatusToCountDown != null) {
          JobSessionListener decorated = getJobSessionListener();
          ctx.setListener(decorated);
        } else {
          ctx.setListener(customListener);
        }
      } else if (enableAutoListener) {
        CountDownLatch localLatch = (latch != null) ? latch : (latch = new CountDownLatch(1));
        JobSessionListener auto = getJobSessionListener(localLatch);
        ctx.setListener(auto);
      } else {
        ctx.setListener(listener);
      }
      ctx.setPendingVendors(List.of(crawler));
      ctx.setVendorNames(List.of(crawler.id()));
      final Set<String> normalizedInputs = Utils.normalizeInput(job.getInput());
      if (!normalizedInputs.isEmpty()) {
        ctx.setNormalizedInputs(normalizedInputs);
        ctx.setNormalizedInputsMap(crawler, normalizedInputs);
      }
      return ctx;
    }

    private JobSessionListener getJobSessionListener(CountDownLatch localLatch) {
      JobSessionVendorStatus vendorTrigger = vendorStatusToCountDown;
      return new JobSessionListener() {
        @Override
        public void result(JobContext context, Crawler vendor, CrawlerItem item, String input) {
          LOGGER.warn("Picked {} {}", input, JsonUtil.toJson(item));
        }

        @Override
        public void done(JobContext context) {
          LOGGER.warn("Job done>>>>>> {} - {}", context.job().getJobId(), context.sessionId());
          LOGGER.warn("summary >> {}", JsonUtil.toJson(context));
          localLatch.countDown();
        }

        @Override
        public void status(
            JobContext context,
            JobSessionStatus oldStatus,
            JobSessionStatus newStatus,
            String remark,
            Map extra) {
          LOGGER.warn(
              "job session status {} {} {}", context.job().getJobId(), oldStatus, newStatus);
        }

        @Override
        public void vendorStatus(
            JobContext context,
            Crawler vendor,
            JobSessionVendorStatus oldVendorStatus,
            JobSessionVendorStatus newVendorStatus,
            String remark,
            Map extra) {
          LOGGER.warn(
              "job session vendor status {} {} {}",
              vendor.name(),
              oldVendorStatus,
              newVendorStatus);
          if (vendorTrigger != null && newVendorStatus == vendorTrigger) {
            localLatch.countDown();
          }
        }
      };
    }

    private JobSessionListener getJobSessionListener() {
      JobSessionListener base = customListener;
      JobSessionVendorStatus vendorTrigger = vendorStatusToCountDown;
      CountDownLatch localLatch = latch;
      return new JobSessionListener() {
        @Override
        public void result(JobContext context, Crawler vendor, CrawlerItem item, String input) {
          base.result(context, vendor, item, input);
        }

        @Override
        public void done(JobContext context) {
          base.done(context);
          if (localLatch != null) {
            localLatch.countDown();
          }
        }

        @Override
        public void status(
            JobContext context,
            JobSessionStatus oldStatus,
            JobSessionStatus newStatus,
            String remark,
            Map extra) {
          base.status(context, oldStatus, newStatus, remark, extra);
        }

        @Override
        public void vendorStatus(
            JobContext context,
            Crawler vendor,
            JobSessionVendorStatus oldVendorStatus,
            JobSessionVendorStatus newVendorStatus,
            String remark,
            Map extra) {
          base.vendorStatus(context, vendor, oldVendorStatus, newVendorStatus, remark, extra);
          if (localLatch != null && vendorTrigger != null && newVendorStatus == vendorTrigger) {
            localLatch.countDown();
          }
        }
      };
    }

    public Fixture buildWithLatch() {
      JobContext ctx = build();
      CountDownLatch localLatch = (latch != null) ? latch : new CountDownLatch(1);
      return new Fixture(ctx, localLatch);
    }
  }
}
