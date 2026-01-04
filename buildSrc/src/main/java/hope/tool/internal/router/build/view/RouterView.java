package hope.tool.internal.router.build.view;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class RouterView implements TreeWire<RouterView> {

  public String name;

  /**
   * route path {@code /page/example} IF the path is strictly design according to the pattern, it
   * can be composite as tree
   */
  public String path;

  /** Component name for example: {@code ExampleTable} */
  public String component;

  public Meta meta;

  // --------------------Tree property--------------------
  public List<RouterView> children;

  @JsonIgnore public transient RouterView parent;

  @JsonIgnore public transient Path filePath;

  public String getPath() {
    return path;
  }

  public RouterView setPath(String path) {
    this.path = path;
    return this;
  }

  private void checkMeta() {
    if (meta == null) {
      meta = new Meta();
    }
  }

  @JsonIgnore
  public String getI18key() {
    checkMeta();
    return meta.getI18key();
  }

  public RouterView setI18key(String i18key) {
    checkMeta();
    meta.setI18key(i18key);
    return this;
  }

  public RouterView setIcon(String icon) {
    checkMeta();
    meta.setIcon(icon);
    return this;
  }

  public RouterView setLayout(String layout) {
    checkMeta();
    meta.setLayout(layout);
    return this;
  }

  public RouterView setFullPath(String path) {
    checkMeta();
    meta.setFullPath(path);
    return this;
  }

  public String getComponent() {
    return component;
  }

  public RouterView setComponent(String component) {
    this.component = component;
    return this;
  }

  public RouterView setHidden(boolean hidden) {
    checkMeta();
    meta.setHidden(hidden);
    return this;
  }

  public List<RouterView> getChildren() {
    return children;
  }

  public RouterView setChildren(List<RouterView> children) {
    this.children = children;
    return this;
  }

  @JsonIgnore
  public RouterView getParent() {
    return parent;
  }

  public RouterView setParent(RouterView parent) {
    this.parent = parent;
    return this;
  }

  @Override
  public RouterView addChild(RouterView child) {
    if (children == null) {
      children = new ArrayList<>();
    }
    children.add(child);
    return this;
  }

  public RouterView setDisplayOrder(int displayOrder) {
    checkMeta();
    meta.setDisplayOrder(displayOrder);
    return this;
  }

  @Override
  public RouterView setLevel(int level) {
    checkMeta();
    meta.setLevel(level);
    return this;
  }

  @JsonIgnore
  public String getTitle() {
    checkMeta();
    return meta.getTitle();
  }

  public RouterView setTitle(String title) {
    checkMeta();
    meta.setTitle(title);
    return this;
  }

  @JsonIgnore
  @Override
  public String path() {
    return getPath();
  }

  @Override
  @JsonIgnore
  public int childrenCount() {
    return children != null ? children.size() : 0;
  }

  @JsonIgnore
  public Path getFilePath() {
    return filePath;
  }

  public RouterView setFilePath(Path filePath) {
    this.filePath = filePath;
    return this;
  }

  @JsonIgnore
  @Override
  public NodeType getType() {
    checkMeta();
    return meta.getType();
  }

  public RouterView setType(NodeType type) {
    checkMeta();
    meta.setType(type);
    return this;
  }

  public Meta getMeta() {
    return meta;
  }

  public RouterView setMeta(Meta meta) {
    this.meta = meta;
    return this;
  }

  public String getName() {
    return name;
  }

  public RouterView setName(String name) {
    this.name = name;
    return this;
  }

  public static class Meta {

    /** the page title of this router */
    public String title;

    /** the i18n key of this router */
    public String i18key;

    /** Icon name {@code lucide:search} */
    public String icon;

    /** layout name dependent on front settings */
    public String layout;

    /** whether it should be hidden */
    public Boolean hidden;

    public NodeType type = NodeType.ROOT;

    public int level;

    /** bigger topper */
    public int displayOrder;

    public String fullPath;

    public int getDisplayOrder() {
      return displayOrder;
    }

    public Meta setDisplayOrder(int displayOrder) {
      this.displayOrder = displayOrder;
      return this;
    }

    public String getTitle() {
      return title;
    }

    public Meta setTitle(String title) {
      this.title = title;
      return this;
    }

    public String getI18key() {
      return i18key;
    }

    public Meta setI18key(String i18key) {
      this.i18key = i18key;
      return this;
    }

    public String getIcon() {
      return icon;
    }

    public Meta setIcon(String icon) {
      this.icon = icon;
      return this;
    }

    public String getLayout() {
      return layout;
    }

    public Meta setLayout(String layout) {
      this.layout = layout;
      return this;
    }

    public Boolean isHidden() {
      return hidden;
    }

    public Meta setHidden(Boolean hidden) {
      this.hidden = hidden;
      return this;
    }

    public NodeType getType() {
      return type;
    }

    public Meta setType(NodeType type) {
      this.type = type;
      return this;
    }

    public int getLevel() {
      return level;
    }

    public Meta setLevel(int level) {
      this.level = level;
      return this;
    }

    public String getFullPath() {
      return fullPath;
    }

    public Meta setFullPath(String path) {
      this.fullPath = path;
      return this;
    }
  }
}
