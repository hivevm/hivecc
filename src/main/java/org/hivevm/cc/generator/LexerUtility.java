// Copyright 2024 HiveVM.ORG. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.cc.generator;

import org.hivevm.cc.parser.JavaCCErrors;
import org.hivevm.cc.parser.RChoice;
import org.hivevm.cc.parser.RegularExpression;

/**
 * The {@link LexerUtility} class.
 */
interface LexerUtility {

  static void CheckUnmatchability(RChoice choice, LexerData data) {
    for (RegularExpression regexp : choice.getChoices()) {
      if (!regexp.isPrivateExp() && (// curRE instanceof RJustName &&
      regexp.getOrdinal() > 0) && (regexp.getOrdinal() < choice.getOrdinal())
          && (data.getState(regexp.getOrdinal()) == data.getState(choice.getOrdinal()))) {
        if (choice.getLabel() != null) {
          JavaCCErrors.warning(choice,
              "Regular Expression choice : " + regexp.getLabel() + " can never be matched as : " + choice.getLabel());
        } else {
          JavaCCErrors.warning(choice, "Regular Expression choice : " + regexp.getLabel()
              + " can never be matched as token of kind : " + choice.getOrdinal());
        }
      }
    }
  }
}
