// Copyright 2024 HiveVM.ORG. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.cc.doc;

import org.hivevm.cc.parser.Expansion;
import org.hivevm.cc.parser.NonTerminal;
import org.hivevm.cc.parser.NormalProduction;
import org.hivevm.cc.parser.RegExprSpec;
import org.hivevm.cc.parser.RegularExpression;
import org.hivevm.cc.parser.TokenProduction;

/**
 * Output BNF in HTML 3.2 format.
 */
class XTextGenerator extends TextGenerator {

  public XTextGenerator(JJDocOptions opts) {
    super(opts);
  }

  @Override
  public void handleTokenProduction(TokenProduction tp) {

    StringBuilder sb = new StringBuilder();

    for (RegExprSpec res : tp.getRespecs()) {
      String regularExpressionText = JJDoc.emitRE(res.rexp);
      sb.append(regularExpressionText);

      if (res.nsTok != null) {
        sb.append(" : " + res.nsTok.image);
      }

      sb.append("\n");
      // if (it2.hasNext()) {
      // sb.append("| ");
      // }
    }

    // text(sb.toString());
  }

  private void println(String s) {
    print(s + "\n");
  }

  @Override
  public void text(String s) {
    print(s);
  }

  @Override
  public void print(String s) {
    this.ostr.print(s);
  }


  @Override
  public void documentStart() {
    this.ostr = create_output_stream();
    println("grammar " + JJDocGlobals.input_file + " with org.eclipse.xtext.common.Terminals");
    println("import \"http://www.eclipse.org/emf/2002/Ecore\" as ecore");
    println("");
  }

  @Override
  public void documentEnd() {
    this.ostr.close();
  }

  /**
   * Prints out comments, used for tokens and non-terminals. {@inheritDoc}
   *
   * @see org.hivevm.cc.doc.TextGenerator#specialTokens(java.lang.String)
   */
  @Override
  public void specialTokens(String s) {
    print(s);
  }


  @Override
  public void nonterminalsStart() {}

  @Override
  public void nonterminalsEnd() {}

  @Override
  public void tokensStart() {}

  @Override
  public void tokensEnd() {}

  @Override
  public void productionStart(NormalProduction np) {}

  @Override
  public void productionEnd(NormalProduction np) {}

  @Override
  public void expansionStart(Expansion e, boolean first) {}

  @Override
  public void expansionEnd(Expansion e, boolean first) {
    println(";");
  }

  @Override
  public void nonTerminalStart(NonTerminal nt) {
    print("terminal ");
  }

  @Override
  public void nonTerminalEnd(NonTerminal nt) {
    print(";");
  }

  @Override
  public void reStart(RegularExpression r) {}

  @Override
  public void reEnd(RegularExpression r) {}
}
