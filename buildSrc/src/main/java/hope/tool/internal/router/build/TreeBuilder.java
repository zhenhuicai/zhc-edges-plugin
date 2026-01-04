package hope.tool.internal.router.build;

import hope.tool.internal.router.build.view.NodeType;
import hope.tool.internal.router.build.view.TreeWire;
import java.util.*;

abstract class TreeBuilder<TYPE extends TreeWire<TYPE>> {

  protected abstract String[] segments(TYPE item);

  protected abstract TYPE root(final String path);

  protected abstract TYPE leaf(TYPE leaf);

  protected abstract TYPE branch(final String path);

  protected abstract String relativePath(final String parent, final String child);

  protected String rootPath(final String segment) {
    return segment;
  }

  protected final List<TYPE> build(final List<TYPE> items) {

    final Map<String, TYPE> rawTree = new LinkedHashMap<>();
    final List<TYPE> roots = new ArrayList<>();
    for (final TYPE item : items) {
      final String[] segments = segments(item);
      final int len = segments.length;
      String path = "";
      if (segments.length > 1) {
        int i = 0, _tail = len - 1;
        for (; i < len; i++) {
          final String segment = segments[i];
          if (i == 0) {
            path = rootPath(segment);
            rawTree.computeIfAbsent(
                path,
                s -> {
                  TYPE _root = root(s);
                  roots.add(_root);
                  return _root;
                });
          } else if (i == _tail) {
            final TYPE parent = rawTree.get(path);
            TYPE leaf =
                rawTree.computeIfAbsent(
                    item.path(),
                    s -> {
                      TYPE it = leaf(item);
                      parent.addChild(it);
                      return it;
                    });
            leaf.setLevel(i);
            leaf.setParent(parent);
          } else {
            final TYPE parent = rawTree.get(path);
            path = relativePath(path, segment);
            TYPE branch =
                rawTree.computeIfAbsent(
                    path,
                    s -> {
                      TYPE it = branch(s);
                      parent.addChild(it);
                      return it;
                    });
            branch.setLevel(i);
            branch.setParent(parent);
          }
        }
      } else {
        rawTree.computeIfAbsent(
            rootPath(item.path()),
            s -> {
              TYPE node = leaf(item);
              roots.add(node);
              return node;
            });
      }
    }

    final Iterator<TYPE> iterator = roots.iterator();
    while (iterator.hasNext()) {
      TYPE _root = iterator.next();
      if (_root.childrenCount() == 0) {
        if (_root.getType() != NodeType.LEAF) {
          iterator.remove();
        }
      } else {
        // Now try to narrow them
        compress(_root.getChildren().iterator());
        if (_root.childrenCount() == 0) {
          iterator.remove();
        }
      }
    }

    return roots;
  }

  protected abstract void doCompress(final TYPE child, final TYPE grandSon);

  protected void compress(final Iterator<TYPE> children) {

    while (children.hasNext()) {
      TYPE child = children.next();
      if (child.childrenCount() == 0) {
        if (child.getType() != NodeType.LEAF) {
          children.remove();
        }
      } else {
        if (child.childrenCount() == 1) {
          // Compress, copy all child's child to grandparent
          // Change grandparent compress and name
          TYPE grandSon = child.getChildren().get(0);
          if (grandSon.childrenCount() == 0) {
            if (grandSon.getType() != NodeType.LEAF) {
              children.remove();
            }
          } else {
            doCompress(child, grandSon);
            compress(grandSon.getChildren().iterator());
          }
        } else {
          // Go next
          compress(child.getChildren().iterator());
          if (child.childrenCount() == 0) {
            children.remove();
          }
        }
      }
    }
  }
}
