package hope.tool.internal.router.build;

import static hope.tool.internal.router.build.Utils.isNotBlank;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import hope.tool.internal.router.RouterConfiguration;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;

public class I18NWriter {

  public static void mergerPageTitleLanguage(
      final Map<String, Object> pageTitles,
      final VueAutoRouterGenerator parent,
      final ObjectMapper objectMapper,
      final Logger logger) {

    for (Map.Entry<String, Object> row : pageTitles.entrySet()) {
      String page = row.getKey();
      // Must
      try {
        Map<String, Object> each = (Map<String, Object>) row.getValue();
        ObjectNode newPageTitleI18n = convertMapToObjectNode(each, objectMapper);
        mergerPageTitleLanguage(parent, objectMapper, page, newPageTitleI18n);
      } catch (Throwable throwable) {
        // Let it go
        logger.warn("fail converter page " + page, throwable);
      }
    }
  }

  private static void mergerPageTitleLanguage(
      final VueAutoRouterGenerator parent,
      final ObjectMapper objectMapper,
      final String page,
      final ObjectNode pageI18n)
      throws IOException {

    final RouterConfiguration.Item routerItemConfiguration = parent.routerItemConfiguration;

    String targetName = page + ".json";
    final List<String> languages = new ArrayList<>();
    if (isNotBlank(routerItemConfiguration.getFirstLanguage())) {
      languages.add(routerItemConfiguration.getFirstLanguage());
    }
    if (isNotBlank(routerItemConfiguration.getSecondLanguage())) {
      languages.add(routerItemConfiguration.getSecondLanguage());
    }
    if (languages.isEmpty()) {
      languages.add("zh-CN");
    }
    for (String language : languages) {
      final Path form18Path =
          Paths.get(
              parent.moduleDir.toString(),
              routerItemConfiguration.getLangDir(),
              language,
              "pages",
              targetName);

      ObjectNode _newOne = pageI18n;
      if (form18Path.toFile().exists()) {
        _newOne = merger((ObjectNode) objectMapper.readTree(form18Path.toFile()), pageI18n);
      }

      Writer.writeToPath(
          Paths.get(parent.moduleDir.toString(), routerItemConfiguration.getLangDir(), language),
          "pages",
          targetName,
          objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(_newOne));
    }
  }

  /**
   * Converts a Map to an ObjectNode
   *
   * @param map The map to convert
   * @return An ObjectNode representation of the map
   */
  public static ObjectNode convertMapToObjectNode(
      final Map<String, Object> map, final ObjectMapper objectMapper) {
    if (map == null) {
      return objectMapper.createObjectNode();
    }

    ObjectNode objectNode = objectMapper.createObjectNode();

    map.forEach(
        (key, value) -> {
          if (value == null) {
            objectNode.putNull(key);
          } else if (value instanceof String) {
            objectNode.put(key, (String) value);
          } else if (value instanceof Integer) {
            objectNode.put(key, (Integer) value);
          } else if (value instanceof Long) {
            objectNode.put(key, (Long) value);
          } else if (value instanceof Double) {
            objectNode.put(key, (Double) value);
          } else if (value instanceof Boolean) {
            objectNode.put(key, (Boolean) value);
          } else if (value instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> nestedMap = (Map<String, Object>) value;
            objectNode.set(key, convertMapToObjectNode(nestedMap, objectMapper));
          } else {
            // For other types, convert to String or use valueToTree for complex objects
            try {
              objectNode.set(key, objectMapper.valueToTree(value));
            } catch (Exception e) {
              objectNode.put(key, value.toString());
            }
          }
        });

    return objectNode;
  }

  /**
   * Merges two ObjectNodes recursively If a field exists in both nodes, it will be merged
   * recursively If a field only exists in newNode, it will be added to existingNode
   *
   * @param existingNode The existing ObjectNode that will be modified
   * @param newNode The new ObjectNode whose fields will be merged into existingNode
   * @return The merged ObjectNode (same instance as existingNode)
   */
  public static ObjectNode merger(final ObjectNode existingNode, ObjectNode newNode) {
    if (existingNode == null || newNode == null) {
      return existingNode;
    }
    newNode
        .fields()
        .forEachRemaining(
            entry -> {
              String fieldName = entry.getKey();
              if (existingNode.has(fieldName)) {
                // If field exists in both nodes
                if (entry.getValue().isObject() && existingNode.get(fieldName).isObject()) {
                  // Recursively merge if both are objects
                  merger((ObjectNode) existingNode.get(fieldName), (ObjectNode) entry.getValue());
                } else {
                  // Replace with new value if not both objects
                  final JsonNode _node = existingNode.get(fieldName);
                  if (_node.isTextual()) {
                    String oldValue = _node.asText();
                    // Default value overwrite with new value
                    if (oldValue.startsWith("$")) {
                      existingNode.set(fieldName, entry.getValue().deepCopy());
                    }
                  } else {
                    existingNode.set(fieldName, entry.getValue().deepCopy());
                  }
                }
              } else {
                // If field only exists in newNode, add it to existingNode
                existingNode.set(fieldName, entry.getValue().deepCopy());
              }
            });

    return existingNode;
  }
}
