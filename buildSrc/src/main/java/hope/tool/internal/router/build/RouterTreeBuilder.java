package hope.tool.internal.router.build;

import hope.tool.internal.router.build.view.NodeType;
import hope.tool.internal.router.build.view.RouterView;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RouterTreeBuilder extends TreeBuilder<RouterView> {
  protected String split = "/";

  public List<RouterView> process(List<RouterView> _items) {

    final List<RouterView> items =
        _items.stream()
            .filter(item -> item.getPath() != null && !item.getPath().isBlank())
            .sorted(Comparator.comparing(RouterView::getPath))
            .collect(Collectors.toList());

    return build(items);
  }

  @Override
  protected void doCompress(RouterView child, RouterView grandSon) {
    child.setPath(grandSon.getPath());
    child.setTitle(grandSon.getTitle());
    child.setChildren(grandSon.getChildren());
  }

  @Override
  protected String rootPath(String segment) {
    if (segment.charAt(0) != '/') {
      return "/" + segment;
    }
    return segment;
  }

  @Override
  protected String[] segments(RouterView item) {
    // {@code /example/form}
    String path = item.getPath();
    if (path.charAt(0) == '/') {
      path = path.substring(1);
    }

    String[] res = path.split(split);
    for (int i = 0; i < res.length; i++) {
      res[i] = Utils.specificPathCharacterReplace(res[i]);
    }
    return res;
  }

  @Override
  protected RouterView root(String path) {
    RouterView res =
        new RouterView().setPath(path.charAt(0) != '/' ? "/" + path : path).setType(NodeType.ROOT);
    return res;
  }

  @Override
  protected RouterView leaf(RouterView leaf) {
    return leaf.setType(NodeType.LEAF);
  }

  @Override
  protected RouterView branch(String path) {
    RouterView res = new RouterView().setPath(path).setType(NodeType.BRANCH);
    // Key things

    return res;
  }

  @Override
  protected String relativePath(String parent, String child) {
    return parent + split + child;
  }
}
