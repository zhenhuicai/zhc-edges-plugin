package hope.tool.internal.router;

import java.util.ArrayList;
import java.util.List;

public class RouterConfiguration {

  protected List<Item> items = new ArrayList<>();

  public List<Item> getItems() {
    return items;
  }

  public RouterConfiguration setItems(List<Item> items) {
    this.items = items;
    return this;
  }

  public static class Item {

    protected String moduleDir;

    protected String pagesDir = "src/pages";
    protected String pagesPath = "@/pages";
    protected String routerOutDir = "src/router/auto";
    protected String menuItem = "MenuItem";
    protected String menuItemImport = "./types";
    protected boolean disable = false;

    protected String firstLanguage = "zh-CN";
    protected String secondLanguage = "en-US";
    protected String langDir = "src/locales/langs";

    /**
     * if backend module set some configuration will be written back to backend resources this is
     * the relative dir to the project's root dir
     */
    protected String backendModuleDir;

    public boolean isDisable() {
      return disable;
    }

    public Item setDisable(boolean disable) {
      this.disable = disable;
      return this;
    }

    public String getPagesDir() {
      return pagesDir;
    }

    public Item setPagesDir(String pagesDir) {
      this.pagesDir = pagesDir;
      return this;
    }

    public String getPagesPath() {
      return pagesPath;
    }

    public Item setPagesPath(String pagesPath) {
      this.pagesPath = pagesPath;
      return this;
    }

    public String getRouterOutDir() {
      return routerOutDir;
    }

    public Item setRouterOutDir(String routerOutDir) {
      this.routerOutDir = routerOutDir;
      return this;
    }

    public String getMenuItem() {
      return menuItem;
    }

    public Item setMenuItem(String menuItem) {
      this.menuItem = menuItem;
      return this;
    }

    public String getMenuItemImport() {
      return menuItemImport;
    }

    public Item setMenuItemImport(String menuItemImport) {
      this.menuItemImport = menuItemImport;
      return this;
    }

    public String getModuleDir() {
      return moduleDir;
    }

    public Item setModuleDir(String moduleDir) {
      this.moduleDir = moduleDir;
      return this;
    }

    public String getFirstLanguage() {
      return firstLanguage;
    }

    public Item setFirstLanguage(String firstLanguage) {
      this.firstLanguage = firstLanguage;
      return this;
    }

    public String getSecondLanguage() {
      return secondLanguage;
    }

    public Item setSecondLanguage(String secondLanguage) {
      this.secondLanguage = secondLanguage;
      return this;
    }

    public String getLangDir() {
      return langDir;
    }

    public Item setLangDir(String langDir) {
      this.langDir = langDir;
      return this;
    }

    public String getBackendModuleDir() {
      return backendModuleDir;
    }

    public Item setBackendModuleDir(String backendModuleDir) {
      this.backendModuleDir = backendModuleDir;
      return this;
    }
  }
}
