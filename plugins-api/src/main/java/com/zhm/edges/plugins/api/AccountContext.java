package com.zhm.edges.plugins.api;

import com.zhm.edges.plugins.api.job.vo.VendorProfile;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 账户上下文管理类，用于缓存当前绑定的账户信息 支持多账号数据隔离 */
public class AccountContext {

  private static final Logger logger = LoggerFactory.getLogger(AccountContext.class);

  private static final AtomicReference<String> currentAccount = new AtomicReference<>();
  private static Map<String, VendorProfile> defaultProfileMap = new ConcurrentHashMap<>();

  /**
   * 绑定账户
   *
   * @param accountId 账户ID
   */
  public static void bindAccount(String accountId) {
    if (accountId == null || accountId.trim().isEmpty()) {
      throw new IllegalArgumentException("Account ID cannot be null or empty");
    }

    String previousAccount = currentAccount.getAndSet(accountId.trim());
    logger.info("Account bound: {} (previous: {})", accountId, previousAccount);
  }

  public static void setDefaultProfileMap(List<VendorProfile> defaultProfiles) {
    defaultProfileMap =
        defaultProfiles.stream()
            .collect(Collectors.toMap(VendorProfile::getVendorId, Function.identity()));
  }

  /**
   * 获取当前绑定的账户ID
   *
   * @return 当前账户ID，如果未绑定则返回默认值 "default"
   */
  public static String getCurrentAccount() {
    String account = currentAccount.get();
    return account != null ? account : "default";
  }

  public static VendorProfile getDefaultProfile(String vendorId) {
    if (defaultProfileMap.isEmpty()) {
      return null;
    }
    return defaultProfileMap.get(vendorId);
  }

  /**
   * 检查是否已绑定账户
   *
   * @return true 如果已绑定账户
   */
  public static boolean isAccountBound() {
    return currentAccount.get() != null;
  }

  /** 解绑当前账户 */
  public static void unbindAccount() {
    String previousAccount = currentAccount.getAndSet(null);
    logger.info("Account unbound: {}", previousAccount);
  }

  /**
   * 基于当前账户获取路径
   *
   * @param basePath 基础路径
   * @param subPaths 子路径
   * @return 包含账户子目录的完整路径
   */
  public static Path getAccountPath(Path basePath, String... subPaths) {
    String accountId = getCurrentAccount();
    String[] allPaths = new String[subPaths.length + 1];
    allPaths[0] = accountId;
    System.arraycopy(subPaths, 0, allPaths, 1, subPaths.length);

    Path result = basePath;
    for (String path : allPaths) {
      result = result.resolve(path);
    }
    return result;
  }
}
