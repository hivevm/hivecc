// Copyright 2024 HiveVM.ORG. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.cc.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Describes expansions where one of many choices is taken (c1|c2|...).
 */

public class Choice extends Expansion {

  /**
   * The list of choices of this expansion unit. Each List component will narrow to ExpansionUnit.
   */
  private final List<Expansion> choices = new ArrayList<>();

  public Choice() {}

  Choice(Token token) {
    setLocation(token);
  }

  Choice(Expansion expansion) {
    setLocation(expansion);
    getChoices().add(expansion);
  }

  /**
   * @return the choices
   */
  public List<Expansion> getChoices() {
    return this.choices;
  }
}
