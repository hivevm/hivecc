// Copyright 2024 HiveVM.ORG. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.cc.parser;

/**
 * Describes zero-or-one regular expressions (<foo?>).
 */

public class RZeroOrOne extends RegularExpression {

  /**
   * The regular expression which is repeated zero or one times.
   */
  private RegularExpression regexpr;

  @Override
  public final <R, D> R accept(RegularExpressionVisitor<R, D> visitor, D data) {
    return visitor.visit(this, data);
  }

  /**
   * Gets the {@link #regexpr}.
   */
  public final RegularExpression getRegexpr() {
    return this.regexpr;
  }

  /**
   * Sets the {@link #regexpr}.
   */
  public final void setRegexpr(RegularExpression regexpr) {
    this.regexpr = regexpr;
  }
}
