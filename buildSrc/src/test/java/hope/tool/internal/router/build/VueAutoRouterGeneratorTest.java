package hope.tool.internal.router.build;

import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class VueAutoRouterGeneratorTest {

  static Logger logger = LoggerFactory.getLogger(VueAutoRouterGenerator.class);

  public static Stream<Arguments> provideTestCases() {
    return Stream.of(
        Arguments.of("/list/detail/[id]/[userId]", "/list/detail/:id/:userId"),
        Arguments.of("/list/detail/[id]_[userId]", "/list/detail/:id_:userId"),
        Arguments.of("/list/detail/[[id]]/[[userId]]", "/list/detail/:id?/:userId?"),
        Arguments.of(
            "/api/users/[id]/posts/[[page]]/[[size]]", "/api/users/:id/posts/:page?/:size?"),
        Arguments.of(null, null),
        Arguments.of("/list/detail", "/list/detail"));
  }

  @ParameterizedTest
  @MethodSource("provideTestCases")
  void shouldConvertPathPattern(String input, String expected) {
    // When
    String result = VueAutoRouterGenerator.convertPathPattern(input);

    // Then
    Assertions.assertThat(result).isEqualTo(expected);
  }
}
