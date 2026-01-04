package hope.tool.internal.router;

import com.fasterxml.jackson.databind.ObjectMapper;
import hope.tool.internal.router.build.VueAutoRouterGenerator;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;

public class RouterGeneratorTask extends DefaultTask {

  @TaskAction
  public void generate() throws IOException {
    Project project = getProject();

    File routerConfig =
        Paths.get(project.getProjectDir().getAbsolutePath(), "router.json").toFile();

    final boolean hasMeta = routerConfig.exists() && routerConfig.canRead();
    ObjectMapper objectMapper = new ObjectMapper();

    RouterConfiguration routerConfiguration =
        objectMapper.readValue(routerConfig, RouterConfiguration.class);

    List<RouterConfiguration.Item> items = routerConfiguration.getItems();
    if (items != null) {
      for (RouterConfiguration.Item it : items) {
        if (!it.isDisable()) {
          getLogger().warn("**start router discover for the module: " + it.getModuleDir());
          if (it.getModuleDir() == null || it.getModuleDir().isBlank()) {
            getLogger().warn("!!!module dir is empty no process will happen!!!");
            continue;
          }
          new VueAutoRouterGenerator(it, project).build();
        } else {
          getLogger().warn("!!router discover disable " + it.getModuleDir());
        }
      }
    }
  }
}
