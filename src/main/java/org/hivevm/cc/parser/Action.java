// Copyright 2024 HiveVM.ORG. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.cc.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Describes actions that may occur on the right hand side of productions.
 */

public class Action extends Expansion {

  /**
   * Contains the list of tokens that make up the action. This list does not include the surrounding
   * braces.
   */
  private final List<Token> action_tokens = new ArrayList<>();

  /**
   * @return the action_tokens
   */
  public List<Token> getActionTokens() {
    return this.action_tokens;
  }
}
