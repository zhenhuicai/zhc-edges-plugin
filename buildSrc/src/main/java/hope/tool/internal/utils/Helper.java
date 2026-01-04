package hope.tool.internal.utils;

import org.gradle.api.Project;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class Helper {

  // Java keywords that cannot be used as package names
  static Set<String> javaKeywords =
      new HashSet<>(
          Arrays.asList(
              "abstract",
              "assert",
              "boolean",
              "break",
              "byte",
              "case",
              "catch",
              "char",
              "class",
              "const",
              "continue",
              "default",
              "do",
              "double",
              "else",
              "enum",
              "extends",
              "final",
              "finally",
              "float",
              "for",
              "goto",
              "if",
              "implements",
              "import",
              "instanceof",
              "int",
              "interface",
              "long",
              "native",
              "new",
              "package",
              "private",
              "protected",
              "public",
              "return",
              "short",
              "record",
              "static",
              "strictfp",
              "super",
              "switch",
              "synchronized",
              "this",
              "throw",
              "throws",
              "transient",
              "try",
              "void",
              "wire",
              "stub",
              "volatile",
              "while"));

  public static String getCurrentGroup(Project project) {
    try {
      Path buildGradlePath = project.getRootProject().getBuildFile().toPath();
      String content = Files.readString(buildGradlePath);

      // Look for group = 'com.example' or group = "com.example"
      java.util.regex.Pattern pattern =
          java.util.regex.Pattern.compile("group\\s*=\\s*['\"]([^'\"]+)['\"]");
      java.util.regex.Matcher matcher = pattern.matcher(content);

      if (matcher.find()) {
        return matcher.group(1);
      }
      return null;
    } catch (IOException e) {
      project.getLogger().error("Failed to read build.gradle", e);
      return null;
    }
  }

  public static void cleanupEmptyDirectories(Path directory) throws IOException {
    Files.walk(directory)
        .sorted(Comparator.reverseOrder())
        .filter(path -> path.toFile().isDirectory())
        .filter(path -> path.toFile().list().length == 0)
        .forEach(
            path -> {
              try {
                Files.delete(path);
              } catch (IOException e) {
                // Log error but continue
                // Fail quite
              }
            });
  }

  /**
   * Checks if a package name is legal according to Java naming conventions. Rules checked: <code>
   *
   *    1. Package name cannot be null or empty
   *    2. Package segments must start with a letter (a-z)
   *    3. Package segments can only contain letters (a-z), numbers (0-9), and underscore (_)
   *    4. Package segments cannot be Java keywords 5. Package segments are separated by dots (.)
   *
   * </code>
   *
   * @param packageName the package name to validate
   * @return true if the package name is legal, false otherwise
   */
  public static boolean isValidPackageName(String packageName) {
    // Check if package name is null or empty
    if (packageName == null || packageName.isEmpty()) {
      return false;
    }

    // Split package name into segments
    final String[] segments = packageName.split("\\.");

    // Check if package name starts or ends with a dot
    if (packageName.startsWith(".") || packageName.endsWith(".")) {
      return false;
    }

    // Check each segment
    for (String segment : segments) {
      // Check if segment is empty
      if (segment.isEmpty()) {
        return false;
      }

      // Check if segment is a Java keyword
      if (javaKeywords.contains(segment)) {
        return false;
      }

      // Check if first character is a letter
      if (!Character.isLowerCase(segment.charAt(0))) {
        return false;
      }

      // Check if segment contains only valid characters
      if (!segment.matches("^[a-z][a-z0-9_]*$")) {
        return false;
      }
    }

    return true;
  }
}
