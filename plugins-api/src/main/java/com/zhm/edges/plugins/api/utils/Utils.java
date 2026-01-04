package com.zhm.edges.plugins.api.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.zhm.edges.plugins.api.utils.exceptions.EngineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public abstract class Utils {

  static Logger logger = LoggerFactory.getLogger(Utils.class);

  // Encrypt a string
  public static String encrypt(String data, SecretKey key) throws Exception {
    Cipher cipher = Cipher.getInstance("AES");
    cipher.init(Cipher.ENCRYPT_MODE, key);
    byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
    return Base64.getEncoder().encodeToString(encrypted);
  }

  // Decrypt a string
  public static String decrypt(String encryptedData, SecretKey key) throws Exception {
    Cipher cipher = Cipher.getInstance("AES");
    cipher.init(Cipher.DECRYPT_MODE, key);
    byte[] decoded = Base64.getDecoder().decode(encryptedData);
    byte[] decrypted = cipher.doFinal(decoded);
    return new String(decrypted, StandardCharsets.UTF_8);
  }

  // Convert SecretKey to String for storage
  public static String keyToString(SecretKey key) {
    return Base64.getEncoder().encodeToString(key.getEncoded());
  }

  // Convert String to SecretKey
  public static SecretKey stringToKey(String keyStr) {
    byte[] decoded = Base64.getDecoder().decode(keyStr);
    return new SecretKeySpec(decoded, 0, decoded.length, "AES");
  }


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

  public static Set<String> normalizeInput(final String input) {
    if (!StringUtils.hasText(input)) {
      return Collections.emptySet();
    }
    final String[] inputs = input.split("\n");
    Set<String> cleanInputs = new LinkedHashSet<>();
    for (final String i : inputs) {
      if (StringUtils.hasText(i)) {
        cleanInputs.add(i.trim());
      }
    }
    return cleanInputs;
  }

  public static void safeRunner(final Runnable runnable) {
    try {
      runnable.run();
    } catch (Throwable throwable) {
      logger.warn("fail run", throwable);
    }
  }

  public static void verifyDirectory(final Path... paths) {

    for (final Path path : paths) {
      File file = path.toFile();
      if (!file.exists()) {
        try {
          Files.createDirectories(path);
        } catch (IOException e) {
          logger.warn("Fail create dir {}", paths, e);
        }
      }
    }
  }

  public static void unzipJarToTargetDir(Path targetJarPath, Path targetJarUnzipPath)
      throws IOException {
    JarFile jar = new JarFile(targetJarPath.toFile());
    try {
      Enumeration<JarEntry> entries = jar.entries();
      while (entries.hasMoreElements()) {
        JarEntry entry = entries.nextElement();
        File file = new File(targetJarUnzipPath.toString(), entry.getName());
        if (entry.isDirectory()) {
          file.mkdirs();
        } else {
          file.getParentFile().mkdirs();
          try (InputStream in = jar.getInputStream(entry);
              OutputStream out = new FileOutputStream(file)) {
            in.transferTo(out);
          }
        }
      }
    } finally {
      try {
        jar.close();
      } catch (Throwable throwable) {
        // Close quite
      }
    }
  }

  public static String getMajorJavaVersion() {
    String version = System.getProperty("java.version");
    if (version.startsWith("1.")) {
      return version.substring(2, 3);
    }
    int dot = version.indexOf(".");
    if (dot != -1) {
      return version.substring(0, dot);
    }
    return version;
  }

  public static String platformDir() {
    String name = System.getProperty("os.name").toLowerCase();
    String arch = System.getProperty("os.arch").toLowerCase();

    if (name.contains("windows")) {
      return "win32_x64";
    }
    if (name.contains("linux")) {
      if (arch.equals("aarch64")) {
        return "linux-arm64";
      } else {
        return "linux";
      }
    }
    if (name.contains("mac os x")) {
      if (arch.equals("aarch64")) {
        return "mac-arm64";
      } else {
        return "mac";
      }
    }
    throw new RuntimeException("Unexpected os.name value: " + name);
  }

  public static Path checkAndVerifyDir(String dir) {

    if (!StringUtils.hasText(dir)) {
      // Go to the temp dir
      // get home dir
      dir = System.getProperty("user.home");
      // mkdir dir: zhm
      dir = Paths.get(dir, "zhm").toAbsolutePath().toString();
    }
    // If this dir not exist create it
    Path path = Paths.get(dir).toAbsolutePath();
    File pathDir = path.toFile();

    if (!pathDir.exists()) {
      try {
        Files.createDirectories(path);
      } catch (IOException e) {
        throw new EngineException("work dir not exist and can not create " + pathDir, e);
      }
    } else {
      if (!pathDir.isDirectory()) {
        // This is dam wrong
        throw new EngineException("work dir exist but not a directory " + pathDir);
      }
    }
    return path;
  }

  private static boolean isExecutable(Path filePath) {
    String name = filePath.getFileName().toString();
    return name.endsWith(".sh") || name.endsWith(".exe") || !name.contains(".");
  }

  public static void extractZipFile(Path zipFile, Path targetDir) {
    try (FileSystem zipFs = FileSystems.newFileSystem(zipFile, (ClassLoader) null)) {
      Path root = zipFs.getPath("/");

      // 找到ZIP文件内的顶层目录（driver-bundle-xxx）
      final Path driverBundleDir;
      try (var stream = Files.list(root)) {
        driverBundleDir =
            stream
                .filter(Files::isDirectory)
                .filter(p -> p.getFileName().toString().startsWith("driver-bundle-"))
                .findFirst()
                .orElseThrow(() -> new IOException("未找到driver-bundle目录"));
      }

      // 从driver-bundle目录开始遍历，直接解压到目标目录
      Files.walk(driverBundleDir)
          .filter(path -> !Files.isDirectory(path))
          .forEach(
              path -> {
                try {
                  // 计算相对于driver-bundle目录的路径
                  Path relative = driverBundleDir.relativize(path);
                  Path target = targetDir.resolve(relative.toString());

                  // 创建父目录
                  Files.createDirectories(target.getParent());

                  // 复制文件
                  Files.copy(path, target, StandardCopyOption.REPLACE_EXISTING);

                  // 设置可执行权限
                  if (isExecutable(target)) {
                    target.toFile().setExecutable(true, true);
                  }

                } catch (IOException e) {
                  throw new RuntimeException("Failed to extract file: " + path, e);
                }
              });
    } catch (IOException e) {
      throw new RuntimeException("Failed to extract zip file: " + zipFile, e);
    }
  }
}
