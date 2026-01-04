package com.zhm.edges.plugins.api.job.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_ABSENT)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CrawlerItem {

  /** This should be any id? like sku or uuid etc */
  protected String input;

  // the very first one:
  protected String id;

  // 0.01%   1/10_000
  protected Long priceInBP;
  protected LocalDateTime timestamp;
  protected int minDeliveryTimeInHour;
  protected int maxDeliveryTimeInHour;
  protected String linkage;
  protected Map<String, Object> extra;

  // This is raw items
  protected List<Product> items;

  public String getId() {
    return id;
  }

  public CrawlerItem setId(String id) {
    this.id = id;
    return this;
  }

  public String getInput() {
    return input;
  }

  public CrawlerItem setInput(String input) {
    this.input = input;
    return this;
  }

  public Long getPriceInBP() {
    return priceInBP;
  }

  public CrawlerItem setPriceInBP(Long priceInBP) {
    this.priceInBP = priceInBP;
    return this;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public CrawlerItem setTimestamp(LocalDateTime timestamp) {
    this.timestamp = timestamp;
    return this;
  }

  public int getMinDeliveryTimeInHour() {
    return minDeliveryTimeInHour;
  }

  public CrawlerItem setMinDeliveryTimeInHour(int minDeliveryTimeInHour) {
    this.minDeliveryTimeInHour = minDeliveryTimeInHour;
    return this;
  }

  public int getMaxDeliveryTimeInHour() {
    return maxDeliveryTimeInHour;
  }

  public CrawlerItem setMaxDeliveryTimeInHour(int maxDeliveryTimeInHour) {
    this.maxDeliveryTimeInHour = maxDeliveryTimeInHour;
    return this;
  }

  public String getLinkage() {
    return linkage;
  }

  public CrawlerItem setLinkage(String linkage) {
    this.linkage = linkage;
    return this;
  }

  public Map<String, Object> getExtra() {
    return extra;
  }

  public CrawlerItem setExtra(Map<String, Object> extra) {
    this.extra = extra;
    return this;
  }

  public CrawlerItem add(final String key, final Object data) {
    if (this.extra == null) {
      this.extra = new LinkedHashMap<>();
    }
    extra.put(key, data);
    return this;
  }

  public List<Product> getItems() {
    return items;
  }

  public CrawlerItem setItems(List<Product> items) {
    this.items = items;
    return this;
  }
}
