package com.zhm.edges.plugins.api.job;

public enum LoggerType {
  // 登录相关
  LOGIN_TASK_SCHEDULE("Schedule login task", "登录任务启动，请在弹出的浏览器窗口中登录"),
  SESSION_VALID("Session valid", "当前账号已登录"),
  SESSION_EXPIRED("Session expired", "当前账号未登录，或登录状态已过期，请重新登录"),
  LOGIN_SUCCESS("Login success", "登录成功"),
  LOGIN_FAILED("Login failed", "登录失败"),
  LOGIN_PAGE_CLOSED("Login page closed", "登录窗口已关闭"),
  AUTO_LOGIN("Try auto login", "尝试自动登录"),
  MUST_LOGIN(
      "No login no access, switch to pending login status of this vendor",
      "未登录状态下无法获取商品信息，请点击下方登录按钮继续任务"),
  CREDENTIAL_SAVE_FAILED("Failed to save credential", "保存登录凭证失败"),

  // 爬取相关
  CRAWL_TASK_SCHEDULED("Schedule crawler task", "获取商品信息任务启动"),
  NAVIGATING_SEARCH_PAGE("Navigating to search page {}", "正在为您搜索商品：{}"),
  PRODUCTS_FOUND("Found {} products", "搜索到{}行商品"),
  PRODUCTS_NOT_FOUND("Not found products, {}", "未搜索到商品：{}"),
  PRODUCT_EXTRACTED(" Extract product {}", " 整理商品信息：{} "),
  ELEMENT_CONTENT_FAILED("Failed to get content for:{}", "元素解析失败：{}"),
  CRAWLER_FAILED("Crawler failed", "获取商品信息任务失败"),

  VENDOR_PREPARE_FAILED("vendor job prepare fail: {}", "插件处理异常: {}"),

  // 任务控制,
  BROWSER_RESOURCE_INSUFFICIENT("Browser resource insufficient", "暂无可用的浏览器资源"),
  TASK_START("Prepare kick of this job", "启动任务"),
  TASK_CANCELING("Task canceling", "取消任务中"),
  TASK_REMOVED("Crawler task removed {}", "任务已取消 {}"),
  TASK_SCHEDULED("Crawler task already scheduled/done {}", "任务已启动，等待关闭任务 {}"),
  TASK_ERROR("fail wait schedule job done {}", "任务异常 {}"),
  TASK_COMPLETED("Task completed", "任务全部完成");

  final String message;
  final String cnMessage;

  LoggerType(String message, String cnMessage) {
    this.message = message;
    this.cnMessage = cnMessage;
  }
}
