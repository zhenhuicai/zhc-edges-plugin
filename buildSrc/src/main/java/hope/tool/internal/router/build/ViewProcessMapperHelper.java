package hope.tool.internal.router.build;

import hope.tool.internal.router.build.view.MenuView;
import hope.tool.internal.router.build.view.RouterView;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

public class ViewProcessMapperHelper {

  public static final String I18N_PAGES = "pages.";
  public static final String I18N_TITLE = ".title";

  static boolean isBlank(final String given) {
    return given == null || given.isBlank();
  }

  public static String pageI18keyNormalize(final String given) {
    if (isBlank(given)) {
      return given;
    }
    String res = given;
    if (!given.startsWith(I18N_PAGES)) {
      res = I18N_PAGES + given;
    }

    if (!given.endsWith(I18N_TITLE)) {
      res = res + I18N_TITLE;
    }
    return res;
  }

  // patch for the i18key
  public static Map<String, Object> patch18Key(final List<RouterView> views) {

    final Map<String, Object> i18Map = new LinkedHashMap<>();
    for (final RouterView view : views) {
      doPatch18Key(view, i18Map);
    }
    return i18Map;
  }

  private static void doPatch18Key(final RouterView view, Map<String, Object> i18Map) {

    if (view.getMeta() == null
        || (view.getMeta().getI18key() == null || view.getMeta().getI18key().isBlank())) {
      view.setI18key(
          Utils.specificPathCharacterReplace(trimPathChar(view.getPath()).replace('/', '.')));
    }
    // Make sure it is pages.****.title
    view.setI18key(pageI18keyNormalize(view.getI18key()));
    // Get the i18 values
    path2MapTree(view.getI18key(), view.getTitle(), i18Map);

    if (view.childrenCount() > 0) {
      // Go on do the things
      for (final RouterView child : view.getChildren()) {
        doPatch18Key(child, i18Map);
      }
    }
  }

  public static void path2MapTree(
      String i18key, final String title, final Map<String, Object> i18Map) {
    if (i18key.startsWith("pages.")) {
      i18key = i18key.substring(6);
    }
    final String[] steps = i18key.split("[.]");
    String v = title == null ? "$" + i18key : "$" + title;
    if (steps == null || steps.length == 0) {
      i18Map.put(i18key, v);
    } else {
      Map targetBucket = i18Map;
      int i = 0;
      for (; i < steps.length - 1; i++) {
        String step = steps[i];
        targetBucket = (Map) targetBucket.computeIfAbsent(step, object -> new LinkedHashMap<>());
      }
      targetBucket.put(steps[steps.length - 1], v);
    }
  }

  protected static String trimPathChar(String path) {
    if (path.charAt(0) == '/') {
      path = path.substring(1);
    }
    if (path.charAt(path.length() - 1) == '/') {
      path = path.substring(0, path.length() - 1);
    }
    return path;
  }

  public static List<RouterView> shrink4Vue(final List<RouterView> views) {
    final Stack<RouterView> reverseTree = new Stack<>();
    for (final RouterView view : views) {
      doPush(view, reverseTree);
    }
    while (reverseTree.size() > 0) {
      RouterView leaf = reverseTree.pop();
      doShrink(leaf);
    }
    return views;
  }

  protected static void doShrink(RouterView view) {
    if (view.getParent() != null) {
      // TODO
      view.setFullPath(view.getPath());
      // The path of the sub-route does not need to start with /
      // Sub-route components need to correctly configure component imports (dynamic imports are
      // recommended)
      // The parent route's component must include the <router-view> component to render the
      // sub-route
      // Sub-routes inherit the meta field from the parent route (can be merged or overridden via
      // route guards)
      final String parentPath = view.getParent().getPath();
      String myPath = view.getPath();
      myPath = myPath.substring(parentPath.length());
      if (myPath.charAt(0) == '/') {
        myPath = myPath.substring(1);
      }
      view.setPath(myPath);
    }
  }

  private static void doPush(RouterView view, final Stack<RouterView> collector) {
    collector.push(view);
    if (view.childrenCount() > 0) {
      for (final RouterView child : view.getChildren()) {
        doPush(child, collector);
      }
    }
  }

  public static List<MenuView> toMenus(final List<RouterView> views) {
    List<MenuView> res = views.stream().map(it -> toMenu(it)).collect(Collectors.toList());
    res.sort((first, second) -> second.getDisplayOrder() - first.getDisplayOrder());
    return res;
  }

  public static void populateChildren(MenuView parent, RouterView view) {
    final MenuView res = new MenuView();
    res.setTitle(view.getTitle());
    res.setPath(view.getPath());
    res.setName(view.getName());
    res.setParentPath(parent.getPath());
    populateMeta(view, res);
    parent.addChild(res);
    if (view.childrenCount() > 0) {
      for (final RouterView child : view.getChildren()) {
        populateChildren(res, child);
      }
      // After all done sort them
      res.getChildren().sort((first, second) -> second.getDisplayOrder() - first.getDisplayOrder());
    }
  }

  public static MenuView toMenu(final RouterView view) {

    MenuView res = new MenuView();
    res.setTitle(view.getTitle()).setPath(view.getPath()).setName(view.getName());

    populateMeta(view, res);

    if (view.childrenCount() > 0) {
      for (final RouterView child : view.getChildren()) {
        populateChildren(res, child);
      }
      // After all done sort them
      res.getChildren().sort((first, second) -> second.getDisplayOrder() - first.getDisplayOrder());
    }
    return res;
  }

  private static void populateMeta(RouterView view, MenuView res) {
    RouterView.Meta meta = view.meta;
    if (meta != null) {
      res.setHidden(meta.isHidden())
          .setIcon(meta.icon)
          .setI18key(meta.i18key)
          .setType(meta.getType())
          .setDisplayOrder(view.getMeta().getDisplayOrder())
          .setLevel(meta.level);
      if (res.getI18key() == null || res.getI18key().isBlank()) {
        res.setI18key(("pages" + res.getPath()).replace("/", "."));
      }
    }
  }
}
