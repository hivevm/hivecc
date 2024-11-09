// Copyright 2024 HiveVM.ORG. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.cc.parser;

/**
 * Describes character range descriptors in a character list.
 */

public class CharacterRange {

  /**
   * The line and column number of the construct that corresponds most closely to this node.
   */
  private int  column;

  private int  line;

  /**
   * The leftmost and the rightmost characters in this character range.
   */
  private char right;

  private char left;

  CharacterRange() {}

  CharacterRange(char l, char r) {
    if (l > r) {
      JavaCCErrors.semantic_error(this, "Invalid range : \"" + (int) l + "\" - \"" + (int) r
          + "\". First character should be less than or equal to the second one in a range.");
    }

    setLeft(l);
    setRight(r);
  }

  /**
   * @return the line
   */
  int getLine() {
    return this.line;
  }

  /**
   * @return the column
   */
  int getColumn() {
    return this.column;
  }

  /**
   * @param column the column to set
   */
  void setLocation(Token token) {
    this.line = token.beginLine;
    this.column = token.beginColumn;
  }

  /**
   * @param left the left to set
   */
  public void setLeft(char left) {
    this.left = left;
  }

  /**
   * @return the left
   */
  public char getLeft() {
    return this.left;
  }

  /**
   * @param right the right to set
   */
  public void setRight(char right) {
    this.right = right;
  }

  /**
   * @return the right
   */
  public char getRight() {
    return this.right;
  }
}
