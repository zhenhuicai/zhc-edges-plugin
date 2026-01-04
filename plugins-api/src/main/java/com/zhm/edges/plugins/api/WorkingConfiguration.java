package com.zhm.edges.plugins.api;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.pf4j.RuntimeMode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(prefix = "work")
public class WorkingConfiguration {

  /** If not set then create and init a temp dir {@code work.dir} */
  protected String dir;

  @NestedConfigurationProperty protected PluginProperties plugin = new PluginProperties();

  public String getDir() {
    return dir;
  }

  public WorkingConfiguration setDir(String dir) {
    this.dir = dir;
    return this;
  }

  public PluginProperties getPlugin() {
    return plugin;
  }

  public WorkingConfiguration setPlugin(PluginProperties plugin) {
    this.plugin = plugin;
    return this;
  }

  public class PluginProperties {
    public static final String GRADLE_LIBS_DIR = "build/libs";

    /** Auto start plugin when main app is ready. */
    private boolean autoStartPlugin = true;

    /** Auto check/install/update plugins on startup using pf4j-update. */
    private boolean autoUpdateOnStartup = true;

    /**
     * The default plugin path is obtained through file scanning. In the development mode, you can
     * specify the plugin path as the project directory.
     */
    private List<Path> fixedPluginPath = new ArrayList<>();

    /** Plugins disabled by default. */
    private String[] disabledPlugins = new String[0];

    /** Plugins enabled by default, prior to `disabledPlugins`. */
    private String[] enabledPlugins = new String[0];

    /**
     * Set to true to allow requires expression to be exactly x.y.z. The default is false, meaning
     * that using an exact version x.y.z will implicitly mean the same as >=x.y.z.
     */
    private boolean exactVersionAllowed = false;

    /** Extended Plugin Class Directory. */
    private List<String> classesDirectories =
        new ArrayList<>(List.of("build/classes/java/main", "build/resources/main"));

    /** Extended Plugin Jar Directory. */
    private List<String> libDirectories = new ArrayList<>(List.of(GRADLE_LIBS_DIR));

    /** Runtime Mode：development/deployment. */
    private RuntimeMode runtimeMode = RuntimeMode.DEPLOYMENT;

    private String pluginsHome = "https://download.zhenhuicai.net/plugins/";

    public boolean isAutoStartPlugin() {
      return autoStartPlugin;
    }

    public PluginProperties setAutoStartPlugin(boolean autoStartPlugin) {
      this.autoStartPlugin = autoStartPlugin;
      return this;
    }

    public boolean isAutoUpdateOnStartup() {
      return autoUpdateOnStartup;
    }

    public PluginProperties setAutoUpdateOnStartup(boolean autoUpdateOnStartup) {
      this.autoUpdateOnStartup = autoUpdateOnStartup;
      return this;
    }

    public List<Path> getFixedPluginPath() {
      return fixedPluginPath;
    }

    public PluginProperties setFixedPluginPath(List<Path> fixedPluginPath) {
      this.fixedPluginPath = fixedPluginPath;
      return this;
    }

    public String[] getDisabledPlugins() {
      return disabledPlugins;
    }

    public PluginProperties setDisabledPlugins(String[] disabledPlugins) {
      this.disabledPlugins = disabledPlugins;
      return this;
    }

    public String[] getEnabledPlugins() {
      return enabledPlugins;
    }

    public PluginProperties setEnabledPlugins(String[] enabledPlugins) {
      this.enabledPlugins = enabledPlugins;
      return this;
    }

    public boolean isExactVersionAllowed() {
      return exactVersionAllowed;
    }

    public PluginProperties setExactVersionAllowed(boolean exactVersionAllowed) {
      this.exactVersionAllowed = exactVersionAllowed;
      return this;
    }

    public List<String> getClassesDirectories() {
      return classesDirectories;
    }

    public PluginProperties setClassesDirectories(List<String> classesDirectories) {
      this.classesDirectories = classesDirectories;
      return this;
    }

    public List<String> getLibDirectories() {
      return libDirectories;
    }

    public PluginProperties setLibDirectories(List<String> libDirectories) {
      this.libDirectories = libDirectories;
      return this;
    }

    public RuntimeMode getRuntimeMode() {
      return runtimeMode;
    }

    public PluginProperties setRuntimeMode(RuntimeMode runtimeMode) {
      this.runtimeMode = runtimeMode;
      return this;
    }

    public String getPluginsHome() {
      return pluginsHome;
    }

    public PluginProperties setPluginsHome(String pluginsHome) {
      this.pluginsHome = pluginsHome;
      return this;
    }
  }
}
