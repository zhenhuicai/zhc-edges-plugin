package hope.tool.internal.rename;

import static org.junit.jupiter.api.Assertions.*;

import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

class PackageRenameTaskTest {

  String example1 =
      """
// @formatter:off
package t.com.zhm.wire.domain.system.repository;

import com.zhm.wire.domain.system.repository.AccountRoleEntityRepository;

/**
 * NEVER try to use this class directly, keep it as an interface(default, no public), all body of this interface will be merger to {@link AccountRoleEntityRepository} after {@code stub };\s
 *
 *
 * NEVER try to Overwrite parent  {@link AccountRoleEntityRepository }   or {@link org.springframework.data.repository.ListCrudRepository} 's default method!!
 *
 * @see AccountRoleEntityRepository
 *
 * @see com.zhm.wire.domain.system.AccountRoleEntity
 */
interface _AccountRoleEntityRepository extends AccountRoleEntityRepository {
  /**
   * Please put your customized SQL here,  any SQL other place will be dropped after merger!
   */
  interface _DerivedSQL {
  }
}
""";

  @Test
  void doRename_proto() {

    String oldPkgName = "com.zhm";
    String newPkgName = "com.xyz";


    String got  = example1.replaceAll(Pattern.quote(oldPkgName), newPkgName);

    System.out.println(got);
  }

  @Test
  void doRename_java() {}

  @Test
  void doRename_resources() {}
}
