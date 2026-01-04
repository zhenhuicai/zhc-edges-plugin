package hope.tool.internal.router.build;

public class Utils {

  public static final String HOPE_NO_MERGER = "// hope-no-merger";

  public static boolean isNotBlank(final String given) {
    return given != null && !given.isBlank();
  }

  /**
   * <code>
   *     /list/detail/:id/:userId
   *     /list/detail/:id?/:userId?
   *     /list/detail/:id_:userId
   *     /api/users/:id/posts/:page?/:size?
   * </code>
   *
   * @param given
   * @return
   */
  public static String specificPathCharacterReplace(final String given) {
    if (given == null || given.isBlank()) {
      return given;
    }
    return given.replace(":", "").replace("?", "");
  }
}
