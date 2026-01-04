package hope.tool.internal;

import hope.tool.internal.rename.PackageRenameTask;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class PackageRenamePackagePlugin implements Plugin<Project> {

  @Override
  public void apply(Project project) {
    final Action<Project> _action = self -> afterEvaluate(self);
    project.afterEvaluate(_action);
  }

  private void afterEvaluate(final Project project) {

    boolean isRoot = project.equals(project.getRootProject());

    if (isRoot) {
      PackageRenameTask task = project.getTasks().create("renamePkg", PackageRenameTask.class);
      task.setGroup("help");
    }
  }
}
