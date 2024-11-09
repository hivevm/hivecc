// Copyright 2024 HiveVM.ORG. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.cc.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Describes regular expressions which are sequences of other regular expressions.
 */

public class RSequence extends RegularExpression {

  /**
   * The list of units in this regular expression sequence. Each list component will narrow to
   * RegularExpression.
   */
  private List<RegularExpression> units = new ArrayList<>();

  RSequence() {}

  public RSequence(List<RegularExpression> seq) {
    this.ordinal = Integer.MAX_VALUE;
    this.units = seq;
  }

  @Override
  public final <R, D> R accept(RegularExpressionVisitor<R, D> visitor, D data) {
    return visitor.visit(this, data);
  }


  /**
   * Gets the {@link #units}.
   */
  public final List<RegularExpression> getUnits() {
    return this.units;
  }
}
