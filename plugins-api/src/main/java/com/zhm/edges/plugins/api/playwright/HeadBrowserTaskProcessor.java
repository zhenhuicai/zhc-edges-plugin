package com.zhm.edges.plugins.api.playwright;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HeadBrowserTaskProcessor extends BrowserTaskProcessor<HeadBrowserTaskProcessor.HeadBrowserTaskWorker> {


  public HeadBrowserTaskProcessor(int queueSize, int parallel) {
    super(queueSize, parallel);
  }


  public HeadBrowserTaskProcessor() {
    this(64, 1);
  }

  @Override
  protected HeadBrowserTaskWorker work(int idx) {
    return new HeadBrowserTaskWorker("HeadWorker-"+idx);
  }

  protected BrowserType.LaunchOptions launchOptions() {
    BrowserType.LaunchOptions res = new BrowserType.LaunchOptions();
    List<String> allArgs = new ArrayList<>();
    List<Constant.OptimizesArgs> DEFAULT =
        Arrays.asList(
            /*test need*/
            // Constant.OptimizesArgs.CORE_SECURITY_SANDBOX,
            /*test need*/
            // Constant.OptimizesArgs.BACKGROUND_NETWORK,
            /*test need*/
            // Constant.OptimizesArgs.EXTENSIONS,
            Constant.OptimizesArgs.FEATURE_DISABLE,
            Constant.OptimizesArgs.NETWORK_SECURITY,
            Constant.OptimizesArgs.UX_OPTIMIZATION,
            Constant.OptimizesArgs.PERFORMANCE_RESOURCE,
            Constant.OptimizesArgs.POPUP_CONTROL
            // Constant.OptimizesArgs.HEAD
            );

    for (final Constant.OptimizesArgs args : DEFAULT) {
      allArgs.addAll(args.args());
    }
    res.setHeadless(false);
    res.setArgs(allArgs);
    res.setTimeout(Constant.LAUNCH_TIME_OUT);
    res.setSlowMo(0); // 无延迟，最大化性能
    return res;
  }

  protected class HeadBrowserTaskWorker extends BrowserTaskProcessor<HeadBrowserTaskWorker>.BrowserTaskWorker{

    protected HeadBrowserTaskWorker(String name) {
      super(name);
    }

    @Override
    protected Playwright playwright() {
      return Playwright.create();
    }

    @Override
    public boolean headless() {
      return false;
    }

    @Override
    protected Browser browser(Playwright playwright) {
      return playwright.chromium().launch(launchOptions());
    }
  }
}
