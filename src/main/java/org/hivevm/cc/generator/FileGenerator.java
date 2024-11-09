// Copyright 2024 HiveVM.ORG. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.cc.generator;

import org.hivevm.cc.JavaCCRequest;
import org.hivevm.cc.parser.ParseException;

/**
 * The {@link FileGenerator} class.
 */
public interface FileGenerator {

  void handleRequest(JavaCCRequest request, LexerData context) throws ParseException;
}
