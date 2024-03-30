
package it.smartio.fastcc;

import org.junit.jupiter.api.Test;

import java.io.File;

import it.smartio.fastcc.FastCCBuilder.Language;


class CCParserTest {

  public static final File ROOT          = new File("/data/smartIO/fastcc");
  public static final File PARSER_SOURCE = new File(CCParserTest.ROOT, "src/main/resources");
  public static final File PARSER_TARGET = new File(CCParserTest.ROOT, "src/main/gen/it/smartio/fastcc");

  @Test
  void testJJParser() {
    FastCCBuilder builder = FastCCBuilder.of(Language.Java);
    builder.setOutputDirectory(new File(CCParserTest.PARSER_TARGET, "parser"));
    builder.setJJFile(CCParserTest.PARSER_SOURCE, "JavaCC.jj");
    builder.build();
  }

  @Test
  void testJJTree() {
    FastCCBuilder builder = FastCCBuilder.of(Language.Java);
    builder.setOutputDirectory(new File(CCParserTest.PARSER_TARGET, "jjtree"));
    builder.setJJTreeFile(CCParserTest.PARSER_SOURCE, "JJTree.jjt");
    builder.build();
  }
}
