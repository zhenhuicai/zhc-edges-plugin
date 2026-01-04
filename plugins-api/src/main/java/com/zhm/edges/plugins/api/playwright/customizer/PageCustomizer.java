package com.zhm.edges.plugins.api.playwright.customizer;

import com.microsoft.playwright.Page;

public interface PageCustomizer {

  PageCustomizer NOTHING = page -> {};

  void customize(final Page page);
}
