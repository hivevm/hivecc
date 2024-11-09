// Copyright 2024 HiveVM.ORG. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.cc.parser;

/**
 * Output error messages and keep track of totals.
 */
public final class JavaCCErrors {

  private static int parse_error_count    = 0;
  private static int semantic_error_count = 0;
  private static int warning_count        = 0;

  private JavaCCErrors() {}

  private static void printLocationInfo(Object node) {
    if (node instanceof NormalProduction) {
      NormalProduction n = (NormalProduction) node;
      System.err.print("Line " + n.getLine() + ", Column " + n.getColumn() + ": ");
    } else if (node instanceof TokenProduction) {
      TokenProduction n = (TokenProduction) node;
      System.err.print("Line " + n.getLine() + ", Column " + n.getColumn() + ": ");
    } else if (node instanceof Expansion) {
      Expansion n = (Expansion) node;
      System.err.print("Line " + n.getLine() + ", Column " + n.getColumn() + ": ");
    } else if (node instanceof CharacterRange) {
      CharacterRange n = (CharacterRange) node;
      System.err.print("Line " + n.getLine() + ", Column " + n.getColumn() + ": ");
    } else if (node instanceof SingleCharacter) {
      SingleCharacter n = (SingleCharacter) node;
      System.err.print("Line " + n.getLine() + ", Column " + n.getColumn() + ": ");
    } else if (node instanceof Token) {
      Token t = (Token) node;
      System.err.print("Line " + t.beginLine + ", Column " + t.beginColumn + ": ");
    }
  }

  public static void parse_error(Object node, String mess) {
    System.err.print("Error: ");
    JavaCCErrors.printLocationInfo(node);
    System.err.println(mess);
    JavaCCErrors.parse_error_count++;
  }

  public static void semantic_error(Object node, String mess) {
    System.err.print("Error: ");
    JavaCCErrors.printLocationInfo(node);
    System.err.println(mess);
    JavaCCErrors.semantic_error_count++;
  }

  public static void semantic_error(String mess) {
    System.err.print("Error: ");
    System.err.println(mess);
    JavaCCErrors.semantic_error_count++;
  }

  public static void warning(Object node, String mess) {
    System.err.print("Warning: ");
    JavaCCErrors.printLocationInfo(node);
    System.err.println(mess);
    JavaCCErrors.warning_count++;
  }

  public static void warning(String mess) {
    System.err.print("Warning: ");
    System.err.println(mess);
    JavaCCErrors.warning_count++;
  }

  public static boolean hasWarning() {
    return JavaCCErrors.warning_count > 0;
  }

  public static boolean hasError() {
    return (JavaCCErrors.parse_error_count + JavaCCErrors.semantic_error_count) > 0;
  }

  public static int get_warning_count() {
    return JavaCCErrors.warning_count;
  }

  public static int get_error_count() {
    return JavaCCErrors.parse_error_count + JavaCCErrors.semantic_error_count;
  }

  public static void reInit() {
    JavaCCErrors.parse_error_count = 0;
    JavaCCErrors.semantic_error_count = 0;
    JavaCCErrors.warning_count = 0;
  }

  public static void fatal(String message) {
    System.err.println("Fatal Error: " + message);
    throw new RuntimeException("Fatal Error: " + message);
  }
}
