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

package it.smartio.fastcc;

import java.util.List;

import it.smartio.fastcc.parser.Action;
import it.smartio.fastcc.parser.NormalProduction;
import it.smartio.fastcc.parser.Options;
import it.smartio.fastcc.parser.RegularExpression;
import it.smartio.fastcc.parser.Token;
import it.smartio.fastcc.parser.TokenProduction;

/**
 * The {@link JavaCCRequest} class.
 */
public interface JavaCCRequest {

  boolean isGenerated();

  String getParserName();

  int getStateCount();

  int getTokenCount();

  Action getActionForEof();

  String getNextStateForEof();

  String getNameOfToken(int ordinal);

  List<Token> getTokens();

  Iterable<RegularExpression> getOrderedsTokens();

  Iterable<TokenProduction> getTokenProductions();

  Iterable<NormalProduction> getNormalProductions();

  NormalProduction getProductionTable(String name);

  List<Token> toInsertionPoint1();

  List<Token> toInsertionPoint2();

  List<Token> fromInsertionPoint2();

  default boolean ignoreCase() {
    return Options.getIgnoreCase();
  }
}
