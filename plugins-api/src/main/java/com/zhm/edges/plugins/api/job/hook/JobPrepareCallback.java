package com.zhm.edges.plugins.api.job.hook;

import com.zhm.edges.plugins.api.job.JobContext;
import com.zhm.edges.plugins.api.job.vo.Job;

public interface JobPrepareCallback {

  JobPrepareCallback EMPTY = (job, jobContext) -> {};

  void prepare(Job job, JobContext jobContext);
}
