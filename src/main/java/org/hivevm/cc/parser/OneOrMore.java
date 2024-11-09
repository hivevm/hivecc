// Copyright 2024 HiveVM.ORG. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.cc.parser;

/**
 * Describes one-or-more expansions (e.g., foo+).
 */

public class OneOrMore extends Expansion {

  /**
   * The expansion which is repeated one or more times.
   */
  private Expansion expansion;

  public OneOrMore() {}

  OneOrMore(Token t, Expansion e) {
    setLocation(t);
    this.expansion = e;
    this.expansion.parent = this;
  }

  /**
   * Gets the {@link Expansionn}.
   */
  public final Expansion getExpansion() {
    return this.expansion;
  }
}
