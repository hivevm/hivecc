// Copyright 2011 Google Inc. All Rights Reserved.
// Author: sreeni@google.com (Sreeni Viswanadha)

/*
 * Copyright (c) 2006, Sun Microsystems, Inc. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer. * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. * Neither the name of the Sun Microsystems, Inc. nor
 * the names of its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package it.smartio.fastcc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.text.ParseException;

import it.smartio.fastcc.generator.ParserEngine;
import it.smartio.fastcc.parser.JavaCCData;
import it.smartio.fastcc.parser.JavaCCErrors;
import it.smartio.fastcc.parser.JavaCCParser;
import it.smartio.fastcc.parser.JavaCCParserDefault;
import it.smartio.fastcc.parser.MetaParseException;
import it.smartio.fastcc.parser.Options;
import it.smartio.fastcc.parser.StreamProvider;
import it.smartio.fastcc.semantic.SemanticContext;
import it.smartio.fastcc.semantic.Semanticize;

/**
 * Entry point.
 */
public abstract class JJParser {

  private JJParser() {}

  private static void printHelp() {
    System.out.println("Usage:");
    System.out.println("    javacc option-settings inputfile");
    System.out.println("");
    System.out.println("\"option-settings\" is a sequence of settings separated by spaces.");
    System.out.println("Each option setting must be of one of the following forms:");
    System.out.println("");
    System.out.println("    -optionname=value (e.g., -STATIC=false)");
    System.out.println("    -optionname:value (e.g., -STATIC:false)");
    System.out.println("    -optionname       (equivalent to -optionname=true.  e.g., -STATIC)");
    System.out.println("    -NOoptionname     (equivalent to -optionname=false. e.g., -NOSTATIC)");
    System.out.println("");
    System.out.println("Option settings are not case-sensitive, so one can say \"-nOsTaTiC\" instead");
    System.out.println("of \"-NOSTATIC\".  Option values must be appropriate for the corresponding");
    System.out.println("option, and must be either an integer, a boolean, or a string value.");
    System.out.println("");

    System.out.println("EXAMPLE:");
    System.out.println("    javacc -STATIC=false -LOOKAHEAD:2 -debug_parser mygrammar.jj");
    System.out.println("");
  }

  /**
   * A main program that exercises the parser.
   */
  public static void main(String args[]) throws Exception {
    if ((args.length == 1) && args[args.length - 1].equalsIgnoreCase("-version")) {
      System.out.println(FastCC.VERSION.toString());
      System.exit(0);
    }

    JJMain.bannerLine("Parser Generator", "");

    JavaCCErrors.reInit();
    Options options = new Options();

    JavaCCParser parser = null;
    if (args.length == 0) {
      System.out.println("");
      JJParser.printHelp();
      System.exit(1);
    } else {
      System.out.println("(type \"javacc\" with no arguments for help)");
    }

    if (options.isOption(args[args.length - 1])) {
      System.out.println("Last argument \"" + args[args.length - 1] + "\" is not a filename.");
      System.exit(1);
    }
    for (int arg = 0; arg < (args.length - 1); arg++) {
      if (!options.isOption(args[arg])) {
        System.out.println("Argument \"" + args[arg] + "\" must be an option setting.");
        System.exit(1);
      }
      options.setCmdLineOption(args[arg]);
    }

    try {
      File fp = new File(args[args.length - 1]);
      if (!fp.exists()) {
        System.out.println("File " + args[args.length - 1] + " not found.");
        System.exit(1);
      }
      if (fp.isDirectory()) {
        System.out.println(args[args.length - 1] + " is a directory. Please use a valid file name.");
        System.exit(1);
      }
      parser = new JavaCCParserDefault(new StreamProvider(
          new InputStreamReader(new FileInputStream(args[args.length - 1]), options.getGrammarEncoding())), options);
    } catch (SecurityException se) {
      System.out.println("Security violation while trying to open " + args[args.length - 1]);
      System.exit(1);
    } catch (FileNotFoundException e) {
      System.out.println("File " + args[args.length - 1] + " not found.");
      System.exit(1);
    }

    try {
      String jjFile = args[args.length - 1];
      System.out.println("Reading from file " + jjFile + " . . .");
      boolean jjtreeGenerated = JJMain.isGeneratedBy("JJTree", jjFile);
      JavaCCData request = new JavaCCData(jjtreeGenerated, options);

      parser.initialize(request);
      parser.javacc_input();

      // Initialize the parser data
      ParserEngine engine = ParserEngine.create(options.getOutputLanguage());

      JJMain.createOutputDir(options.getOutputDirectory());

      Semanticize.semanticize(request, new SemanticContext(options));

      options.setStringOption(FastCC.PARSER_NAME, request.getParserName());

      engine.generate(request);


      if (!JavaCCErrors.hasError()) {
        if (!JavaCCErrors.hasWarning()) {
          System.out.println("Parser generated successfully.");
        } else {
          System.out.println("Parser generated with 0 errors and " + JavaCCErrors.get_warning_count() + " warnings.");
        }
      } else {
        System.out.println("Detected " + JavaCCErrors.get_error_count() + " errors and "
            + JavaCCErrors.get_warning_count() + " warnings.");
        System.exit(JavaCCErrors.get_error_count() == 0 ? 0 : 1);
      }
    } catch (MetaParseException e) {
      System.out.println("Detected " + JavaCCErrors.get_error_count() + " errors and "
          + JavaCCErrors.get_warning_count() + " warnings.");
      System.exit(1);
    } catch (ParseException e) {
      System.out.println(e.toString());
      System.out.println("Detected " + (JavaCCErrors.get_error_count() + 1) + " errors and "
          + JavaCCErrors.get_warning_count() + " warnings.");
      System.exit(1);
    }
  }
}
