// Copyright 2024 HiveVM.ORG. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.cc.parser;

/**
 * Describes zero-or-more expansions (e.g., foo*).
 */

public class ZeroOrMore extends Expansion {

  /**
   * The expansion which is repeated zero or more times.
   */
  private Expansion expansion;

  public ZeroOrMore() {}

  ZeroOrMore(Token t, Expansion expansion) {
    setLocation(t);
    this.expansion = expansion;
    this.expansion.parent = this;
  }

  /**
   * Gets the {@link Expansionn}.
   */
  public final Expansion getExpansion() {
    return this.expansion;
  }
}
