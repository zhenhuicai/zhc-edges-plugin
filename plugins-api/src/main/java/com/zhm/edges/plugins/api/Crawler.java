package com.zhm.edges.plugins.api;

import com.zhm.edges.plugins.api.job.JobContext;
import com.zhm.edges.plugins.api.job.vo.VendorProfile;
import com.zhm.edges.plugins.api.playwright.BrowserTaskCanceller;
import com.zhm.edges.plugins.api.playwright.BrowserTaskProcessSupplier;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.zhm.edges.plugins.api.utils.Utils;
import org.pf4j.ExtensionPoint;

public interface Crawler extends ExtensionPoint {

  /**
   * Version of this vendor? we better put it to plugin description not here ?
   *
   * @return string
   */
  default String version() {
    return "0.0.1";
  }

  /**
   * Tags of this vendor
   *
   * @return list of string
   */
  default List<String> tags() {
    return Collections.emptyList();
  }

  default int displayOrder() {
    return 100;
  }

  /**
   * Customize name easy pick
   *
   * @return string
   */
  String name();

  /**
   * This is unique globally, the id should always be qualified: {@code com.example.plugin.abc}
   *
   * @return string
   */
  String id();

  /**
   * Home page just for show up
   *
   * @return url
   */
  String homePage();

  /**
   * Customized description of this vendor plugin
   *
   * @return string
   */
  String description();

  /**
   * Whether this vendor supports anonymous (unauthenticated) access.
   *
   * @return true if anonymous access is supported, false otherwise
   */
  default boolean supportsAnonymousAccess() {
    return true;
  }

  /**
   * Do the job assign from application layer
   *
   * @param jobContext job context
   * @param supplier Browser process supplier
   */
  default Optional<BrowserTaskCanceller> job(
      JobContext jobContext, BrowserTaskProcessSupplier supplier) {
    return Optional.empty();
  }

  /**
   * @param supplier
   * @param afterLogin
   */
  default void login(BrowserTaskProcessSupplier supplier, Runnable afterLogin) {
    login(supplier, afterLogin, null);
  }

  /**
   * Trigger a login manually with after login process:
   *
   * @param supplier Browser process supplier
   * @param afterLogin the action after login
   * @param possibleJobContext may not present
   */
  default void login(
      BrowserTaskProcessSupplier supplier, Runnable afterLogin, JobContext possibleJobContext) {}

  /**
   * Navigator to specific page with head or headless model
   *
   * @param url the specific url of this navigator
   * @param headless whether headless or head
   * @param afterAction runnable action after this navigator
   */
  default void navigator(final String url, boolean headless, final Runnable afterAction) {}

  /**
   * 获取供应商配置路径 Vendor Local state cache dir: {@code {workdir}/vendors/{normalized_id} }
   *
   * @return {@link Path} 供应商配置的本地路径
   */
  default Path configurationPath() {
    // 检查应用是否已启动
    Path vendorsPath = WorkingDirContext.INSTANCE.vendorsWithAccount();

    // 规范化ID并获取默认供应商配置
    String _id = id().trim().replace(".", "_").replace("-", "_");
    VendorProfile defaultProfile = AccountContext.getDefaultProfile(id());
    if (defaultProfile != null) {
      String profileDir = defaultProfile.getDir();
      return Utils.resolveSubDirs(vendorsPath, true, _id, profileDir);
    } else {
      return Utils.resolveSubDirs(vendorsPath, true, _id);
    }
  }

  default Path statePath() {
    return configurationOfSessionPath().resolve("state.json");
  }

  default Path configurationOfSessionPath() {
    return Utils.resolveSubDirs(configurationPath(), true, "sessions");
  }
}
