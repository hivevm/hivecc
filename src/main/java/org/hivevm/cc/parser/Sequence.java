// Copyright 2024 HiveVM.ORG. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.cc.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Describes expansions that are sequences of expansion units. (c1 c2 ...)
 */

public class Sequence extends Expansion {

  /**
   * The list of units in this expansion sequence. Each List component will narrow to Expansion.
   */
  private final List<? super Object> units = new ArrayList<>();

  public Sequence() {}

  Sequence(Token t, Lookahead lookahead) {
    setLocation(t);
    this.units.add(lookahead);
  }


  /**
   * Gets the {@link #units}.
   */
  public final List<? super Object> getUnits() {
    return this.units;
  }
}
