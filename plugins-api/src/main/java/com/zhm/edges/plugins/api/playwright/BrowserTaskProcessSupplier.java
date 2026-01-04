package com.zhm.edges.plugins.api.playwright;

public interface BrowserTaskProcessSupplier {
  BrowserTaskProcessor headProcessor();

  BrowserTaskProcessor headlessProcessor();
}
