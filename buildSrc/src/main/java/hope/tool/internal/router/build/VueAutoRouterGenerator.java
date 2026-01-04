package hope.tool.internal.router.build;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hope.tool.internal.router.RouterConfiguration;
import hope.tool.internal.router.build.view.MenuView;
import hope.tool.internal.router.build.view.RouterView;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.gradle.api.Project;
import org.slf4j.Logger;

public class VueAutoRouterGenerator {

  public static final ObjectMapper objectMapper = new ObjectMapper();

  private static final Pattern OPTIONS_REGEX =
      Pattern.compile("defineOptions\\s*\\(\\s*(\\{[\\s\\S]*?\\})\\s*\\)");
  protected final RouterConfiguration.Item routerItemConfiguration;
  final Logger logger;
  final Path pagesRootPath;
  final File pagesRootDir;
  final Project project;
  final Path moduleDir;
  private final List<RouterView> routerViews = new ArrayList<>();
  private final Set<String> rawPaths = new LinkedHashSet<>();
  public List<RouterView> generatedRouterViews;
  public List<MenuView> generatedMenuViews;
  public Set<String> generatedRawPaths;
  public Map<String, Object> generatedPageTitles;

  public VueAutoRouterGenerator(
      RouterConfiguration.Item routerItemConfiguration, final Project project) {
    this.routerItemConfiguration = routerItemConfiguration;
    logger = project.getLogger();
    this.project = project;

    moduleDir =
        Paths.get(
            project.getProjectDir().getAbsolutePath(),
            routerItemConfiguration.getModuleDir() == null
                ? "."
                : routerItemConfiguration.getModuleDir());

    pagesRootPath =
        Paths.get(moduleDir.toAbsolutePath().toString(), routerItemConfiguration.getPagesDir())
            .toAbsolutePath();

    pagesRootDir = pagesRootPath.toFile();

    objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
    objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
    objectMapper.configure(JsonParser.Feature.ALLOW_TRAILING_COMMA, true);
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    //
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    objectMapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, false);
  }

  public static String getRelativePath(Path rootPath, Path filePath) {
    // Normalize both paths to handle ".." and "." segments
    Path normalizedRoot = rootPath.normalize();
    Path normalizedFile = filePath.normalize();

    // Calculate the relative path
    Path relativePath = normalizedRoot.relativize(normalizedFile);

    // Convert path separators to forward slashes for consistency across OS
    return relativePath.toString().replace(File.separatorChar, '/');
  }

  /**
   * Converts a path pattern with square brackets to Express-style parameters Examples: <code>
   *    /list/detail/[id]/[userId] -> /list/detail/:id/:userId
   *    /list/detail/[[id]]/[[userId]] ->/list/detail/:id?/:userId?
   * </code>
   *
   * @param pattern The input path pattern with square brackets
   * @return The converted path pattern with Express-style parameters
   */
  public static String convertPathPattern(String pattern) {
    if (pattern == null || pattern.isEmpty()) {
      return pattern;
    }

    // First replace optional parameters [[param]] with :param?
    String result = pattern.replaceAll("\\[\\[([^\\[\\]]+)\\]\\]", ":$1?");

    // Then replace required parameters [param] with :param
    result = result.replaceAll("\\[([^\\[\\]]+)\\]", ":$1");

    return result;
  }

  public void build() {
    if (pagesRootDir.exists() && pagesRootDir.isDirectory()) {
      // Walk through this dir recursively pick all the .vue files
      try {
        List<Path> allFiles =
            Files.walk(pagesRootPath)
                .filter(path -> path.toString().endsWith(".vue"))
                .collect(Collectors.toList());
        // Should group them by the hierarchy
        Map<Path, Set<Path>> dirHierarchy = new HashMap<>();
        for (final Path path : allFiles) {
          final Path parent = path.getParent();
          dirHierarchy.computeIfAbsent(parent, x -> new LinkedHashSet()).add(path);
        }
        for (final Path file : allFiles) {
          processVue(file);
        }
        if (routerViews != null && !routerViews.isEmpty()) {
          final RouterTreeBuilder treeBuilder = new RouterTreeBuilder();
          List<RouterView> views = treeBuilder.process(routerViews);
          // Pick backend Router views
          // Patch for the i18key
          Map<String, Object> pageTitles = ViewProcessMapperHelper.patch18Key(views);

          generatedMenuViews = ViewProcessMapperHelper.toMenus(views);
          generatedRouterViews = ViewProcessMapperHelper.shrink4Vue(views);
          generatedRawPaths = rawPaths;
          generatedPageTitles = pageTitles;

          doWrite();
        }

      } catch (Throwable e) {
        logger.error("fail build page ", e);
      }
    } else {
      logger.warn(
          "no pages dir "
              + pagesRootDir.getAbsolutePath()
              + " is exists "
              + pagesRootDir.exists()
              + "  is dir "
              + pagesRootDir.isDirectory());
    }
  }

  private boolean notEmpty(Collection given) {
    return given != null && !given.isEmpty();
  }

  private void doWrite() throws IOException {
    // Push to the write dir
    if (notEmpty(generatedRouterViews)) {
      // TODO parent not be ignored

      String merger =
          """
                          // @Generated

                          import type { RouteRecordRaw } from 'vue-router'
                          """
              + "import type { "
              + routerItemConfiguration.getMenuItem()
              + " } from '"
              + routerItemConfiguration.getMenuItemImport()
              + "'\n"
              + "\n\nexport const pages: RouteRecordRaw[] ="
              + VueAutoRouterGenerator.objectMapper
                  .writerWithDefaultPrettyPrinter()
                  .writeValueAsString(generatedRouterViews)
                  .replace("\"__", "")
                  .replace("__\"", "")
              + "\n\n export type PageKey ="
              + generatedRawPaths.stream()
                  .map(it -> "'" + it + "'")
                  .collect(Collectors.joining("\n  |"));

      if (notEmpty(generatedMenuViews)) {
        // TODO write back to the app?

        final ObjectMapper _objectMapper = new ObjectMapper();
        _objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        _objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        if (Utils.isNotBlank(routerItemConfiguration.getBackendModuleDir())) {
          logger.debug(
              "write back menus to backend module "
                  + routerItemConfiguration.getBackendModuleDir());

          final String menus =
              _objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(generatedMenuViews);

          Paths.get(
              project.getRootDir().getAbsolutePath(),
              routerItemConfiguration.getBackendModuleDir(),
              "src",
              "main",
              "resources",
              "frontend");

          Writer.writeToPath(
              Paths.get(
                  project.getRootDir().getAbsolutePath(),
                  routerItemConfiguration.getBackendModuleDir(),
                  "src",
                  "main",
                  "resources",
                  "frontend"),
              null,
              "router.json",
              menus);
        }

        merger =
            merger
                + "\n\nexport const menus: "
                + routerItemConfiguration.getMenuItem()
                + "[] = "
                + VueAutoRouterGenerator.objectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(generatedMenuViews)
                + "\n";

        Writer.writeToPath(
            Paths.get(moduleDir.toString(), routerItemConfiguration.getRouterOutDir()),
            null,
            "index.ts",
            merger);

        // I18KEY
        if (generatedPageTitles != null && !generatedPageTitles.isEmpty()) {
          // We need to distinguish
          I18NWriter.mergerPageTitleLanguage(generatedPageTitles, this, _objectMapper, logger);
        }
      }
    }
  }

  public Path getModuleDir() {
    return moduleDir;
  }

  public RouterConfiguration.Item getRouterItemConfiguration() {
    return routerItemConfiguration;
  }

  private void processVue(final Path vueFile) {
    try {
      String content = Files.readString(vueFile, StandardCharsets.UTF_8);
      Matcher optionsMatch = OPTIONS_REGEX.matcher(content);

      if (optionsMatch.find()) {
        String optionsStr = optionsMatch.group(1);
        JsonNode options = objectMapper.readTree(optionsStr);
        String relativePath = getRelativePath(pagesRootPath, vueFile);
        if (options.has("meta")) {
          JsonNode meta = options.get("meta");
          RouterView routerView = new RouterView();
          if (relativePath.endsWith("index.vue")) {
            routerView.setPath("/" + relativePath.substring(0, relativePath.length() - 10));
          } else {
            routerView.setPath("/" + relativePath.replace(".vue", ""));
          }

          if (meta.has("title")) {
            routerView.setTitle(meta.get("title").asText());
          }
          if (meta.has("icon")) {
            routerView.setIcon(meta.get("icon").asText());
          }
          if (meta.has("hidden")) {
            routerView.setHidden(meta.get("hidden").asBoolean());
          }

          if (meta.has("layout")) {
            routerView.setLayout(meta.get("layout").asText());
          }
          if (meta.has("i18key")) {
            // Enhance for customize the title i18key
            routerView.setI18key(meta.get("i18key").asText());
          }

          if (meta.has("order")) {
            routerView.setDisplayOrder(meta.get("order").asInt());
          }

          if (meta.has("path")) {
            String _p = meta.get("path").asText();
            if (_p != null && !_p.isBlank()) {
              routerView.setPath(_p);
            }
          }
          // Choose appropriate parameter types: use [param] for required parameters and [[param]]
          // for optional ones
          // Parameter names should be descriptive; avoid overly simple or ambiguous names
          // For complex parameter combinations, use multi-parameter syntax like
          // detail_[id]_[userId] to improve readability
          // (Optional) Parameter routing:
          //  /list/detail/[id]/[userId] ->  /list/detail/:id/:userId
          // /list/detail/[[id]]/[[userId]] ->  /list/detail/:id?/:userId?
          if (routerView.getPath() != null) {
            routerView.setPath(convertPathPattern(routerView.getPath()));
          }

          rawPaths.add(routerView.getPath());

          routerView.setComponent(
              "__() => import('"
                  + routerItemConfiguration.getPagesPath()
                  + "/"
                  + relativePath
                  + "')__");
          if (options.has("name")) {
            routerView.setName(options.get("name").asText());
          }
          routerView.setFilePath(vueFile);
          routerViews.add(routerView);
        }
      }
    } catch (Throwable error) {
      logger.warn("Fail parse file {}", vueFile.toAbsolutePath(), error);
    }
  }
}
