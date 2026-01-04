package com.zhm.edges.plugins.api.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class PathUtils {

  static Logger logger = LoggerFactory.getLogger(PathUtils.class);

  /**
   * Returns the Path of a subdirectory (or nested subdirectories) under the given parent Path.
   * Accepts multiple subdirectory names and resolves them in order.
   *
   * @param parent the parent directory Path
   * @param subDirs the names of the subdirectories (can be nested)
   * @return the Path of the nested subdirectory
   */
  public static Path resolveSubDirs(Path parent, String... subDirs) {
    return resolveSubDirs(parent, false, subDirs);
  }

  public static Path resolveSubDirs(
      Path parent, boolean createDirectoriesIfNotExist, String... subDirs) {
    Path result = parent;
    for (String sub : subDirs) {
      result = result.resolve(sub);
    }
    if (createDirectoriesIfNotExist) {
      try {
        Files.createDirectories(result);
      } catch (IOException e) {
        logger.warn("Fail create dir {}", result, e);
      }
    }
    return result;
  }
}
