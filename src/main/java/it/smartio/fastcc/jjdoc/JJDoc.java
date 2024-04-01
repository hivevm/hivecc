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

package it.smartio.fastcc.jjdoc;

import java.util.Iterator;

import it.smartio.fastcc.parser.Action;
import it.smartio.fastcc.parser.BNFProduction;
import it.smartio.fastcc.parser.CharacterRange;
import it.smartio.fastcc.parser.Choice;
import it.smartio.fastcc.parser.Expansion;
import it.smartio.fastcc.parser.JavaCCData;
import it.smartio.fastcc.parser.Lookahead;
import it.smartio.fastcc.parser.NonTerminal;
import it.smartio.fastcc.parser.NormalProduction;
import it.smartio.fastcc.parser.OneOrMore;
import it.smartio.fastcc.parser.RCharacterList;
import it.smartio.fastcc.parser.RChoice;
import it.smartio.fastcc.parser.REndOfFile;
import it.smartio.fastcc.parser.RJustName;
import it.smartio.fastcc.parser.ROneOrMore;
import it.smartio.fastcc.parser.RRepetitionRange;
import it.smartio.fastcc.parser.RSequence;
import it.smartio.fastcc.parser.RStringLiteral;
import it.smartio.fastcc.parser.RZeroOrMore;
import it.smartio.fastcc.parser.RZeroOrOne;
import it.smartio.fastcc.parser.RegExprSpec;
import it.smartio.fastcc.parser.RegularExpression;
import it.smartio.fastcc.parser.Sequence;
import it.smartio.fastcc.parser.SingleCharacter;
import it.smartio.fastcc.parser.Token;
import it.smartio.fastcc.parser.TokenProduction;
import it.smartio.fastcc.parser.ZeroOrMore;
import it.smartio.fastcc.parser.ZeroOrOne;
import it.smartio.fastcc.utils.Encoding;

/**
 * The main entry point for JJDoc.
 */
class JJDoc extends JJDocGlobals {

  static void start(JavaCCData javacc) {
    JJDocGlobals.generator = JJDocGlobals.getGenerator((JJDocOptions) javacc.options());
    JJDocGlobals.generator.documentStart();
    JJDoc.emitTokenProductions(JJDocGlobals.generator, javacc.getTokenProductions());
    JJDoc.emitNormalProductions(JJDocGlobals.generator, javacc.getNormalProductions());
    JJDocGlobals.generator.documentEnd();
  }

  private static Token getPrecedingSpecialToken(Token tok) {
    Token t = tok;
    while (t.specialToken != null) {
      t = t.specialToken;
    }
    return (t != tok) ? t : null;
  }

  private static void emitTopLevelSpecialTokens(Token tok, Generator gen) {
    if (tok == null) {
      // Strange ...
      return;
    }
    tok = JJDoc.getPrecedingSpecialToken(tok);
  }

  /*
   * private static boolean toplevelExpansion(Expansion exp) { return exp.parent != null && (
   * (exp.parent instanceof NormalProduction) || (exp.parent instanceof TokenProduction) ); }
   */

  private static void emitTokenProductions(Generator gen, Iterable<TokenProduction> prods) {
    gen.tokensStart();
    // FIXME there are many empty productions here
    for (TokenProduction tp : prods) {
      JJDoc.emitTopLevelSpecialTokens(tp.getFirstToken(), gen);


      gen.handleTokenProduction(tp);

      // if (!token.equals("")) {
      // gen.tokenStart(tp);
      // String token = getStandardTokenProductionText(tp);
      // gen.text(token);
      // gen.tokenEnd(tp);
      // }
    }
    gen.tokensEnd();
  }

  static String getStandardTokenProductionText(TokenProduction tp) {
    String token = "";
    if (tp.isExplicit()) {
      if (tp.getLexStates() == null) {
        token += "<*> ";
      } else {
        token += "<";
        for (int i = 0; i < tp.getLexStates().length; ++i) {
          token += tp.getLexStates()[i];
          if (i < (tp.getLexStates().length - 1)) {
            token += ",";
          }
        }
        token += "> ";
      }
      token += tp.getKind().name();
      if (tp.isIgnoreCase()) {
        token += " [IGNORE_CASE]";
      }
      token += " : {\n";
      for (Iterator<RegExprSpec> it2 = tp.getRespecs().iterator(); it2.hasNext();) {
        RegExprSpec res = it2.next();

        token += JJDoc.emitRE(res.rexp);

        if (res.nsTok != null) {
          token += " : " + res.nsTok.image;
        }

        token += "\n";
        if (it2.hasNext()) {
          token += "| ";
        }
      }
      token += "}\n\n";
    }
    return token;
  }

  private static void emitNormalProductions(Generator gen, Iterable<NormalProduction> prods) {
    gen.nonterminalsStart();
    for (NormalProduction np : prods) {
      JJDoc.emitTopLevelSpecialTokens(np.getFirstToken(), gen);
      if (np instanceof BNFProduction) {
        gen.productionStart(np);
        if (np.getExpansion() instanceof Choice) {
          boolean first = true;
          Choice c = (Choice) np.getExpansion();
          for (Object element : c.getChoices()) {
            Expansion e = (Expansion) (element);
            gen.expansionStart(e, first);
            JJDoc.emitExpansionTree(e, gen);
            gen.expansionEnd(e, first);
            first = false;
          }
        } else {
          gen.expansionStart(np.getExpansion(), true);
          JJDoc.emitExpansionTree(np.getExpansion(), gen);
          gen.expansionEnd(np.getExpansion(), true);
        }
        gen.productionEnd(np);
      }
    }
    gen.nonterminalsEnd();
  }

  private static void emitExpansionTree(Expansion exp, Generator gen) {
    // gen.text("[->" + exp.getClass().getName() + "]");
    if (exp instanceof Action) {
      JJDoc.emitExpansionAction((Action) exp, gen);
    } else if (exp instanceof Choice) {
      JJDoc.emitExpansionChoice((Choice) exp, gen);
    } else if (exp instanceof Lookahead) {
      JJDoc.emitExpansionLookahead((Lookahead) exp, gen);
    } else if (exp instanceof NonTerminal) {
      JJDoc.emitExpansionNonTerminal((NonTerminal) exp, gen);
    } else if (exp instanceof OneOrMore) {
      JJDoc.emitExpansionOneOrMore((OneOrMore) exp, gen);
    } else if (exp instanceof RegularExpression) {
      JJDoc.emitExpansionRegularExpression((RegularExpression) exp, gen);
    } else if (exp instanceof Sequence) {
      JJDoc.emitExpansionSequence((Sequence) exp, gen);
    } else if (exp instanceof ZeroOrMore) {
      JJDoc.emitExpansionZeroOrMore((ZeroOrMore) exp, gen);
    } else if (exp instanceof ZeroOrOne) {
      JJDoc.emitExpansionZeroOrOne((ZeroOrOne) exp, gen);
    } else {
      JJDocGlobals.error("Oops: Unknown expansion type.");
    }
    // gen.text("[<-" + exp.getClass().getName() + "]");
  }

  private static void emitExpansionAction(Action a, Generator gen) {}

  private static void emitExpansionChoice(Choice c, Generator gen) {
    for (Iterator<Expansion> it = c.getChoices().iterator(); it.hasNext();) {
      Expansion e = it.next();
      JJDoc.emitExpansionTree(e, gen);
      if (it.hasNext()) {
        gen.text(" | ");
      }
    }
  }

  private static void emitExpansionLookahead(Lookahead l, Generator gen) {}

  private static void emitExpansionNonTerminal(NonTerminal nt, Generator gen) {
    gen.nonTerminalStart(nt);
    gen.text(nt.getName());
    gen.nonTerminalEnd(nt);
  }

  private static void emitExpansionOneOrMore(OneOrMore o, Generator gen) {
    gen.text("( ");
    JJDoc.emitExpansionTree(o.getExpansion(), gen);
    gen.text(" )+");
  }

  private static void emitExpansionRegularExpression(RegularExpression r, Generator gen) {
    String reRendered = JJDoc.emitRE(r);
    if (!reRendered.equals("")) {
      gen.reStart(r);
      gen.text(reRendered);
      gen.reEnd(r);
    }
  }

  private static void emitExpansionSequence(Sequence s, Generator gen) {
    boolean firstUnit = true;
    for (Object unit : s.getUnits()) {
      Expansion e = (Expansion) unit;
      if ((e instanceof Lookahead) || (e instanceof Action)) {
        continue;
      }
      if (!firstUnit) {
        gen.text(" ");
      }
      boolean needParens = (e instanceof Choice) || (e instanceof Sequence);
      if (needParens) {
        gen.text("( ");
      }
      JJDoc.emitExpansionTree(e, gen);
      if (needParens) {
        gen.text(" )");
      }
      firstUnit = false;
    }
  }

  private static void emitExpansionZeroOrMore(ZeroOrMore z, Generator gen) {
    gen.text("( ");
    JJDoc.emitExpansionTree(z.getExpansion(), gen);
    gen.text(" )*");
  }

  private static void emitExpansionZeroOrOne(ZeroOrOne z, Generator gen) {
    gen.text("( ");
    JJDoc.emitExpansionTree(z.getExpansion(), gen);
    gen.text(" )?");
  }

  static String emitRE(RegularExpression re) {
    String returnString = "";
    boolean hasLabel = !re.getLabel().equals("");
    boolean justName = re instanceof RJustName;
    boolean eof = re instanceof REndOfFile;
    boolean isString = re instanceof RStringLiteral;
    boolean toplevelRE = (re.getTpContext() != null);
    boolean needBrackets = justName || eof || hasLabel || (!isString && toplevelRE);
    if (needBrackets) {
      returnString += "<";
      if (!justName) {
        if (re.isPrivateExp()) {
          returnString += "#";
        }
        if (hasLabel) {
          returnString += re.getLabel();
          returnString += ": ";
        }
      }
    }
    if (re instanceof RCharacterList) {
      RCharacterList cl = (RCharacterList) re;
      if (cl.isNegated_list()) {
        returnString += "~";
      }
      returnString += "[";
      for (Iterator<Object> it = cl.getDescriptors().iterator(); it.hasNext();) {
        Object o = it.next();
        if (o instanceof SingleCharacter) {
          returnString += "\"";
          char s[] = { ((SingleCharacter) o).ch };
          returnString += Encoding.escape(new String(s));
          returnString += "\"";
        } else if (o instanceof CharacterRange) {
          returnString += "\"";
          char s[] = { ((CharacterRange) o).getLeft() };
          returnString += Encoding.escape(new String(s));
          returnString += "\"-\"";
          s[0] = ((CharacterRange) o).getRight();
          returnString += Encoding.escape(new String(s));
          returnString += "\"";
        } else {
          JJDocGlobals.error("Oops: unknown character list element type.");
        }
        if (it.hasNext()) {
          returnString += ",";
        }
      }
      returnString += "]";
    } else if (re instanceof RChoice) {
      RChoice c = (RChoice) re;
      for (Iterator<RegularExpression> it = c.getChoices().iterator(); it.hasNext();) {
        RegularExpression sub = it.next();
        returnString += JJDoc.emitRE(sub);
        if (it.hasNext()) {
          returnString += " | ";
        }
      }
    } else if (re instanceof REndOfFile) {
      returnString += "EOF";
    } else if (re instanceof RJustName) {
      RJustName jn = (RJustName) re;
      returnString += jn.getLabel();
    } else if (re instanceof ROneOrMore) {
      ROneOrMore om = (ROneOrMore) re;
      returnString += "(";
      returnString += JJDoc.emitRE(om.getRegexpr());
      returnString += ")+";
    } else if (re instanceof RSequence) {
      RSequence s = (RSequence) re;
      for (Iterator<RegularExpression> it = s.getUnits().iterator(); it.hasNext();) {
        RegularExpression sub = it.next();
        boolean needParens = false;
        if (sub instanceof RChoice) {
          needParens = true;
        }
        if (needParens) {
          returnString += "(";
        }
        returnString += JJDoc.emitRE(sub);
        if (needParens) {
          returnString += ")";
        }
        if (it.hasNext()) {
          returnString += " ";
        }
      }
    } else if (re instanceof RStringLiteral) {
      RStringLiteral sl = (RStringLiteral) re;
      returnString += ("\"" + Encoding.escape(sl.getImage()) + "\"");
    } else if (re instanceof RZeroOrMore) {
      RZeroOrMore zm = (RZeroOrMore) re;
      returnString += "(";
      returnString += JJDoc.emitRE(zm.getRegexpr());
      returnString += ")*";
    } else if (re instanceof RZeroOrOne) {
      RZeroOrOne zo = (RZeroOrOne) re;
      returnString += "(";
      returnString += JJDoc.emitRE(zo.getRegexpr());
      returnString += ")?";
    } else if (re instanceof RRepetitionRange) {
      RRepetitionRange zo = (RRepetitionRange) re;
      returnString += "(";
      returnString += JJDoc.emitRE(zo.getRegexpr());
      returnString += ")";
      returnString += "{";
      if (zo.hasMax()) {
        returnString += zo.getMin();
        returnString += ",";
        returnString += zo.getMax();
      } else {
        returnString += zo.getMin();
      }
      returnString += "}";
    } else {
      JJDocGlobals.error("Oops: Unknown regular expression type.");
    }
    if (needBrackets) {
      returnString += ">";
    }
    return returnString;
  }
}
