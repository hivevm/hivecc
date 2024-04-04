/*
 * Copyright (c) 2001-2022 Territorium Online Srl / TOL GmbH. All Rights Reserved.
 *
 * This file contains Original Code and/or Modifications of Original Code as defined in and that are
 * subject to the Territorium Online License Version 1.0. You may not use this file except in
 * compliance with the License. Please obtain a copy of the License at http://www.tol.info/license/
 * and read it before using this file.
 *
 * The Original Code and all software distributed under the License are distributed on an 'AS IS'
 * basis, WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESS OR IMPLIED, AND TERRITORIUM ONLINE HEREBY
 * DISCLAIMS ALL SUCH WARRANTIES, INCLUDING WITHOUT LIMITATION, ANY WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, QUIET ENJOYMENT OR NON-INFRINGEMENT. Please see the License for
 * the specific language governing rights and limitations under the License.
 */

package it.smartio.fastcc.utils;

import java.util.List;
import java.util.function.Function;

/**
 * The {@link TemplateFunction} class.
 */
public class TemplateFunction<T> {

  final Iterable<T>               values;
  final List<Function<T, String>> functions;

  /**
   * Constructs an instance of {@link TemplateFunction}.
   *
   */
  TemplateFunction(Iterable<T> values, List<Function<T, String>> functions) {
    this.values = values;
    this.functions = functions;
  }

  public final TemplateFunction<T> add(Function<T, String> function) {
    this.functions.add(function);
    return this;
  }
}
