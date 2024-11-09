// Copyright 2024 HiveVM.ORG. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.cc.parser;

/**
 * Describes single character descriptors in a character list.
 */

public class SingleCharacter {

  /**
   * The line and column number of the construct that corresponds most closely to this node.
   */
  private int column;

  private int line;

  /**
   * The character of this descriptor.
   */
  public char ch;

  SingleCharacter() {}

  SingleCharacter(char c) {
    this.ch = c;
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
}
