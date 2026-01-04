package hope.tool.internal.router.build.view;

import java.util.ArrayList;
import java.util.List;

public class MenuView {

  protected String name;

  /**
   * route path {@code /page/example} IF the path is strictly design according to the pattern, it
   * can be composite as tree
   */
  protected String path;

  /** the page title of this router */
  protected String title;

  /** the i18n key of this router */
  protected String i18key;

  /** Icon name {@code lucide:search} */
  protected String icon;

  /** whether it should be hidden */
  protected Boolean hidden;

  protected NodeType type = NodeType.ROOT;

  protected int level;

  protected String parentPath;

  protected List<MenuView> children;

  protected int displayOrder;

  public String getName() {
    return name;
  }

  public MenuView setName(String name) {
    this.name = name;
    return this;
  }

  public String getPath() {
    return path;
  }

  public MenuView setPath(String path) {
    this.path = path;
    return this;
  }

  public List<MenuView> getChildren() {
    return children;
  }

  public MenuView setChildren(List<MenuView> children) {
    this.children = children;
    return this;
  }

  public String getTitle() {
    return title;
  }

  public MenuView setTitle(String title) {
    this.title = title;
    return this;
  }

  public String getI18key() {
    return i18key;
  }

  public MenuView setI18key(String i18key) {
    this.i18key = i18key;
    return this;
  }

  public String getIcon() {
    return icon;
  }

  public MenuView setIcon(String icon) {
    this.icon = icon;
    return this;
  }

  public Boolean getHidden() {
    return hidden;
  }

  public MenuView setHidden(Boolean hidden) {
    this.hidden = hidden;
    return this;
  }

  public NodeType getType() {
    return type;
  }

  public MenuView setType(NodeType type) {
    this.type = type;
    return this;
  }

  public int getLevel() {
    return level;
  }

  public MenuView setLevel(int level) {
    this.level = level;
    return this;
  }

  public String getParentPath() {
    return parentPath;
  }

  public MenuView setParentPath(String parentPath) {
    this.parentPath = parentPath;
    return this;
  }

  public MenuView addChild(MenuView res) {
    if (children == null) {
      children = new ArrayList<>();
    }
    children.add(res);
    return this;
  }

  public int getDisplayOrder() {
    return displayOrder;
  }

  public MenuView setDisplayOrder(int displayOrder) {
    this.displayOrder = displayOrder;
    return this;
  }
}
