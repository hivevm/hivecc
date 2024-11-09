// Copyright 2024 HiveVM.ORG. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.cc.parser;

/**
 * Describes regular expressions which are referred to just by their name. This means that a regular
 * expression with this name has been declared earlier.
 */

public class RJustName extends RegularExpression {

  /**
   * "regexpr" points to the regular expression denoted by the name.
   */
  private RegularExpression regexpr;

  public RJustName() {}

  RJustName(Token t, String image) {
    setLocation(t);
    this.label = image;
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
