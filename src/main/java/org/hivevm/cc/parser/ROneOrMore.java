// Copyright 2024 HiveVM.ORG. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.cc.parser;

/**
 * Describes one-or-more regular expressions (<foo+>).
 */

public class ROneOrMore extends RegularExpression {

  /**
   * The regular expression which is repeated one or more times.
   */
  private RegularExpression regexpr;

  public ROneOrMore() {}

  ROneOrMore(Token t, RegularExpression re) {
    setLocation(t);
    this.regexpr = re;
  }

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
