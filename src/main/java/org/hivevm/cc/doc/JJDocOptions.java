// Copyright 2024 HiveVM.ORG. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.cc.doc;

import org.hivevm.cc.parser.Options;

/**
 * The options, specific to JJDoc.
 *
 * @author Kees Jan Koster &lt;kjkoster@kjkoster.org&gt;
 */
class JJDocOptions extends Options {

  /**
   * Limit subclassing to derived classes.
   */
  public JJDocOptions() {
    Options.optionValues.put("ONE_TABLE", Boolean.TRUE);
    Options.optionValues.put("TEXT", Boolean.FALSE);
    Options.optionValues.put("XTEXT", Boolean.FALSE);
    Options.optionValues.put("BNF", Boolean.FALSE);

    Options.optionValues.put("OUTPUT_FILE", "");
    Options.optionValues.put("CSS", "");
  }

  /**
   * Find the one table value.
   *
   * @return The requested one table value.
   */
  boolean getOneTable() {
    return booleanValue("ONE_TABLE");
  }

  /**
   * Find the CSS value.
   *
   * @return The requested CSS value.
   */
  String getCSS() {
    return stringValue("CSS");
  }

  /**
   * Find the text value.
   *
   * @return The requested text value.
   */
  boolean getText() {
    return booleanValue("TEXT");
  }

  boolean getXText() {
    return booleanValue("XTEXT");
  }

  /**
   * Find the BNF value.
   *
   * @return The requested text value.
   */
  boolean getBNF() {
    return booleanValue("BNF");
  }

  /**
   * Find the output file value.
   *
   * @return The requested output value.
   */
  String getOutputFile() {
    return stringValue("OUTPUT_FILE");
  }
}
