/*
 * Copyright (c) 2001-2021 Territorium Online Srl / TOL GmbH. All Rights Reserved.
 *
 * This file contains Original Code and/or Modifications of Original Code as defined in and that are
 * subject to the Territorium Online License Version 1.0. You may not use this file except in
 * compliance with the License. Please obtain a copy of the License at http://www.tol.info/license/
 * and read it before using this file.
 *
 * The Original Code and all software distributed under the License are distributed on an 'AS IS'
 * basis, WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESS OR IMPLIED, AND TERRITORIUM ONLINE HEREBY
 * DISCLAIMS ALL SUCH WARRANTIES, INCLUDING WITHOUT LIMITATION, ANY WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, QUIET ENJOYMENT OR NON-INFRINGEMENT. Please see the License for
 * the specific language governing rights and limitations under the License.
 */

package it.smartio.fastcc.generator;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;

import it.smartio.fastcc.JJLanguage;
import it.smartio.fastcc.JavaCCRequest;
import it.smartio.fastcc.generator.cpp.CppLexerGenerator;
import it.smartio.fastcc.generator.cpp.CppOtherFilesGenerator;
import it.smartio.fastcc.generator.cpp.CppParserGenerator;
import it.smartio.fastcc.generator.cpp.CppTreeGenerator;
import it.smartio.fastcc.generator.java.JavaLexerGenerator;
import it.smartio.fastcc.generator.java.JavaOtherFilesGenerator;
import it.smartio.fastcc.generator.java.JavaParserGenerator;
import it.smartio.fastcc.generator.java.JavaTreeGenerator;
import it.smartio.fastcc.jjtree.ASTGrammar;

/**
 * The {@link ParserEngine} class.
 */
public class ParserEngine {

  private LexerGenerator      lexerGenerator;
  private ParserGenerator     parserGenerator;
  private JJTreeCodeGenerator treeGenerator;
  private OtherFilesGenerator otherFilesGenerator;

  /**
   * Constructs an instance of {@link ParserEngine}.
   */
  private ParserEngine() {}

  public final void generate(JavaCCRequest request) throws IOException, ParseException {
    LexerData data = new LexerBuilder().build(request);
    lexerGenerator.start(data);
    parserGenerator.start(request);
    otherFilesGenerator.start(data, request);
  }

  /**
   * Create a new instance of {@link ParserEngine}.
   *
   * @param node
   * @param writer
   */
  public void generateJJTree(ASTGrammar node, PrintWriter writer) throws IOException {
    node.jjtAccept(treeGenerator, writer);
    treeGenerator.generateJJTree();
  }

  /**
   * Create a new instance of {@link ParserEngine}.
   *
   * @param language
   */
  public static ParserEngine create(JJLanguage language) {
    ParserEngine engine = new ParserEngine();
    switch (language) {
      case Cpp:
        engine.lexerGenerator = new CppLexerGenerator();
        engine.parserGenerator = new CppParserGenerator();
        engine.treeGenerator = new CppTreeGenerator();
        engine.otherFilesGenerator = new CppOtherFilesGenerator();
        break;
      case Java:
        engine.lexerGenerator = new JavaLexerGenerator();
        engine.parserGenerator = new JavaParserGenerator();
        engine.treeGenerator = new JavaTreeGenerator();
        engine.otherFilesGenerator = new JavaOtherFilesGenerator();
        break;
      default:
        throw new RuntimeException("Language '" + language + "' type not supported!");
    }
    return engine;
  }
}
