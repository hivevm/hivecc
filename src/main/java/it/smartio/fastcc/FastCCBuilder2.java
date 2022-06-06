
package it.smartio.fastcc;


import java.io.File;

import it.smartio.fastcc.FastCCBuilder.Language;

/**
 * The {@link FastCCBuilder2} class.
 */
public class FastCCBuilder2 {

  public static final File BASE = new File("src/main/").getAbsoluteFile();

  /**
   * {@link #main}.
   *
   * @param args
   */
  public static void main(String[] args) {
    FastCCBuilder builderJJ = FastCCBuilder.of(Language.Java);
    builderJJ.setOutputDirectory(FastCCBuilder2.BASE, "generated/org/javacc/parser");
    builderJJ.setJJFile(FastCCBuilder2.BASE, "resources/JavaCC.jj");
    builderJJ.build();

    FastCCBuilder builderJJT = FastCCBuilder.of(Language.Java);
    builderJJT.setOutputDirectory(FastCCBuilder2.BASE, "generated/org/javacc/jjtree");
    builderJJT.setJJTreeFile(FastCCBuilder2.BASE, "resources/JJTree.jjt");
    builderJJT.build();
  }
}
