package com.zhm.edges.plugins.api.playwright;

import oshi.SystemInfo;

public class Benchmark {

  static final int MB = 1024 * 1024;
  static Runtime runtime = Runtime.getRuntime();
  static SystemInfo systemInfo = new SystemInfo();
  final String name;
  final long freeMemory;
  final long systemMemory;

  public Benchmark(String name) {
    this.name = name;
    freeMemory = runtime.freeMemory();
    systemMemory = systemInfo.getHardware().getMemory().getAvailable();
  }

  public static Benchmark of(final String name) {
    return new Benchmark(name);
  }

  public Statistic end() {

    long _freeMemory = runtime.freeMemory();
    long _systemMemory = systemInfo.getHardware().getMemory().getAvailable();

    // Begin - End  = Cost
    return new Statistic(
        name, (freeMemory - _freeMemory) / MB, (systemMemory - _systemMemory) / MB);
  }

  public static class Statistic {
    private String name;
    private Long jvmMemoryCost;
    private Long systemMemoryCost;

    public Statistic() {}

    public Statistic(String name, Long jvmMemoryCost, Long systemMemoryCost) {
      this.name = name;
      this.jvmMemoryCost = jvmMemoryCost;
      this.systemMemoryCost = systemMemoryCost;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public Long getJvmMemoryCost() {
      return jvmMemoryCost;
    }

    public void setJvmMemoryCost(Long jvmMemoryCost) {
      this.jvmMemoryCost = jvmMemoryCost;
    }

    public Long getSystemMemoryCost() {
      return systemMemoryCost;
    }

    public void setSystemMemoryCost(Long systemMemoryCost) {
      this.systemMemoryCost = systemMemoryCost;
    }
  }
}
