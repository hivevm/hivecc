// Copyright 2024 HiveVM.ORG. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.cc.parser;

import java.util.HashSet;
import java.util.Set;


/**
 * Describes string literals.
 */

public class RStringLiteral extends RegularExpression {

  public final class KindInfo {

    public long[]              validKinds;
    public long[]              finalKinds;
    public int                 validKindCnt = 0;
    public int                 finalKindCnt = 0;
    private final Set<Integer> finalKindSet = new HashSet<>();
    private final Set<Integer> validKindSet = new HashSet<>();

    public KindInfo(int maxKind) {
      this.validKinds = new long[(maxKind / 64) + 1];
      this.finalKinds = new long[(maxKind / 64) + 1];
    }

    public void InsertValidKind(int kind) {
      this.validKinds[kind / 64] |= (1L << (kind % 64));
      this.validKindCnt++;
      this.validKindSet.add(kind);
    }

    public void InsertFinalKind(int kind) {
      this.finalKinds[kind / 64] |= (1L << (kind % 64));
      this.finalKindCnt++;
      this.finalKindSet.add(kind);
    }
  }

  /**
   * The string image of the literal.
   */
  private String image;

  public RStringLiteral() {}

  RStringLiteral(Token t, String image) {
    setLocation(t);
    this.image = image;
  }

  @Override
  public String toString() {
    return super.toString() + " - " + this.image;
  }

  @Override
  public final <R, D> R accept(RegularExpressionVisitor<R, D> visitor, D data) {
    return visitor.visit(this, data);
  }


  /**
   * Gets the {@link #image}.
   */
  public final String getImage() {
    return this.image;
  }


  /**
   * Sets the {@link #image}.
   */
  public final void setImage(String image) {
    this.image = image;
  }
}
