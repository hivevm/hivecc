// Copyright 2012 Google Inc. All Rights Reserved.
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

package it.smartio.fastcc.generator.cpp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import it.smartio.fastcc.FastCC;
import it.smartio.fastcc.JavaCCRequest;
import it.smartio.fastcc.generator.AbstractFileGenerator;
import it.smartio.fastcc.generator.FileGenerator;
import it.smartio.fastcc.generator.LexerData;
import it.smartio.fastcc.parser.JavaCCErrors;
import it.smartio.fastcc.parser.ParseException;
import it.smartio.fastcc.parser.RStringLiteral;
import it.smartio.fastcc.parser.RegExprSpec;
import it.smartio.fastcc.parser.RegularExpression;
import it.smartio.fastcc.parser.TokenProduction;
import it.smartio.fastcc.utils.DigestOptions;
import it.smartio.fastcc.utils.DigestWriter;

/**
 * Generates the Constants file.
 */
public class CppFileGenerator extends AbstractFileGenerator implements FileGenerator {

  /**
   * Gets the template by name.
   * 
   * @param name
   */
  protected final String getTemplate(String name) {
    return String.format("/templates/cpp/%s.template", name);
  }

  /**
   * Creates a new {@link DigestWriter}.
   * 
   * @param file
   * @param options
   */
  protected final DigestWriter createDigestWriter(File file, DigestOptions options) throws FileNotFoundException {
    return DigestWriter.createCpp(file, FastCC.VERSION, options);
  }

  @Override
  public final void handleRequest(JavaCCRequest request, LexerData context) throws ParseException {
    if (JavaCCErrors.hasError()) {
      throw new ParseException();
    }

    handleToken(request, context);
    handleProvider(request, context);
    handleException(request, context);
    handleGeneric(request, context);

    handleParserConstants(request, context);
  }

  protected void handleToken(JavaCCRequest request, LexerData context) throws ParseException {
    generateFile("Token.h", context);
    generateFile("Token.cc", context);
    generateFile("TokenManager.h", context);
    generateFile("TokenManagerError.h", context);
    generateFile("TokenManagerError.cc", context);
    generateFile("TokenManagerErrorHandler.h", context);
    generateFile("TokenManagerErrorHandler.cc", context);
  }

  protected final void handleProvider(JavaCCRequest request, LexerData context) throws ParseException {
    generateFile("Reader.h", context);
    generateFile("StringReader.h", context);
    generateFile("StringReader.cc", context);
  }

  protected final void handleException(JavaCCRequest request, LexerData context) throws ParseException {
    generateFile("ParseException.h", context);
    generateFile("ParseException.cc", context);
    generateFile("ParserErrorHandler.h", context);
    generateFile("ParserErrorHandler.cc", context);
  }

  protected final void handleGeneric(JavaCCRequest request, LexerData context) throws ParseException {
    generateFile("JavaCC.h", context);
  }

  protected final void handleParserConstants(JavaCCRequest request, LexerData context) throws ParseException {
    List<RegularExpression> expressions = new ArrayList<>();
    for (TokenProduction tp : request.getTokenProductions()) {
      for (RegExprSpec res : tp.getRespecs()) {
        expressions.add(res.rexp);
      }
    }

    DigestOptions options = new DigestOptions(context.options());
    options.addValues("TOKENS", request.getOrderedsTokens()).add(r -> "" + r.getOrdinal()).add(r -> r.getLabel());
    options.addValues("STATES", context.getStateCount()).add(i -> String.valueOf(i)).add(i -> context.getStateName(i));
    options.addValues("REGEXPS", expressions.size() + 1).add(i -> String.valueOf(i))
        .add(i -> getRegExp(false, i, expressions)).add(i -> getRegExp(true, i, expressions));

    generateFile("ParserConstants.h", request.getParserName() + "Constants.h", options);
  }

  private static String getRegExp(boolean isImage, int i, List<RegularExpression> expressions) {
    StringWriter builder = new StringWriter();
    try (PrintWriter writer = new PrintWriter(builder)) {
      writer.print("" + i + "[] = ");
      if (i == 0) {
        CppFileGenerator.printCharArray(writer, "<EOF>");
      } else if (expressions.get(i - 1) instanceof RStringLiteral) {
        RStringLiteral literal = (RStringLiteral) expressions.get(i - 1);
        if (isImage) {
          CppFileGenerator.printCharArray(writer, literal.getImage());
        } else {
          CppFileGenerator.printCharArray(writer, "<" + literal.getLabel() + ">");
        }
      } else if (!expressions.get(i - 1).getLabel().equals("")) {
        CppFileGenerator.printCharArray(writer, "<" + expressions.get(i - 1).getLabel() + ">");
      } else {
        if (expressions.get(i - 1).getTpContext().getKind() == TokenProduction.Kind.TOKEN) {
          JavaCCErrors.warning(expressions.get(i - 1),
              "Consider giving this non-string token a label for better error reporting.");
        }
        CppFileGenerator.printCharArray(writer, "<token of kind " + expressions.get(i - 1).getOrdinal() + ">");
      }
    }
    return builder.toString();
  }

  // Used by the CPP code generatror
  protected static void printCharArray(PrintWriter writer, String s) {
    writer.print("{");
    for (int i = 0; i < s.length(); i++) {
      writer.print("0x" + Integer.toHexString(s.charAt(i)) + ", ");
    }
    writer.print("0}");
  }
}
