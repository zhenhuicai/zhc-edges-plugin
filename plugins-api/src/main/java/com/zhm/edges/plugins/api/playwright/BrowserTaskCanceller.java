package com.zhm.edges.plugins.api.playwright;

import java.util.Optional;

public interface BrowserTaskCanceller {
  Optional<BrowserTask> remove();
}
