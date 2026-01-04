package hope.tool.internal.router.build.view;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

public interface TreeWire<T extends TreeWire> {

  T addChild(T child);

  T setParent(T parent);

  T setLevel(int level);

  @JsonIgnore
  String path();

  @JsonIgnore
  int childrenCount();

  NodeType getType();

  List<T> getChildren();
}
