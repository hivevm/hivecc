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

import it.smartio.fastcc.parser.JavaCCErrors;
import it.smartio.fastcc.parser.RChoice;
import it.smartio.fastcc.parser.RegularExpression;

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
