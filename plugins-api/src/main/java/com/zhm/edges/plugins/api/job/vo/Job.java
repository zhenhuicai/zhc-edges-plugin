package com.zhm.edges.plugins.api.job.vo;

import com.zhm.edges.plugins.api.job.enums.InputTypeEnum;
import java.util.List;
import java.util.Objects;

public class Job {

  // job input
  // job resources
  // Vendor who do you want?
  protected String jobId;
  protected String input;
  protected List<String> vendors;
  protected InputTypeEnum inputType;
  protected String jobName;

  public List<String> getVendors() {
    return vendors;
  }

  public Job setVendors(List<String> vendors) {
    this.vendors = vendors;
    return this;
  }

  public String getJobId() {
    return jobId;
  }

  public Job setJobId(String jobId) {
    this.jobId = jobId;
    return this;
  }

  public String getInput() {
    return input;
  }

  public Job setInput(String input) {
    this.input = input;
    return this;
  }

  public String getJobName() {
    return jobName;
  }

  public Job setJobName(String jobName) {
    this.jobName = jobName;
    return this;
  }

  public InputTypeEnum getInputType() {
    return inputType;
  }

  public Job setInputType(InputTypeEnum inputType) {
    this.inputType = inputType;
    return this;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) return true;
    if (object == null || getClass() != object.getClass()) return false;
    Job job = (Job) object;
    return Objects.equals(jobId, job.jobId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(jobId);
  }
}
