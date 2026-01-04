package com.zhm.edges.plugins.api.job.vo;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Product {

  /** 品牌 */
  private String brand;

  /** 系列 */
  private String series;

  /** 产品名称 */
  private String name;

  /** 型号 */
  private String model;

  /** 订货号 */
  private String materialNo;

  /** 自定义编码 */
  private String customCode;

  /** 市场价 */
  private String marketPrice;

  /** 销售价 */
  private String price;

  /** 未税价 */
  private String untaxedPrice;

  /** 库存 */
  private String stock;

  /** 货期 */
  private String delivery;

  /** 货期（转为可比较的数字） */
  private Integer leadTime;

  /** 包装规格 */
  private String packingSpecifications;

  /** 产品图片 */
  private String imageUrl;

  /** 产品链接 */
  private String productUrl;

  /** 产品规格 */
  private Map<String, String> attributes = new LinkedHashMap<>();

  private BigDecimal matchRate;

  public String getBrand() {
    return brand;
  }

  public Product setBrand(String brand) {
    this.brand = brand;
    return this;
  }

  public String getSeries() {
    return series;
  }

  public Product setSeries(String series) {
    this.series = series;
    return this;
  }

  public String getName() {
    return name;
  }

  public Product setName(String name) {
    this.name = name;
    return this;
  }

  public String getModel() {
    return model;
  }

  public Product setModel(String model) {
    this.model = model;
    return this;
  }

  public String getMaterialNo() {
    return materialNo;
  }

  public Product setMaterialNo(String materialNo) {
    this.materialNo = materialNo;
    return this;
  }

  public String getCustomCode() {
    return customCode;
  }

  public Product setCustomCode(String customCode) {
    this.customCode = customCode;
    return this;
  }

  public String getMarketPrice() {
    return marketPrice;
  }

  public Product setMarketPrice(String marketPrice) {
    this.marketPrice = marketPrice;
    return this;
  }

  public String getPrice() {
    return price;
  }

  public Product setPrice(String price) {
    this.price = price;
    return this;
  }

  public String getUntaxedPrice() {
    return untaxedPrice;
  }

  public Product setUntaxedPrice(String untaxedPrice) {
    this.untaxedPrice = untaxedPrice;
    return this;
  }

  public String getStock() {
    return stock;
  }

  public Product setStock(String stock) {
    this.stock = stock;
    return this;
  }

  public String getDelivery() {
    return delivery;
  }

  public Product setDelivery(String delivery) {
    this.delivery = delivery;
    return this;
  }

  public Integer getLeadTime() {
    return leadTime;
  }

  public Product setLeadTime(Integer leadTime) {
    this.leadTime = leadTime;
    return this;
  }

  public String getPackingSpecifications() {
    return packingSpecifications;
  }

  public Product setPackingSpecifications(String packingSpecifications) {
    this.packingSpecifications = packingSpecifications;
    return this;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public Product setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
    return this;
  }

  public String getProductUrl() {
    return productUrl;
  }

  public Product setProductUrl(String productUrl) {
    this.productUrl = productUrl;
    return this;
  }

  public Map<String, String> getAttributes() {
    return attributes;
  }

  public Product setAttributes(Map<String, String> attributes) {
    this.attributes = attributes;
    return this;
  }

  public Product addAttribute(final String key, String value) {
    attributes.put(key, value);
    return this;
  }

  public BigDecimal getMatchRate() {
    return matchRate;
  }

  public Product setMatchRate(BigDecimal matchRate) {
    this.matchRate = matchRate;
    return this;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
        .append("brand", brand)
        .append("series", series)
        .append("name", name)
        .append("model", model)
        .append("materialNo", materialNo)
        .append("customCode", customCode)
        .append("marketPrice", marketPrice)
        .append("price", price)
        .append("untaxedPrice", untaxedPrice)
        .append("stock", stock)
        .append("delivery", delivery)
        .append("leadTime", leadTime)
        .append("packingSpecifications", packingSpecifications)
        .append("imageUrl", imageUrl)
        .append("productUrl", productUrl)
        .append("attributes", attributes)
        .append("matchRate", matchRate)
        .toString();
  }
}
