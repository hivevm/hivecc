// Copyright 2024 HiveVM.ORG. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.cc;


/**
 * The {@link JJLanguage} class.
 */
public enum JJLanguage {

  None,
  Java,
  Cpp;

  public final String CODE;

  private JJLanguage() {
    this.CODE = "@" + name().toLowerCase();
  }

  public final String strip(String text) {
    return text.substring(name().length() + 1);
  }
}
