// Copyright 2024 HiveVM.ORG. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.cc.parser;

/**
 * Describes zero-or-one expansions (e.g., [foo], foo?).
 */

public class ZeroOrOne extends Expansion {

  /**
   * The expansion which is repeated zero or one times.
   */
  private Expansion expansion;

  public ZeroOrOne() {}

  ZeroOrOne(Token t, Expansion e) {
    setLocation(t);
    this.expansion = e;
    e.parent = this;
  }

  /**
   * Gets the {@link Expansionn}.
   */
  public final Expansion getExpansion() {
    return this.expansion;
  }
}
