// Copyright 2024 HiveVM.ORG. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.cc.utils;

/**
 * The {@link Environment} provides environment variables.
 */
public interface Environment {

  boolean isSet(String name);

  Object get(String name);

  void set(String name, Object value);
}
