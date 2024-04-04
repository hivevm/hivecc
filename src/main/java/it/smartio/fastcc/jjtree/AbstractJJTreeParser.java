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

package it.smartio.fastcc.jjtree;

/**
 * The {@link AbstractJJTreeParser} class.
 */
abstract class AbstractJJTreeParser {

  protected final void setParserName(int index) {
    JJTreeGlobals.parserName = getToken(index).image;
  }

  protected final void addProduction(ASTProduction prod) {
    JJTreeGlobals.productions.put(prod.name, prod);
  }

  protected final void normalize() {
    getOptions().normalize();
  }

  protected final String setInputOption(Token o, Token v) {
    String image = v.image;
    switch (v.kind) {
      case JJTreeParserConstants.INTEGER_LITERAL:
        getOptions().setInputOption(o, v, o.image, Integer.valueOf(image));
        break;

      case JJTreeParserConstants.TRUE:
      case JJTreeParserConstants.FALSE:
        getOptions().setInputOption(o, v, o.image, Boolean.valueOf(image));
        break;

      default:
        image = TokenUtils.remove_escapes_and_quotes(v, image);
        getOptions().setInputOption(o, v, o.image, image);
        break;
    }
    return image;
  }

  protected abstract Token getNextToken();

  protected abstract Token getToken(int index);

  protected void jjtreeOpenNodeScope(Node n) {}

  protected void jjtreeCloseNodeScope(Node n) {}

  protected JJTreeOptions getOptions() {
    throw new UnsupportedOperationException();
  }

  /**
   * Returns true if the next token is not in the FOLLOW list of "expansion". It is used to decide
   * when the end of an "expansion" has been reached.
   */
  protected boolean notTailOfExpansionUnit() {
    Token t;
    t = getToken(1);
    if ((t.kind == JJTreeParserConstants.BIT_OR) || (t.kind == JJTreeParserConstants.COMMA)
        || (t.kind == JJTreeParserConstants.RPAREN) || (t.kind == JJTreeParserConstants.RBRACE)
        || (t.kind == JJTreeParserConstants.RBRACKET)) {
      return false;
    }
    return true;
  }

  protected boolean checkEmptyLA(boolean emptyLA, Token token) {
    return !emptyLA && (token.kind != JJTreeParserConstants.RPAREN);
  }

  protected boolean checkEmptyLAAndCommandEnd(boolean emptyLA, boolean commaAtEnd, Token token) {
    return !emptyLA && !commaAtEnd && (getToken(1).kind != JJTreeParserConstants.RPAREN);
  }

  protected boolean checkEmptyLAOrCommandEnd(boolean emptyLA, boolean commaAtEnd) {
    return emptyLA || commaAtEnd;
  }

  protected boolean checkEmpty(Token token) {
    return token.kind != JJTreeParserConstants.RPAREN && token.kind != JJTreeParserConstants.LBRACE;
  }
}
