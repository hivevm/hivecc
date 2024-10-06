
package it.smartio.fastcc;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.smartio.fastcc.generator.LexerBuilder;
import it.smartio.fastcc.generator.LexerData;
import it.smartio.fastcc.lexer.NfaState;
import it.smartio.fastcc.parser.JavaCCData;
import it.smartio.fastcc.parser.JavaCCErrors;
import it.smartio.fastcc.parser.JavaCCParser;
import it.smartio.fastcc.parser.JavaCCParserDefault;
import it.smartio.fastcc.parser.Options;
import it.smartio.fastcc.parser.StringProvider;
import it.smartio.fastcc.parser.TokenProduction;
import it.smartio.fastcc.semantic.Semanticize;

public class FastCCInterpreter {

  private final Options options;


  /**
   * @param options
   */
  public FastCCInterpreter(Options options) {
    this.options = options;
  }

  public void runTokenizer(String grammar, String input) {
    JavaCCErrors.reInit();
    try {
      JavaCCData request = new JavaCCData(false, options);

      JavaCCParser parser = new JavaCCParserDefault(new StringProvider(grammar), options);
      parser.initialize(request);
      parser.javacc_input();

      // Semanticize.start();
      // LexGen lg = new LexGen();
      // lg.generateDataOnly = true;
      // lg.start();
      // LexerData td = LexGen.tokenizerData;

      Semanticize.semanticize(request, options);

      if (JavaCCErrors.get_error_count() == 0) {
        LexerData data = new LexerBuilder().build(request);
        tokenize(data, input, options);
      }
    } catch (ParseException e) {
      System.out.println("Detected " + JavaCCErrors.get_error_count() + " errors and "
          + JavaCCErrors.get_warning_count() + " warnings.");
      System.exit(1);
    } catch (Exception e) {
      System.out.println(e.toString());
      System.out.println("Detected " + (JavaCCErrors.get_error_count() + 1) + " errors and "
          + JavaCCErrors.get_warning_count() + " warnings.");
      System.exit(1);
    }
  }

  public static void tokenize(LexerData td, String input, Options options) {
    // First match the string literals.
    final int input_size = input.length();
    int curPos = 0;
    int curLexState = td.defaultLexState;
    Set<Integer> curStates = new HashSet<Integer>();
    Set<Integer> newStates = new HashSet<Integer>();
    while (curPos < input_size) {
      int beg = curPos;
      int matchedPos = beg;
      int matchedKind = Integer.MAX_VALUE;
      int nfaStartState = td.initialStates.get(curLexState);

      char c = input.charAt(curPos);
      if (options.getIgnoreCase())
        c = Character.toLowerCase(c);
      int key = curLexState << 16 | (int) c;
      final List<String> literals = td.literalSequence.get(key);
      if (literals != null) {
        // We need to go in order so that the longest match works.
        int litIndex = 0;
        for (String s : literals) {
          int index = 1;
          // See which literal matches.
          while (index < s.length() && curPos + index < input_size) {
            c = input.charAt(curPos + index);
            if (options.getIgnoreCase())
              c = Character.toLowerCase(c);
            if (c != s.charAt(index))
              break;
            index++;
          }
          if (index == s.length()) {
            // Found a string literal match.
            matchedKind = td.literalKinds.get(key).get(litIndex);
            matchedPos = curPos + index - 1;
            nfaStartState = td.kindToNfaStartState.get(matchedKind);
            curPos += index;
            break;
          }
          litIndex++;
        }
      }

      if (nfaStartState != -1) {
        // We need to add the composite states first.
        int kind = Integer.MAX_VALUE;
        curStates.add(nfaStartState);
        curStates.addAll(td.nfa.get(nfaStartState).compositeStates);
        do {
          c = input.charAt(curPos);
          if (options.getIgnoreCase())
            c = Character.toLowerCase(c);
          for (int state : curStates) {
            NfaState nfaState = td.nfa.get(state);
            if (nfaState.characters.contains(c)) {
              if (kind > nfaState.kind) {
                kind = nfaState.kind;
              }
              newStates.addAll(nfaState.nextStates);
            }
          }
          Set<Integer> tmp = newStates;
          newStates = curStates;
          curStates = tmp;
          newStates.clear();
          if (kind != Integer.MAX_VALUE) {
            matchedKind = kind;
            matchedPos = curPos;
            kind = Integer.MAX_VALUE;
          }
        } while (!curStates.isEmpty() && ++curPos < input_size);
      }
      if (matchedPos == beg && matchedKind > td.wildcardKind.get(curLexState)) {
        matchedKind = td.wildcardKind.get(curLexState);
      }
      if (matchedKind != Integer.MAX_VALUE) {
        TokenizerData.MatchInfo matchInfo = td.allMatches.get(matchedKind);
        if (matchInfo.action != null) {
          System.err.println("Actions not implemented (yet) in intererpreted mode");
        }
        if (matchInfo.matchType == TokenProduction.Kind.TOKEN) {
          System.err.println("Token: " + matchedKind + "; image: \"" + input.substring(beg, matchedPos + 1) + "\"");
        }
        if (matchInfo.newLexState != -1) {
          curLexState = matchInfo.newLexState;
        }
        curPos = matchedPos + 1;
      } else {
        System.err.println("Encountered token error at char: " + input.charAt(curPos));
        System.exit(1);
      }
    }
    System.err.println("Matched EOF");
  }
}
