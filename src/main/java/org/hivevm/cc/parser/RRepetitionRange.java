// Copyright 2024 HiveVM.ORG. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.cc.parser;

/**
 * Describes one-or-more regular expressions (<foo+>).
 */

public class RRepetitionRange extends RegularExpression {

  /**
   * The regular expression which is repeated one or more times.
   */
  private RegularExpression regexpr;
  private int               min = 0;
  private int               max = -1;
  private boolean           hasMax;

  @Override
  public final <R, D> R accept(RegularExpressionVisitor<R, D> visitor, D data) {
    return visitor.visit(this, data);
  }


  /**
   * Gets the {@link #min}.
   */
  public final int getMin() {
    return this.min;
  }


  /**
   * Gets the {@link #max}.
   */
  public final int getMax() {
    return this.max;
  }


  /**
   * Gets the {@link #hasMax}.
   */
  public final boolean hasMax() {
    return this.hasMax;
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
  final void setRegexpr(RegularExpression regexpr, int min, int max, boolean hasMax) {
    this.regexpr = regexpr;
    this.min = min;
    this.max = max;
    this.hasMax = hasMax;
  }
}
