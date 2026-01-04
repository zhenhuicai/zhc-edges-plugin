package hope.tool.internal.router.build;

import static hope.tool.internal.router.build.Utils.HOPE_NO_MERGER;
import static hope.tool.internal.router.build.Utils.isNotBlank;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Writer {

  public static Path writeToPath(
      final Path directory, final String packageName, final String name, final String body)
      throws IOException {
    assert (Files.notExists(directory) || Files.isDirectory(directory))
        : "path " + directory.toAbsolutePath() + " exists but is not a directory.";

    Path outputDirectory = directory;

    if (isNotBlank(packageName)) {
      for (String packageComponent : packageName.split("\\.")) {
        outputDirectory = outputDirectory.resolve(packageComponent);
      }
      Files.createDirectories(outputDirectory);
    } else {
      Files.createDirectories(outputDirectory);
    }

    final Path outputPath = outputDirectory.resolve(name);

    // Json can not mark
    if (outputPath.toFile().exists() && (name.endsWith(".ts") || name.endsWith("js"))) {
      // Is it has  `// hope-no-merger` then skip output
      final List<String> lines = Files.readAllLines(outputPath);
      boolean skip = false;
      if (!lines.isEmpty()) {
        for (final String line : lines) {
          if (line.trim().startsWith(HOPE_NO_MERGER)) {
            skip = true;
            break;
          }
        }
      }
      if (skip) {
        // no merger
        return outputPath;
      }
    }

    try (java.io.Writer writer = new OutputStreamWriter(Files.newOutputStream(outputPath), UTF_8)) {
      writer.write(body);
    }
    return outputPath;
  }
}
