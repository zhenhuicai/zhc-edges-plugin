package hope.tool.internal;

import hope.tool.internal.router.RouterGeneratorTask;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class AutoRouterPlugin implements Plugin<Project> {

  @Override
  public void apply(Project project) {
    // router.json
    final Action<Project> _action = self -> afterEvaluate(self);
    project.afterEvaluate(_action);
  }

  private void afterEvaluate(final Project project) {

    RouterGeneratorTask task = project.getTasks().create("gen-router", RouterGeneratorTask.class);
    task.setGroup("build");
  }
}
