package com.zhm.edges.plugins.api.playwright;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.microsoft.playwright.options.WaitUntilState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Enhanced SafePlaywright utility that handles execution context destroyed errors
 * and provides robust element interaction methods for web crawling scenarios.
 */
public class EnhancedSafePlaywright {
    
    static final Logger logger = LoggerFactory.getLogger(EnhancedSafePlaywright.class);
    
    // Retry configuration
    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final long RETRY_DELAY_MS = 1000;
    private static final long DEFAULT_TIMEOUT_MS = 10000;
    
    /**
     * Safe element querying with execution context protection and retry logic
     */
    public static List<ElementHandle> querySelectorAllSafely(Page page, String selector) {
        return querySelectorAllSafely(page, selector, DEFAULT_TIMEOUT_MS);
    }
    
    public static List<ElementHandle> querySelectorAllSafely(Page page, String selector, long timeoutMs) {
        for (int attempt = 1; attempt <= MAX_RETRY_ATTEMPTS; attempt++) {
            try {
                // Ensure page is in a stable state before querying
                waitForPageStability(page, timeoutMs);
                
                // Use locator for safer element selection
                Locator locator = page.locator(selector);
                
                // Wait for at least one element to be attached
                try {
                    locator.first().waitFor(new Locator.WaitForOptions()
                        .setState(WaitForSelectorState.ATTACHED)
                        .setTimeout(timeoutMs));
                } catch (Exception e) {
                    // No elements found, return empty list
                    logger.debug("No elements found for selector: {}", selector);
                    return Collections.emptyList();
                }
                
                // Get all elements
                int count = locator.count();
                List<ElementHandle> elements = new ArrayList<>();
                
                for (int i = 0; i < count; i++) {
                    try {
                        ElementHandle element = locator.nth(i).elementHandle();
                        if (element != null) {
                            elements.add(element);
                        }
                    } catch (Exception e) {
                        logger.debug("Failed to get element at index {}: {}", i, e.getMessage());
                    }
                }
                
                logger.debug("Found {} elements for selector: {}", elements.size(), selector);
                return elements;
                
            } catch (Exception e) {
                if (isExecutionContextDestroyed(e)) {
                    logger.warn("Execution context destroyed on attempt {} for selector: {}", attempt, selector);
                    if (attempt < MAX_RETRY_ATTEMPTS) {
                        sleep(RETRY_DELAY_MS * attempt);
                        continue;
                    }
                }
                logger.warn("Element query failed for selector {} on attempt {}: {}", selector, attempt, e.getMessage());
                return Collections.emptyList();
            }
        }
        
        return Collections.emptyList();
    }
    
    /**
     * Safe single element querying with execution context protection
     */
    public static ElementHandle querySelectorSafely(Page page, String selector) {
        return querySelectorSafely(page, selector, DEFAULT_TIMEOUT_MS);
    }
    
    public static ElementHandle querySelectorSafely(Page page, String selector, long timeoutMs) {
        for (int attempt = 1; attempt <= MAX_RETRY_ATTEMPTS; attempt++) {
            try {
                // Ensure page is in a stable state
                waitForPageStability(page, timeoutMs);
                
                // Use locator for safer element selection
                Locator locator = page.locator(selector);
                
                // Wait for element to be attached
                locator.first().waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.ATTACHED)
                    .setTimeout(timeoutMs));
                
                ElementHandle element = locator.first().elementHandle();
                if (element != null) {
                    logger.debug("Found element for selector: {}", selector);
                    return element;
                }
                
            } catch (Exception e) {
                if (isExecutionContextDestroyed(e)) {
                    logger.warn("Execution context destroyed on attempt {} for selector: {}", attempt, selector);
                    if (attempt < MAX_RETRY_ATTEMPTS) {
                        sleep(RETRY_DELAY_MS * attempt);
                        continue;
                    }
                }
                logger.warn("Element query failed for selector {} on attempt {}: {}", selector, attempt, e.getMessage());
            }
        }
        
        return null;
    }
    
    /**
     * Safe element interaction with execution context protection
     */
    public static void querySelector(Supplier<ElementHandle> elementHandleSupplier, Consumer<ElementHandle> elementHandleConsumer) {
        querySelector(elementHandleSupplier, elementHandleConsumer, DEFAULT_TIMEOUT_MS);
    }
    
    public static void querySelector(Supplier<ElementHandle> elementHandleSupplier, Consumer<ElementHandle> elementHandleConsumer, long timeoutMs) {
        for (int attempt = 1; attempt <= MAX_RETRY_ATTEMPTS; attempt++) {
            try {
                ElementHandle elementHandle = elementHandleSupplier.get();
                if (elementHandle != null) {
                    elementHandleConsumer.accept(elementHandle);
                    return;
                }
            } catch (Exception e) {
                if (isExecutionContextDestroyed(e)) {
                    logger.warn("Execution context destroyed on attempt {}: {}", attempt, e.getMessage());
                    if (attempt < MAX_RETRY_ATTEMPTS) {
                        sleep(RETRY_DELAY_MS * attempt);
                        continue;
                    }
                }
                logger.warn("Element interaction failed on attempt {}: {}", attempt, e.getMessage());
            }
        }
    }
    
    /**
     * Safe text content extraction with execution context protection
     */
    public static String getTextContentSafely(ElementHandle element) {
        return getTextContentSafely(element, DEFAULT_TIMEOUT_MS);
    }
    
    public static String getTextContentSafely(ElementHandle element, long timeoutMs) {
        for (int attempt = 1; attempt <= MAX_RETRY_ATTEMPTS; attempt++) {
            try {
                String text = element.textContent();
                if (text != null) {
                    return text.trim();
                }
                return "";
                
            } catch (Exception e) {
                if (isExecutionContextDestroyed(e)) {
                    logger.warn("Execution context destroyed on attempt {} for text content: {}", attempt, e.getMessage());
                    if (attempt < MAX_RETRY_ATTEMPTS) {
                        sleep(RETRY_DELAY_MS * attempt);
                        continue;
                    }
                }
                logger.warn("Text content extraction failed on attempt {}: {}", attempt, e.getMessage());
            }
        }
        
        return "";
    }
    
    /**
     * Safe attribute extraction with execution context protection
     */
    public static String getAttributeSafely(ElementHandle element, String attribute) {
        return getAttributeSafely(element, attribute, DEFAULT_TIMEOUT_MS);
    }
    
    public static String getAttributeSafely(ElementHandle element, String attribute, long timeoutMs) {
        for (int attempt = 1; attempt <= MAX_RETRY_ATTEMPTS; attempt++) {
            try {
                String value = element.getAttribute(attribute);
                return value != null ? value.trim() : "";
                
            } catch (Exception e) {
                if (isExecutionContextDestroyed(e)) {
                    logger.warn("Execution context destroyed on attempt {} for attribute {}: {}", attempt, attribute, e.getMessage());
                    if (attempt < MAX_RETRY_ATTEMPTS) {
                        sleep(RETRY_DELAY_MS * attempt);
                        continue;
                    }
                }
                logger.warn("Attribute extraction failed for {} on attempt {}: {}", attribute, attempt, e.getMessage());
            }
        }
        
        return "";
    }
    
    /**
     * Safe inner HTML extraction with execution context protection
     */
    public static void innerHtml(ElementHandle element, Consumer<String> htmlConsumer) {
        innerHtml(element, htmlConsumer, DEFAULT_TIMEOUT_MS);
    }
    
    public static void innerHtml(ElementHandle element, Consumer<String> htmlConsumer, long timeoutMs) {
        for (int attempt = 1; attempt <= MAX_RETRY_ATTEMPTS; attempt++) {
            try {
                String innerHtml = element.innerHTML();
                if (innerHtml != null) {
                    htmlConsumer.accept(innerHtml);
                    return;
                }
                
            } catch (Exception e) {
                if (isExecutionContextDestroyed(e)) {
                    logger.warn("Execution context destroyed on attempt {} for inner HTML: {}", attempt, e.getMessage());
                    if (attempt < MAX_RETRY_ATTEMPTS) {
                        sleep(RETRY_DELAY_MS * attempt);
                        continue;
                    }
                }
                logger.warn("Inner HTML extraction failed on attempt {}: {}", attempt, e.getMessage());
            }
        }
    }
    
    /**
     * Safe navigation with execution context protection
     */
    public static Response navigateSafely(Page page, String url) {
        return navigateSafely(page, url, DEFAULT_TIMEOUT_MS);
    }
    
    public static Response navigateSafely(Page page, String url, long timeoutMs) {
        for (int attempt = 1; attempt <= MAX_RETRY_ATTEMPTS; attempt++) {
            try {
                // Wait for any ongoing operations to complete
                page.waitForLoadState(LoadState.NETWORKIDLE, 
                    new Page.WaitForLoadStateOptions().setTimeout(5000));
                
                // Perform navigation
                Response response = page.navigate(url, 
                    new Page.NavigateOptions()
                        .setTimeout(timeoutMs)
                        .setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
                
                // Wait for network idle to ensure page is fully loaded
                page.waitForLoadState(LoadState.NETWORKIDLE, 
                    new Page.WaitForLoadStateOptions().setTimeout(timeoutMs));
                
                logger.debug("Successfully navigated to: {}", url);
                return response;
                
            } catch (Exception e) {
                if (isExecutionContextDestroyed(e)) {
                    logger.warn("Execution context destroyed on attempt {} for navigation to {}: {}", attempt, url, e.getMessage());
                    if (attempt < MAX_RETRY_ATTEMPTS) {
                        sleep(RETRY_DELAY_MS * attempt);
                        continue;
                    }
                }
                logger.warn("Navigation failed for URL {} on attempt {}: {}", url, attempt, e.getMessage());
                throw new RuntimeException("Navigation failed after " + MAX_RETRY_ATTEMPTS + " attempts", e);
            }
        }
        
        throw new RuntimeException("Navigation failed for URL: " + url);
    }
    
    /**
     * Safe click operation with execution context protection
     */
    public static void clickSafely(ElementHandle element) {
        clickSafely(element, DEFAULT_TIMEOUT_MS);
    }
    
    public static void clickSafely(ElementHandle element, long timeoutMs) {
        for (int attempt = 1; attempt <= MAX_RETRY_ATTEMPTS; attempt++) {
            try {
                element.click(new ElementHandle.ClickOptions().setTimeout(timeoutMs));
                logger.debug("Successfully clicked element");
                return;
                
            } catch (Exception e) {
                if (isExecutionContextDestroyed(e)) {
                    logger.warn("Execution context destroyed on attempt {} for click: {}", attempt, e.getMessage());
                    if (attempt < MAX_RETRY_ATTEMPTS) {
                        sleep(RETRY_DELAY_MS * attempt);
                        continue;
                    }
                }
                logger.warn("Click operation failed on attempt {}: {}", attempt, e.getMessage());
            }
        }
    }
    
    /**
     * Safe fill operation with execution context protection
     */
    public static void fillSafely(ElementHandle element, String value) {
        fillSafely(element, value, DEFAULT_TIMEOUT_MS);
    }
    
    public static void fillSafely(ElementHandle element, String value, long timeoutMs) {
        for (int attempt = 1; attempt <= MAX_RETRY_ATTEMPTS; attempt++) {
            try {
                element.fill(value, new ElementHandle.FillOptions().setTimeout(timeoutMs));
                logger.debug("Successfully filled element with value: {}", value);
                return;
                
            } catch (Exception e) {
                if (isExecutionContextDestroyed(e)) {
                    logger.warn("Execution context destroyed on attempt {} for fill: {}", attempt, e.getMessage());
                    if (attempt < MAX_RETRY_ATTEMPTS) {
                        sleep(RETRY_DELAY_MS * attempt);
                        continue;
                    }
                }
                logger.warn("Fill operation failed on attempt {}: {}", attempt, e.getMessage());
            }
        }
    }
    
    /**
     * Wait for page stability before performing operations
     */
    private static void waitForPageStability(Page page, long timeoutMs) {
        try {
            // Wait for network idle
            page.waitForLoadState(LoadState.NETWORKIDLE, 
                new Page.WaitForLoadStateOptions().setTimeout(timeoutMs));
            
            // Additional stability check - wait for no ongoing requests
            page.waitForFunction("() => window.performance.getEntriesByType('resource').filter(r => !r.responseEnd).length === 0", 
                new Page.WaitForFunctionOptions().setTimeout(5000));
                
        } catch (Exception e) {
            logger.debug("Page stability wait failed: {}", e.getMessage());
            // Continue anyway, as this is not critical
        }
    }
    
    /**
     * Check if the exception is related to execution context being destroyed
     */
    private static boolean isExecutionContextDestroyed(Exception e) {
        String message = e.getMessage();
        if (message == null) {
            return false;
        }
        
        return message.contains("Execution context was destroyed") ||
               message.contains("Target closed") ||
               message.contains("Session closed") ||
               message.contains("Connection closed");
    }
    
    /**
     * Safe sleep with interruption handling
     */
    private static void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted during retry delay", e);
        }
    }
    
    /**
     * Legacy compatibility methods
     */
    public static String failQuickTextContent(Locator locator) {
        try {
            return locator.textContent(new Locator.TextContentOptions().setTimeout(100));
        } catch (Exception e) {
            logger.warn("Quick text content failed: {}", e.getMessage());
            return "";
        }
    }
    
    public static void locator(Supplier<Locator> locatorSupplier, Consumer<Locator> locatorConsumer) {
        try {
            Locator locator = locatorSupplier.get();
            if (locator != null) {
                locatorConsumer.accept(locator);
            }
        } catch (Exception e) {
            logger.warn("Locator operation failed: {}", e.getMessage());
        }
    }
} 