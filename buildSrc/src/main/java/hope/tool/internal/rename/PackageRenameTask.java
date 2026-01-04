package hope.tool.internal.rename;

import static hope.tool.internal.utils.Helper.cleanupEmptyDirectories;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hope.tool.internal.utils.Helper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.TaskAction;

/**
 * Usage of this task: <code>
 *     # pkg rename strategy: {old}-{new}
 *     -DpkgRename=com.abc-com.xyz
 *     # rename skip module: seperated by ','
 *     -DskipModules=module1,module2
 * </code>
 */
@SuppressWarnings("Duplicates")
public class PackageRenameTask extends DefaultTask {

  static final String HOPE_STUB = "com.apihug.stub";
  static final String HOPE_WIRE = "com.apihug.wire";
  static final Set<String> YES =
      new HashSet<>() {
        {
          add("Y");
          add("YES");
        }
      };
  static String pkgArg = "pkgRename";
  final ObjectMapper objectMapper = new ObjectMapper();

  private static RenamePair getRenameSetting() {
    // -DpkgRename=com.abc-com.xyz
    String pkgName = System.getenv(pkgArg);
    if (pkgName == null) {
      pkgName = System.getProperty(pkgArg);
    }

    if (pkgName != null && !pkgName.isBlank()) {
      String[] got = pkgName.split("-");
      if (got != null && got.length == 2) {
        return new RenamePair(got[0], got[1]);
      }
    }
    return null;
  }

  private static Set<String> getSkipModules() {
    // -DpkgRename=com.abc-com.xyz
    String pkgName = System.getenv("skipModules");
    if (pkgName == null) {
      pkgName = System.getProperty("skipModules");
    }

    if (pkgName != null && !pkgName.isBlank()) {
      String[] got = pkgName.split(",");

      if (got != null) {
        return Arrays.stream(got).collect(Collectors.toSet());
      }
    }
    return Collections.emptySet();
  }

  @TaskAction
  public void doRename() {

    Project project = getProject();
    Logger logger = project.getLogger();
    // Get -DpkgName=com.another
    final RenamePair renameSetting = getRenameSetting();
    // 1. qualify
    // 2. not empty

    if (renameSetting == null) {
      logger.warn("please pass -DpkgRename=com.exist-com.latest  as new package rename pair");
      // Pick from th
      return;
    } else {
      // Java pkg qualify
      if (!Helper.isValidPackageName(renameSetting.oldPkg)
          || !Helper.isValidPackageName(renameSetting.newPkg)) {
        logger.error("-DpkgRename include no qualify java package name: " + renameSetting);
        return;
      }
    }

    if (project.getAllprojects() == null || project.getAllprojects().isEmpty()) {
      logger.warn("not modules under: " + project.getName());
      return;
    }

    final Set<String> skipModules = getSkipModules();

    for (final Project child : project.getAllprojects()) {
      child.getGroup();
    }

    List<Project> wireProjects = new ArrayList<>();
    List<Project> stubProjects = new ArrayList<>();

    for (final Project each : project.getAllprojects()) {
      if (each.getPlugins().hasPlugin(HOPE_WIRE)) {
        // Good we prepare to transform it
        wireProjects.add(each);
      } else {
        if (each.getPlugins().hasPlugin(HOPE_STUB)) {
          stubProjects.add(each);
        }
      }
    }

    // First the wire
    if (!wireProjects.isEmpty()) {
      for (final Project wireProject : wireProjects) {
        if (!skipModules.contains(wireProject.getName())) {
          try {
            processWireModule(project, wireProject, renameSetting.newPkg);
          } catch (Throwable throwable) {
            logger.error(
                "fail process wire module "
                    + wireProject.getName()
                    + " of path "
                    + wireProject.getPath(),
                throwable);
          }
        }
      }
    }
    // Then the stub
    if (!stubProjects.isEmpty()) {
      for (final Project stubProject : stubProjects) {
        if (!skipModules.contains(stubProject.getName())) {
          try {
            processStubModule(project, stubProject, renameSetting.newPkg, renameSetting.oldPkg);
          } catch (Throwable throwable) {
            logger.error(
                "fail process stub module "
                    + stubProject.getName()
                    + " of path "
                    + stubProject.getPath(),
                throwable);
          }
        }
      }
    }

    // We need to upgrade the {root/build.gradle} 's group
    if (userConfirm(
        "Do you want to upgrade build.gradle's group from : ["
            + renameSetting.oldPkg
            + "] to  ["
            + renameSetting.newPkg
            + "]")) {

      renameSpecificFile(
          project.getLogger(),
          Paths.get(project.getProjectDir().getAbsolutePath(), "build.gradle"),
          renameSetting.oldPkg,
          renameSetting.newPkg);
    }
  }

  private void processStubModule(
      Project parent, Project module, final String newPkgName, final String oldPkgName)
      throws IOException {

    if (userConfirm(module, newPkgName)) {
      final String src =
          Paths.get(module.getProjectDir().getAbsolutePath(), "src").toAbsolutePath().toString();
      final String main = Paths.get(src, "main").toAbsolutePath().toString();

      final Path javaDir = Paths.get(main, "java").toAbsolutePath();
      final Path stubDir = Paths.get(main, "stub").toAbsolutePath();
      final Path traitDir = Paths.get(main, "trait", "t").toAbsolutePath();

      final Path resourcesDir = Paths.get(main, "resources").toAbsolutePath();
      final Path testDir = Paths.get(src, "test", "java").toAbsolutePath();

      final String oldPkgPath = oldPkgName.replace('.', '/');
      final String newPkgPath = newPkgName.replace('.', '/');

      // 1. upgrade the stub's
      // 2. upgrade the application's
      // 3. upgrade the trait's
      renameJavas(parent.getLogger(), javaDir, oldPkgName, newPkgName, oldPkgPath, newPkgPath);
      renameJavas(parent.getLogger(), stubDir, oldPkgName, newPkgName, oldPkgPath, newPkgPath);

      // Has issue:
      // package t.com.example.....repository;
      renameJavas(parent.getLogger(), traitDir, oldPkgName, newPkgName, oldPkgPath, newPkgPath);
      renameJavas(parent.getLogger(), testDir, oldPkgName, newPkgName, oldPkgPath, newPkgPath);

      // Process it also
      updateResources(parent.getLogger(), resourcesDir, oldPkgName, newPkgName);
    }
  }

  private void processWireModule(Project parent, Project module, final String newPkgName)
      throws IOException {
    if (userConfirm(module, newPkgName)) {
      final String src =
          Paths.get(module.getProjectDir().getAbsolutePath(), "src").toAbsolutePath().toString();
      final Path protoDir = Paths.get(src, "main", "proto").toAbsolutePath();
      final Path resourcesDir = Paths.get(src, "main", "resources").toAbsolutePath();
      final Path wireDir = Paths.get(src, "main", "wire").toAbsolutePath();
      final Path wireTestDir = Paths.get(src, "wireTest").toAbsolutePath();

      // Get the config
      Path hopeWireJson = Paths.get(resourcesDir.toString(), "hope-wire.json").toAbsolutePath();
      File wireJsonFile = hopeWireJson.toFile();
      if (!wireJsonFile.exists()) {
        parent
            .getLogger()
            .error(
                "fail get the hope-wire.json of project "
                    + module.getName()
                    + " from path:"
                    + hopeWireJson);
        return;
      }

      JsonNode root = objectMapper.readTree(wireJsonFile);
      JsonNode packageNameNode = root.get("packageName");
      if (packageNameNode == null) {
        parent.getLogger().error("hope-wire.json has no packageName from " + hopeWireJson);
        return;
      }

      final Logger logger = parent.getLogger();

      String oldPkgName = packageNameNode.asText();
      if (Objects.equals(newPkgName, oldPkgName)) {
        parent.getLogger().error("hope-wire.json packageName already updated to " + newPkgName);
        return;
      }

      final String oldPkgPath = oldPkgName.replace('.', '/');
      final String newPkgPath = newPkgName.replace('.', '/');

      // 1. Update proto files
      if (protoDir.toFile().exists() && protoDir.toFile().isDirectory()) {
        updateProtoFiles(parent, protoDir, oldPkgName, newPkgName, oldPkgPath, newPkgPath);
      }
      // 2. Update generated files
      if (wireDir.toFile().exists() && wireDir.toFile().isDirectory()) {
        renameJavas(logger, wireDir, oldPkgName, newPkgName, oldPkgPath, newPkgPath);
      }

      // 3. Update wire test files
      if (wireTestDir.toFile().exists()) {
        renameJavas(logger, wireTestDir, oldPkgName, newPkgName, oldPkgPath, newPkgPath);
      }

      // 4. Update configuration files
      updateResources(logger, resourcesDir, oldPkgName, newPkgName);
    }
  }

  private void updateProtoFiles(
      Project parent,
      Path protoDir,
      String oldPkgName,
      String newPkgName,
      final String oldPkgPath,
      final String newPkgPath)
      throws IOException {

    Path oldPkgDir = protoDir.resolve(oldPkgPath);
    Path newPkgDir = protoDir.resolve(newPkgPath);

    if (!oldPkgDir.toFile().exists()) {
      parent.getLogger().warn("Old package directory not found: " + oldPkgDir);
      return;
    }

    // Create new package directory structure
    newPkgDir.toFile().mkdirs();

    // Process all proto files
    Files.walk(oldPkgDir)
        .filter(path -> path.toString().endsWith(".proto"))
        .forEach(
            protoFile -> {
              try {
                // Read proto file content
                String content = Files.readString(protoFile);

                // Update package declaration
                // Other full path how to handle it?
                content = content.replaceAll(Pattern.quote(oldPkgName), newPkgName);

                // This for import
                content = content.replaceAll(Pattern.quote(oldPkgPath), newPkgPath);

                // Calculate new file path
                Path relativePath = oldPkgDir.relativize(protoFile);
                Path newFilePath = newPkgDir.resolve(relativePath);

                // Create parent directories if needed
                newFilePath.getParent().toFile().mkdirs();

                // Write updated content
                Files.writeString(newFilePath, content);

                // Delete old file
                Files.delete(protoFile);
              } catch (IOException e) {
                parent.getLogger().error("Failed to process proto file: " + protoFile, e);
              }
            });

    // Clean up empty directories
    cleanupEmptyDirectories(oldPkgDir);
  }

  private void renameJavas(
      final Logger logger,
      final Path javaDir,
      final String oldPkgName,
      final String newPkgName,
      final String oldPkgPath,
      final String newPkgPath)
      throws IOException {

    final Path oldPkgDir = javaDir.resolve(oldPkgPath);
    final Path newPkgDir = javaDir.resolve(newPkgPath);

    if (!oldPkgDir.toFile().exists()) {
      logger.warn("Old package directory not found: " + oldPkgDir);
      return;
    }

    // Create new package directory structure
    newPkgDir.toFile().mkdirs();

    // Process all Java files
    Files.walk(oldPkgDir)
        .filter(path -> path.toString().endsWith(".java"))
        .forEach(
            javaFile -> {
              try {
                // Read Java file content
                String content = Files.readString(javaFile);

                // Update package declaration
                content = content.replaceAll(Pattern.quote(oldPkgName), newPkgName);

                // Calculate new file path
                Path relativePath = oldPkgDir.relativize(javaFile);
                Path newFilePath = newPkgDir.resolve(relativePath);

                // Create parent directories if needed
                newFilePath.getParent().toFile().mkdirs();

                // Write updated content
                Files.writeString(newFilePath, content);

                // Delete old file
                Files.delete(javaFile);
              } catch (IOException e) {
                logger.error("Failed to process Java file: " + javaFile, e);
              }
            });

    // Clean up empty directories
    cleanupEmptyDirectories(oldPkgDir);
  }

  private void updateResources(
      Logger logger, Path resourcesDir, String oldPkgName, String newPkgName) throws IOException {

    // Process all text files under resources directory
    Files.walk(resourcesDir)
        .filter(
            path -> {
              File f = path.toFile();
              if (f.isFile()) {
                // no old exit such like swagger/xxx.json
                if (f.toString().endsWith(".json")) {
                  return !path.toAbsolutePath().toString().contains("swagger/");
                }
                return true;
              }
              return false;
            })
        .forEach(
            file -> {
              try {
                // Read file content
                String content = Files.readString(file);
                // Replace package name in various formats
                String updatedContent = content.replaceAll(Pattern.quote(oldPkgName), newPkgName);
                // Write updated content back to file
                Files.writeString(file, updatedContent);

              } catch (IOException e) {
                logger.error("Failed to process config file: " + file, e);
              }
            });
  }

  private void renameSpecificFile(
      final Logger logger, final Path file, final String oldPkgName, final String newPkgName) {
    if (file.toFile().exists()) {
      try {
        // Read file content
        String content = Files.readString(file);
        // Replace package name in various formats
        String updatedContent = content.replaceAll(Pattern.quote(oldPkgName), newPkgName);
        // Write updated content back to file
        Files.writeString(file, updatedContent);

      } catch (IOException e) {
        logger.error("Failed to process config file: " + file, e);
      }
    }
  }

  private boolean userConfirm(final Project project, final String newPkg) {
    return userConfirm(
        "Do you want to continue rename package name of project ["
            + project.getName()
            + "("
            + project.getPath()
            + ")] to name:"
            + newPkg
            + "  (Y/yes)");
  }

  private boolean userConfirm(String tip) {
    Scanner scanner = new Scanner(System.in);
    System.out.println(tip);
    String userInput = scanner.nextLine().trim();
    boolean confirmed = YES.contains(userInput.toUpperCase());
    return confirmed;
  }

  record RenamePair(String oldPkg, String newPkg) {}
}
