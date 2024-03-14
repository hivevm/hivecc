
package it.smartio.fastcc;


import java.io.File;

import it.smartio.fastcc.FastCCBuilder.Language;

/**
 * The {@link FastCCBuilderSelf} class.
 */
public class FastCCBuilderSelf {

  public static final File BASE = new File("src/main/").getAbsoluteFile();

  /**
   * {@link #main}.
   *
   * @param args
   */
  public static void main(String[] args) {
    FastCCBuilder builderJJ = FastCCBuilder.of(Language.Java);
    builderJJ.setOutputDirectory(FastCCBuilderSelf.BASE, "generated/org/javacc/parser");
    builderJJ.setJJFile(FastCCBuilderSelf.BASE, "resources/JavaCC.jj");
    builderJJ.build();

    FastCCBuilder builderJJT = FastCCBuilder.of(Language.Java);
    builderJJT.setOutputDirectory(FastCCBuilderSelf.BASE, "generated/org/javacc/jjtree");
    builderJJT.setJJTreeFile(FastCCBuilderSelf.BASE, "resources/JJTree.jjt");
    builderJJT.build();
  }
}
