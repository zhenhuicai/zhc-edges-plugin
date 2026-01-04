package com.zhm.edges.plugins.api.bootstrap;

public class BootStrapReadyEvent {

  public static BootStrapReadyEvent isReady() {
    return new BootStrapReadyEvent();
  }
}
