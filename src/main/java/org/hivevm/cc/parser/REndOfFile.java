// Copyright 2024 HiveVM.ORG. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.cc.parser;

/**
 * Describes the EOF regular expression
 */

public class REndOfFile extends RegularExpression {

  @Override
  public final <R, D> R accept(RegularExpressionVisitor<R, D> visitor, D data) {
    return visitor.visit(this, data);
  }
}
