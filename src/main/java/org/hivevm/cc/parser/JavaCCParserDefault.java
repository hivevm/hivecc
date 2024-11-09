// Copyright 2024 HiveVM.ORG. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.cc.parser;


/**
 * The {@link JavaCCParserDefault} class.
 */
public class JavaCCParserDefault extends JavaCCParser {

  private final Options options;

  public JavaCCParserDefault(Provider stream, Options options) {
    super(stream);
    this.options = options;
  }

  /**
   * Gets the {@link #options}.
   */
  @Override
  public final Options getOptions() {
    return this.options;
  }
}
