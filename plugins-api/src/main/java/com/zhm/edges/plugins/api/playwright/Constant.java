package com.zhm.edges.plugins.api.playwright;

import java.util.Arrays;
import java.util.List;

public interface Constant {

  int LAUNCH_TIME_OUT = 30000; // 30 seconds

  int DEF_VIEW_WIDTH = 1280;
  int DEF_VIEW_HEIGHT = 720;

  String DEF_USER_AGENT =
      "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";

  enum OptimizesArgs {
    CORE_SECURITY_SANDBOX("--no-sandbox", "--disable-dev-shm-usage", "--disable-setuid-sandbox"),
    BACKGROUND_NETWORK(
        "--disable-background-networking",
        "--disable-background-timer-throttling",
        "--disable-backgrounding-occluded-windows",
        "--disable-renderer-backgrounding",
        "--disable-component-extensions-with-background-pages"),
    EXTENSIONS(
        "--disable-extensions",
        "--disable-default-apps",
        "--disable-plugins",
        "--disable-component-update"),
    FEATURE_DISABLE(
        "--disable-client-side-phishing-detection",
        "--disable-sync",
        "--disable-translate",
        "--disable-breakpad",
        "--disable-hang-monitor"),
    NETWORK_SECURITY(
        "--disable-ipc-flooding-protection",
        "--disable-domain-reliability",
        "--disable-web-security"),
    UX_OPTIMIZATION(
        "--no-first-run",
        "--no-default-browser-check",
        "--disable-prompt-on-repost",
        "--password-store=basic",
        "--use-mock-keychain"),
    PERFORMANCE_RESOURCE(
        "--force-color-profile=srgb",
        "--metrics-recording-only",
        "--mute-audio",
        "--memory-pressure-off",
        "--max_old_space_size=4096",
        "--disable-logging",
        "--silent"),
    POPUP_CONTROL("--disable-popup-blocking=false"),

    HEADLESS(
        "--disable-gpu",
        "--disable-software-rasterizer",
        "--hide-scrollbars",
        "--disable-gpu-sandbox",
        "--disable-accelerated-2d-canvas",
        "--disable-accelerated-jpeg-decoding",
        "--disable-accelerated-mjpeg-decode",
        "--disable-accelerated-video-decode",
        "--disable-features=TranslateUI,BlinkGenPropertyTrees,VizDisplayCompositor"),
    // Try to hide the address bar
    HEAD("--enable-gpu-rasterization", "--enable-zero-copy", "--kiosk");

    public static List<OptimizesArgs> DEFAULT =
        Arrays.asList(
            CORE_SECURITY_SANDBOX,
            BACKGROUND_NETWORK,
            EXTENSIONS,
            FEATURE_DISABLE,
            NETWORK_SECURITY,
            UX_OPTIMIZATION,
            PERFORMANCE_RESOURCE,
            POPUP_CONTROL);
    private final List<String> args;

    OptimizesArgs(String... args) {
      this.args = Arrays.asList(args);
    }

    public List<String> args() {
      return args;
    }
  }
}
