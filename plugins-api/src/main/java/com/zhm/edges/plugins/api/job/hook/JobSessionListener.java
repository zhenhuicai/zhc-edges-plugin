package com.zhm.edges.plugins.api.job.hook;

import com.zhm.edges.plugins.api.Crawler;
import com.zhm.edges.plugins.api.job.JobContext;
import com.zhm.edges.plugins.api.job.JobSessionStatus;
import com.zhm.edges.plugins.api.job.JobSessionVendorStatus;
import com.zhm.edges.plugins.api.job.vo.CrawlerItem;

import java.util.Map;

/** This is the listener during the */
public interface JobSessionListener {

  JobSessionListener DEAD =
      new JobSessionListener() {

        @Override
        public void result(JobContext context, Crawler vendor, CrawlerItem item, String input) {}

        @Override
        public void done(JobContext context) {}

        @Override
        public void status(
            JobContext context,
            JobSessionStatus oldStatus,
            JobSessionStatus newStatus,
            String remark,
            final Map extra) {}

        @Override
        public void vendorStatus(
            JobContext jobContext,
            Crawler vendor,
            JobSessionVendorStatus oldVendorStatus,
            JobSessionVendorStatus newVendorStatus,
            String remark,
            final Map extra) {}
      };

  /**
   * An result crawled from vendor
   *
   * @param context the job context
   * @param vendor the vendor
   * @param item the crawled item
   * @param input the input
   */
  void result(
          final JobContext context, final Crawler vendor, final CrawlerItem item, final String input);

  /**
   * Job session finished, no matter normal done or canceled
   *
   * @param context the job context
   */
  void done(JobContext context);

  /**
   * Switch of the session's status
   *
   * @param context the job context
   * @param oldStatus the old status
   * @param newStatus the new status
   * @param remark any comment
   * @param extra any extra args
   */
  void status(
      JobContext context,
      JobSessionStatus oldStatus,
      JobSessionStatus newStatus,
      String remark,
      final Map extra);

  /**
   * Switch of the vendor's status
   *
   * @param jobContext the job context
   * @param vendor the vendor
   * @param oldVendorStatus the old status
   * @param newVendorStatus the new status
   * @param remark any comment
   * @param extra any extra args
   */
  void vendorStatus(
      JobContext jobContext,
      Crawler vendor,
      JobSessionVendorStatus oldVendorStatus,
      JobSessionVendorStatus newVendorStatus,
      String remark,
      Map extra);

  /**
   * Try to publish message to external
   *
   * @param message the message body
   * @param <T> generic type
   */
  default <T> void publish(T message) {}
}
