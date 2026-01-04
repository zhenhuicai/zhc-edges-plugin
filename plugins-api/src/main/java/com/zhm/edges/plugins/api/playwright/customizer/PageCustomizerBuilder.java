package com.zhm.edges.plugins.api.playwright.customizer;

import java.util.ArrayList;
import java.util.List;

public class PageCustomizerBuilder {

  public static PageCustomizer DEFAULT =
      PageCustomizerBuilder.builder().withoutFont().withoutImage().withoutTrack().build();
  final List<PageCustomizer> customizerList = new ArrayList<>();

  private PageCustomizerBuilder() {}

  public static PageCustomizerBuilder builder() {
    return new PageCustomizerBuilder();
  }

  /**
   * ignore {@code image } resource type
   *
   * @return
   */
  public PageCustomizerBuilder withoutImage() {

    customizerList.add(
        page ->
            page.route(
                "**/*",
                route -> {
                  if (route.request().resourceType().equals("image")) {
                    route.abort();
                  } else {
                    route.resume();
                  }
                }));
    return this;
  }

  /**
   * ignore {@code font } resource type
   *
   * @return
   */
  public PageCustomizerBuilder withoutFont() {
    customizerList.add(
        page ->
            page.route(
                "**/*",
                route -> {
                  if (route.request().resourceType().equals("font")) {
                    route.abort();
                  } else {
                    route.resume();
                  }
                }));
    return this;
  }

  /**
   * disable:
   *
   * <ul>
   *   <li>google-analytics.com
   *   <li>googletagmanager.com
   *   <li>facebook.com
   * </ul>
   *
   * @return
   */
  public PageCustomizerBuilder withoutTrack() {
    customizerList.add(
        page -> {
          page.route("**/google-analytics.com/**", route -> route.abort());
          page.route("**/googletagmanager.com/**", route -> route.abort());
          page.route("**/facebook.com/tr/**", route -> route.abort());
          // clarity.ms
          page.route("**/clarity.ms/**", route -> route.abort());
        });
    return this;
  }

  public PageCustomizerBuilder add(final PageCustomizer customizer) {
    customizerList.add(customizer);
    return this;
  }

  public PageCustomizer build() {
    if (customizerList.isEmpty()) {
      return PageCustomizer.NOTHING;
    }
    return page -> {
      for (final PageCustomizer each : customizerList) {
        each.customize(page);
      }
    };
  }
}
