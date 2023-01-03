
package it.smartio.fastcc;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import it.smartio.fastcc.generator.java.JavaCCToken;
import it.smartio.fastcc.parser.JavaCCErrors;
import it.smartio.fastcc.parser.Options;

public class FastCCInterpreter {

  public static void main(String[] args) throws Exception {
    JavaCCToken.reset();
    JavaCCErrors.reInit();
    Options.init();

    for (int arg = 0; arg < (args.length - 2); arg++) {
      if (!Options.isOption(args[arg])) {
        System.out.println("Argument \"" + args[arg] + "\" must be an option setting.");
        System.exit(1);
      }
      Options.setCmdLineOption(args[arg]);
    }

    try {
      File fp = new File(args[args.length - 2]);
      byte[] buf = new byte[(int) fp.length()];
      try (DataInputStream stream = new DataInputStream(new BufferedInputStream(new FileInputStream(fp)))) {
        stream.readFully(buf);
      }
      new String(buf);
      File inputFile = new File(args[args.length - 1]);
      buf = new byte[(int) inputFile.length()];
      try (DataInputStream stream = new DataInputStream(new BufferedInputStream(new FileInputStream(inputFile)))) {
        stream.readFully(buf);
      }
      new String(buf);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      System.exit(1);
    } catch (Throwable t) {
      System.exit(1);
    }
    long l = System.currentTimeMillis();
    System.err.println("Tokenized in: " + (System.currentTimeMillis() - l));
  }
}
