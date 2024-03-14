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
import it.smartio.fastcc.jjtree.JJTreeOptions;

/**
 * The {@link ParserEngine} class.
 */
public class ParserEngine {

  private final LexerGenerator      lexerGenerator;
  private final ParserGenerator     parserGenerator;
  private final JJTreeCodeGenerator treeGenerator;
  private final OtherFilesGenerator otherFilesGenerator;

  /**
   * Constructs an instance of {@link ParserEngine}.
   */
  private ParserEngine(LexerGenerator lexerGenerator, ParserGenerator parserGenerator,
      JJTreeCodeGenerator treeGenerator, OtherFilesGenerator otherFilesGenerator) {
    this.lexerGenerator = lexerGenerator;
    this.parserGenerator = parserGenerator;
    this.treeGenerator = treeGenerator;
    this.otherFilesGenerator = otherFilesGenerator;
  }

  public final void generate(JavaCCRequest request) throws IOException, ParseException {
    LexerData data = new LexerBuilder().build(request);
    this.lexerGenerator.start(data);
    this.parserGenerator.start(request);
    this.otherFilesGenerator.start(data, request);
  }

  /**
   * Create a new instance of {@link ParserEngine}.
   *
   * @param node
   * @param writer
   */
  public void generateJJTree(ASTGrammar node, PrintWriter writer, JJTreeOptions options) throws IOException {
    node.jjtAccept(this.treeGenerator, writer);
    this.treeGenerator.generateJJTree(options);
  }

  /**
   * Create a new instance of {@link ParserEngine}.
   *
   * @param language
   */
  public static ParserEngine create(JJLanguage language) {
    switch (language) {
      case Cpp:
        return new ParserEngine(new CppLexerGenerator(), new CppParserGenerator(), new CppTreeGenerator(),
            new CppOtherFilesGenerator());

      case Java:
        return new ParserEngine(new JavaLexerGenerator(), new JavaParserGenerator(), new JavaTreeGenerator(),
            new JavaOtherFilesGenerator());

      default:
        throw new RuntimeException("Language '" + language + "' type not supported!");
    }
  }
}
