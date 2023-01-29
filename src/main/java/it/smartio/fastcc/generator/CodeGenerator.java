// Copyright 2011 Google Inc. All Rights Reserved.
// Author: sreeni@google.com (Sreeni Viswanadha)

package it.smartio.fastcc.generator;

import it.smartio.fastcc.parser.JavaCCParserConstants;

import it.smartio.fastcc.parser.Options;
import it.smartio.fastcc.parser.Token;
import it.smartio.fastcc.source.SourceWriter;
import it.smartio.fastcc.utils.Encoding;

class CodeGenerator {

  protected int crow, ccol;

  protected final void saveOutput(SourceWriter writer) {
    writer.saveOutput(Options.getOutputDirectory());
  }

  protected void genTokenSetup(Token t) {
    Token tt = t;

    while (tt.specialToken != null) {
      tt = tt.specialToken;
    }

    this.crow = tt.beginLine;
    this.ccol = tt.beginColumn;
  }

  protected final String getStringToPrint(Token t) {
    String retval = "";
    Token tt = t.specialToken;
    if (tt != null) {
      while (tt.specialToken != null) {
        tt = tt.specialToken;
      }
      while (tt != null) {
        retval += getStringForTokenOnly(tt);
        tt = tt.next;
      }
    }

    return retval + getStringForTokenOnly(t);
  }

  protected String getStringForTokenOnly(Token t) {
    String retval = "";
    for (; this.crow < t.beginLine; this.crow++) {
      retval += "\n";
      this.ccol = 1;
    }
    for (; this.ccol < t.beginColumn; this.ccol++) {
      retval += " ";
    }
    if ((t.kind == JavaCCParserConstants.STRING_LITERAL) || (t.kind == JavaCCParserConstants.CHARACTER_LITERAL)) {
      retval += Encoding.escapeUnicode(t.image);
    } else {
      retval += t.image;
    }
    this.crow = t.endLine;
    this.ccol = t.endColumn + 1;
    if (t.image.length() > 0) {
      char last = t.image.charAt(t.image.length() - 1);
      if ((last == '\n') || (last == '\r')) {
        this.crow++;
        this.ccol = 1;
      }
    }
    return retval;
  }
}
