package com.zhm.edges.plugins.api;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public enum WorkingDirContext {
  INSTANCE;

  public static List<WorkDirSetter> setters =
      new ArrayList<>() {
        {
          add(new WorkDirSetter("downloads", path -> WorkingDirContext.INSTANCE.downloads = path));
          add(new WorkDirSetter("temp", path -> WorkingDirContext.INSTANCE.temp = path));
          add(
              new WorkDirSetter(
                  "playwright", path -> WorkingDirContext.INSTANCE.playwright = path));
          add(new WorkDirSetter("chrome", path -> WorkingDirContext.INSTANCE.chrome = path));
          add(new WorkDirSetter("uploads", path -> WorkingDirContext.INSTANCE.uploads = path));
          add(new WorkDirSetter("db", path -> WorkingDirContext.INSTANCE.db = path));
          add(new WorkDirSetter("others", path -> WorkingDirContext.INSTANCE.others = path));
          add(new WorkDirSetter("logger", path -> WorkingDirContext.INSTANCE.logger = path));
          add(new WorkDirSetter("java", path -> WorkingDirContext.INSTANCE.java = path));
          add(new WorkDirSetter("libs", path -> WorkingDirContext.INSTANCE.libs = path));
          add(new WorkDirSetter("jobs", path -> WorkingDirContext.INSTANCE.jobs = path));
          add(new WorkDirSetter("backup", path -> WorkingDirContext.INSTANCE.backup = path));
          add(new WorkDirSetter("sessions", path -> WorkingDirContext.INSTANCE.sessions = path));
          add(new WorkDirSetter("vendors", path -> WorkingDirContext.INSTANCE.vendors = path));
          add(new WorkDirSetter("config", path -> WorkingDirContext.INSTANCE.config = path));
          add(new WorkDirSetter("plugins", path -> WorkingDirContext.INSTANCE.plugins = path));
        }
      };

  /**
   *
   *
   * <ul>
   *   <li>downloads: all the download things
   * </ul>
   */
  public Path downloads;

  public Path temp;
  public Path sessions;
  public Path playwright;
  public Path chrome;
  public Path uploads;
  public Path db;
  public Path jobs;
  public Path others;
  public Path logger;
  public Path java;
  public Path engine;
  public Path backup;
  public Path vendors;
  public Path config;
  public Path plugins;
  public Path libs;

  public Path downloads() {
    return downloads;
  }

  public Path temp() {
    return temp;
  }

  public Path sessions() {
    return sessions;
  }

  public Path playwright() {
    return playwright;
  }

  public Path chrome() {
    return chrome;
  }

  public Path uploads() {
    return uploads;
  }

  public Path db() {
    return db;
  }

  public Path others() {
    return others;
  }

  public Path logger() {
    return logger;
  }

  public Path java() {
    return java;
  }

  public Path engine() {
    return engine;
  }

  public Path libs() {
    return libs;
  }

  public Path jobs() {
    return jobs;
  }

  public Path backup() {
    return backup;
  }

  public Path vendors() {
    return vendors;
  }

  public Path config() {
    return config;
  }

  public Path plugins() {
    return plugins;
  }

  /**
   * 获取基于当前账户的作业路径
   *
   * @return 当前账户的作业路径
   */
  public Path jobsWithAccount() {
    return getAccountPath(jobs);
  }

  /**
   * 获取基于当前账户的供应商路径
   *
   * @return 当前账户的供应商路径
   */
  public Path vendorsWithAccount() {
    return getAccountPath(vendors);
  }

  /**
   * 基于当前账户获取指定基础路径的子路径
   *
   * @param basePath 基础路径
   * @return 包含账户子目录的完整路径
   */
  private Path getAccountPath(Path basePath) {
    String accountId = getCurrentAccount();
    return basePath.resolve(accountId);
  }

  public Path uploadsWithAccount() {
    return getAccountPath(uploads);
  }

  /**
   * 获取当前账户ID，如果未绑定则返回默认值 "default"
   *
   * @return 当前账户ID
   */
  private String getCurrentAccount() {
    return AccountContext.getCurrentAccount();
  }

  public static class WorkDirSetter {
    private String name;
    private Consumer<Path> pathConsumer;

    public WorkDirSetter() {}

    public WorkDirSetter(String name, Consumer<Path> pathConsumer) {
      this.name = name;
      this.pathConsumer = pathConsumer;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public Consumer<Path> getPathConsumer() {
      return pathConsumer;
    }

    public void setPathConsumer(Consumer<Path> pathConsumer) {
      this.pathConsumer = pathConsumer;
    }
  }
}
