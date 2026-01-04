package com.zhm.edges.plugins.api.playwright;

import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Locator;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SafePlaywright {
  static final Logger logger = LoggerFactory.getLogger(SafePlaywright.class);

  public static String failQuickTextContent(Locator locator) {
    return locator.textContent(new Locator.TextContentOptions().setTimeout(100));
  }

  public static void innerHtml(Locator locator, final Consumer<String> htmlConsumer) {

    try {
      final String innerHtml = locator.innerHTML(new Locator.InnerHTMLOptions().setTimeout(1_000));
      htmlConsumer.accept(innerHtml);
    } catch (Throwable throwable) {
      logger.warn("fail pick inner html {}", throwable.getMessage());
    }
  }

  public static void querySelector(final Supplier<ElementHandle> elementHandleSupplier, final Consumer<ElementHandle> elementHandleConsumer){

    try {
      ElementHandle elementHandle = elementHandleSupplier.get();
      if (elementHandle != null) {
        elementHandleConsumer.accept(elementHandle);
      }
    } catch (Throwable throwable) {
      logger.warn("fail pick locator ", throwable);
    }
  }

  /**
   * This is safe
   *
   * @param locatorSupplier
   * @param locatorConsumer
   */
  public static void locator(
      final Supplier<Locator> locatorSupplier, final Consumer<Locator> locatorConsumer) {

    try {
      Locator locator = locatorSupplier.get();
      if (locator != null) {
        locatorConsumer.accept(locator);
      }
    } catch (Throwable throwable) {
      logger.warn("fail pick locator ", throwable);
    }
  }
}
